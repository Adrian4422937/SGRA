import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, NavigationEnd } from '@angular/router'; // ✅ AÑADIR Router + NavigationEnd
import { filter } from 'rxjs/operators'; // ✅ AÑADIR filter
import { CoordinadorService, CoordinadorDashboardResumen } from '../coordinador.service';

@Component({
  selector: 'app-coordinador-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './coordinador-dashboard.component.html',
  styleUrls: ['./coordinador-dashboard.component.css']
})
export class CoordinadorDashboardComponent implements OnInit {

  loading = false;
  errorMsg = '';
  resumen?: CoordinadorDashboardResumen;

  enAsignar = false;

  constructor(
    private coordinadorService: CoordinadorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargar();

    const calc = () => {
      this.enAsignar = this.router.url.includes('/coordinador/dashboard/asignar-materias');
    };

    calc(); // ✅ inicial
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(calc);
  }

  cargar(): void {
    this.loading = true;
    this.errorMsg = '';

    this.coordinadorService.getResumen().subscribe({
      next: (data) => {
        this.resumen = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo cargar el resumen del coordinador.';
        this.loading = false;
      }
    });
  }

  pct(n: number): number {
    const t = this.resumen?.total ?? 0;
    if (!t || t <= 0) return 0;
    return Math.round((n * 100) / t);
  }
}
