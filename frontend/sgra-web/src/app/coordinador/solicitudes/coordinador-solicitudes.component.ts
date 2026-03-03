import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CoordinadorService, CoordinadorSolicitudItem } from '../coordinador.service';

@Component({
  selector: 'app-coordinador-solicitudes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './coordinador-solicitudes.component.html',
  styleUrls: ['./coordinador-solicitudes.component.css']
})
export class CoordinadorSolicitudesComponent implements OnInit {
  loading = false;
  errorMsg = '';
  estado = '';
  solicitudes: CoordinadorSolicitudItem[] = [];

  constructor(
    private coordinadorService: CoordinadorService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ✅ lee estado desde ?estado=... cuando vienes del dashboard
    this.route.queryParamMap.subscribe(params => {
      const e = (params.get('estado') || '').toUpperCase();
      this.estado = e;
      this.buscar(false); // false = no reescribe URL para evitar loop
    });
  }

  buscar(updateUrl = true): void {
    this.loading = true;
    this.errorMsg = '';

    if (updateUrl) {
      // ✅ mantiene el filtro en el URL (profesional)
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: { estado: this.estado || null },
        queryParamsHandling: 'merge'
      });
    }

    this.coordinadorService.getSolicitudes(this.estado || undefined).subscribe({
      next: (data) => {
        this.solicitudes = data ?? [];
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo cargar la bandeja de solicitudes.';
        this.loading = false;
      }
    });
  }

  limpiarFiltro(): void {
    this.estado = '';
    this.buscar(true);
  }

  badgeClass(est: string | null | undefined): string {
    const e = (est || '').toUpperCase();
    if (e.includes('APROB')) return 'ok';
    if (e.includes('RECHAZ')) return 'bad';
    if (e.includes('OBSERV')) return 'warn';
    return 'info'; // pendiente
  }

  formatDate(v: string | null | undefined): string {
    if (!v) return '-';
    // si te llega ISO (2026-03-03T...), recorta bonito
    return v.length >= 10 ? v.substring(0, 10) : v;
  }
}
