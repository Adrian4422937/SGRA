import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Subscription, filter } from 'rxjs';
import { environment } from '../../environments/environment';

type Notificacion = {
  leida?: boolean;
  leido?: boolean;
  visto?: boolean;
  estado?: string; // ej: 'NO_LEIDA'
};

@Component({

  standalone: true,
  selector: 'app-docente-shell',
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './docente-shell.component.html',
  //styleUrls: ['./docente-shell.component.css'],
})
export class DocenteShellComponent implements OnInit, OnDestroy {
  sidebarOpen = false;
  notiNoLeidas = 0;

  private sub?: Subscription;
  private api = environment.apiUrl;

  constructor(private router: Router, private http: HttpClient) {}


  ngOnInit(): void {
    // 1) carga al entrar
    this.refreshNotiBadge();

    // 2) refresca cada vez que cambias de pantalla (navegación)
    this.sub = this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => this.refreshNotiBadge());
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  refreshNotiBadge(): void {
    // ✅ Intenta primero un endpoint de "conteo" (rápido)
    // Cambia este path si tu backend lo maneja distinto:
    const urlCount = `${this.api}/docente/notificaciones/no-leidas-count`;

    this.http.get<number>(urlCount).subscribe({
      next: (n) => (this.notiNoLeidas = Number(n) || 0),
      error: () => {
        // ✅ Fallback: si no existe el count, trae la lista y cuenta "no leídas"
        const urlList = `${this.api}/docente/notificaciones`;
        this.http.get<Notificacion[]>(urlList).subscribe({
          next: (list) => {
            const arr = Array.isArray(list) ? list : [];
            this.notiNoLeidas = arr.filter(x =>
              x.leida === false ||
              x.leido === false ||
              x.visto === false ||
              (x.estado || '').toUpperCase().includes('NO')
            ).length;
          },
          error: () => (this.notiNoLeidas = 0),
        });
      },
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('authToken');
    this.router.navigateByUrl('/auth/login');
  }
}
