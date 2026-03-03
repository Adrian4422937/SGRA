import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';

type SolicitudEstadoItem = {
  idSolicitud?: number;
  id?: number;
  idAsignatura?: number;
  idEmpresa?: number;
  fechaSolicitud?: string;
  fechaInicio?: string;
  fechaFin?: string;
  semana?: string;
  cupoMaximo?: number;
  cupo_Maximo?: number;
  estadoActual?: string;
};

@Component({
  standalone: true,
  selector: 'app-estado-solicitudes',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './estado-solicitudes.component.html',
  styleUrls: ['./estado-solicitudes.component.css'],
})
export class EstadoSolicitudesComponent implements OnInit {
  private apiUrl = `${environment.apiUrl}/docente/estado-solicitudes`;

  loading = false;
  msg = '';

  q = '';
  estado = '';
  idAsignatura: number | null = null;

  items: SolicitudEstadoItem[] = [];
  filtered: SolicitudEstadoItem[] = [];

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      const id = params.get('idAsignatura');
      this.idAsignatura = id ? Number(id) : null;
      this.estado = params.get('estado') ?? '';
      this.load();
    });
  }

  load(): void {
    this.loading = true;
    this.msg = '';

    const params: any = {};
    if (this.idAsignatura != null && Number.isFinite(this.idAsignatura)) {
      params.idAsignatura = this.idAsignatura;
    }
    if ((this.estado ?? '').trim()) {
      params.estado = this.estado.trim();
    }

    this.http.get<SolicitudEstadoItem[]>(this.apiUrl, { params }).subscribe({
      next: (r) => {
        this.items = Array.isArray(r) ? r : [];
        this.applyFilter();
        this.loading = false;
        if (!this.items.length) this.msg = 'No hay solicitudes para los filtros seleccionados.';
      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        this.msg = (err?.status === 401 || err?.status === 403)
          ? 'No autorizado (revisa token / rol docente).'
          : 'No se pudo cargar el estado de solicitudes.';
      }
    });
  }

  applyFilter(): void {
    const q = (this.q ?? '').trim().toLowerCase();
    this.filtered = !q
      ? [...this.items]
      : this.items.filter(x => JSON.stringify(x).toLowerCase().includes(q));
  }

  aplicarFiltros(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        idAsignatura: this.idAsignatura ?? null,
        estado: (this.estado ?? '').trim() || null
      },
      queryParamsHandling: 'merge'
    });
  }

  limpiarFiltros(): void {
    this.q = '';
    this.estado = '';
    this.aplicarFiltros();
  }

  getEstadoClass(estado: string | null | undefined): string {
    const e = (estado || '').toUpperCase();
    if (e.includes('APROB')) return 'badge-estado aprobado';
    if (e.includes('RECHAZ')) return 'badge-estado rechazado';
    if (e.includes('OBSERV')) return 'badge-estado observado';
    return 'badge-estado pendiente';
  }

  volverMaterias(): void {
    this.router.navigateByUrl('/docente/mis-materias');
  }

  getId(s: SolicitudEstadoItem): number | string {
    return s.idSolicitud ?? s.id ?? '-';
  }

  getCupo(s: SolicitudEstadoItem): number | string {
    const v = (s.cupoMaximo ?? s.cupo_Maximo);
    return (v === null || v === undefined) ? '-' : v;
  }

  formatDate(value?: string): string {
    if (!value) return '-';
    // soporta "YYYY-MM-DD" o ISO
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return value;
    return d.toLocaleDateString('es-EC', { year: 'numeric', month: '2-digit', day: '2-digit' });
  }

  copiarId(s: SolicitudEstadoItem): void {
    const id = this.getId(s);
    if (!id || id === '-') return;
    navigator.clipboard?.writeText(String(id));
  }

  /** Ajusta la ruta si tu detalle tiene otro path */
  verDetalle(s: SolicitudEstadoItem): void {
    const id = this.getId(s);
    if (!id || id === '-') return;

    // Ejemplo: /docente/solicitud-detalle?idSolicitud=123
    this.router.navigate(['/docente/solicitud-detalle'], {
      queryParams: { idSolicitud: id }
    });
  }

  countEstado(fragment: string): number {
    const f = (fragment || '').toUpperCase();
    return (this.items || []).filter(x => ((x.estadoActual || '').toUpperCase().includes(f))).length;
  }

}
