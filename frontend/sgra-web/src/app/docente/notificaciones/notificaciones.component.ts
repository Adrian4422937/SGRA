import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../environments/environment';

type NotificacionDto = {
  tipo: string;
  mensaje: string;
  leido: boolean;
  fecha: string; // LocalDateTime -> string en JSON
};

@Component({
  standalone: true,
  selector: 'app-notificaciones',
  imports: [CommonModule, FormsModule],
  templateUrl: './notificaciones.component.html',
  styleUrls: ['./notificaciones.component.css']
})
export class NotificacionesComponent implements OnInit {
  private apiUrl = `${environment.apiUrl}/docente/notificaciones`;

  loading = false;
  msg = '';
  soloNoLeidas = false;

  items: NotificacionDto[] = [];

  notiNoLeidas = 0;
  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.msg = '';

    let params = new HttpParams();
    if (this.soloNoLeidas) {
      params = params.set('noLeidas', 'true');
    }

    this.http.get<NotificacionDto[]>(this.apiUrl, { params }).subscribe({
      next: (res) => {
        this.items = Array.isArray(res) ? res : [];
        this.loading = false;

        this.notiNoLeidas = this.items.filter(x => !x.leido).length;
        if (!this.items.length) {
          this.msg = this.soloNoLeidas
            ? 'No tienes notificaciones no leídas.'
            : 'No tienes notificaciones.';
        }
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.items = []; // 👈 limpia datos viejos
        this.msg = (err?.status === 401 || err?.status === 403)
          ? 'No autorizado (revisa token / rol docente).'
          : 'No se pudieron cargar las notificaciones.';
      }
    });
  }

  marcarTodasLeidas(): void {
    this.http.put<{ updated: number }>(`${this.apiUrl}/marcar-leidas`, {}).subscribe({
      next: (res) => {
        const updated = res?.updated ?? 0;

        // mensaje
        this.msg = updated > 0
          ? `Se marcaron ${updated} notificaciones como leídas.`
          : 'No había notificaciones pendientes por marcar.';

        // recarga lista desde backend (mejor que solo cambiar local)
        this.load();

        // si tienes badge en este mismo componente/sidebar
        this.notiNoLeidas = 0;
      },
      error: (err) => {
        console.error(err);
        this.msg = 'No se pudieron marcar las notificaciones como leídas.';
      }
    });
  }


  badgeTipo(tipo?: string): string {
    const t = (tipo ?? '').toUpperCase();

    if (t.includes('APROB')) return 'ok';
    if (t.includes('RECHAZ')) return 'bad';
    if (t.includes('INFO')) return 'info';
    return 'muted';
  }

  fmtFecha(v?: string): string {
    if (!v) return '-';
    return v.replace('T', ' ').slice(0, 19);
  }
}
