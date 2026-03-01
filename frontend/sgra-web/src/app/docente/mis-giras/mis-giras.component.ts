import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

type SolicitudGiraDto = {
  id?: number;
  idAsignatura?: number;
  idEmpresa?: number;
  fechaSolicitud?: string;
  fechaInicio?: string;
  fechaFin?: string;
  semana?: string;
  cupo_Maximo?: number; // backend actual
  cupoMaximo?: number;  // por compatibilidad
  estadoActual?: string;
};

@Component({
  standalone: true,
  selector: 'app-mis-giras',
  imports: [CommonModule, FormsModule],
  templateUrl: './mis-giras.component.html',
  styleUrls: ['./mis-giras.component.css']
})
export class MisGirasComponent implements OnInit {
  private apiUrl = `${environment.apiUrl}/docente/mis-giras`;

  loading = false;
  msg = '';
  q = '';
  estado = '';

  items: SolicitudGiraDto[] = [];
  filtered: SolicitudGiraDto[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.msg = '';

    const params: any = {};
    if (this.estado?.trim()) {
      params.estado = this.estado.trim();
    }

    this.http.get<SolicitudGiraDto[]>(this.apiUrl, { params }).subscribe({
      next: (res) => {
        this.items = Array.isArray(res) ? res : [];
        this.applyFilter();
        this.loading = false;

        if (!this.items.length) {
          this.msg = 'No tienes giras para mostrar.';
        }
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.msg = (err?.status === 401 || err?.status === 403)
          ? 'No autorizado (revisa token / rol docente).'
          : 'No se pudieron cargar tus giras.';
      }
    });
  }

  applyFilter(): void {
    const q = (this.q ?? '').trim().toLowerCase();

    if (!q) {
      this.filtered = [...this.items];
      return;
    }

    this.filtered = this.items.filter(x =>
      JSON.stringify(x).toLowerCase().includes(q)
    );
  }

  getCupo(x: SolicitudGiraDto): number | string {
    return x.cupo_Maximo ?? x.cupoMaximo ?? '-';
  }

  badgeClass(estado?: string): string {
    const e = (estado ?? '').trim().toUpperCase();

    if (e === 'APROBADA') return 'ok';
    if (e === 'EN_CURSO') return 'warn';
    if (e === 'FINALIZADA') return 'info';
    if (e === 'RECHAZADA') return 'bad';
    if (e === 'PENDIENTE') return 'pending';

    return 'muted';
  }

  fmtFecha(v?: string): string {
    if (!v) return '-';
    // si viene YYYY-MM-DD, lo dejamos así
    return v;
  }
}
