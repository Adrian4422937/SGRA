import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
  FormGroup,
} from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../auth/auth.service'; // ajusta si tu ruta es otra

type Stats = {
  enviadas: number;
  aprobadas: number;
  pendientes: number;
  rechazadas: number;
};
type SolicitudEstadoItem = {
  idSolicitud: number;
  idAsignatura: number;
  idEmpresa: number;
  fechaSolicitud: string;
  fechaInicio: string;
  fechaFin: string;
  semana: string;
  cupoMaximo: number;
  estadoActual: string;
};
@Component({
  standalone: true,
  selector: 'app-solicitud-gira',
  imports: [CommonModule, ReactiveFormsModule, RouterLink, RouterLinkActive],
  templateUrl: './solicitud-gira.component.html',
  styleUrls: ['./solicitud-gira.component.css'],
})
export class SolicitudGiraComponent implements OnInit {
  sidebarOpen = false;

 stats: Stats = { enviadas: 0, aprobadas: 0, pendientes: 0, rechazadas: 0 };

  loading = false;
  msg = '';

  form!: FormGroup; // ✅ se crea en constructor

  solicitudes: SolicitudEstadoItem[] = [];
  loadingSolicitudes = false;

  private apiMisSolicitudesUrl = `${environment.apiUrl}/docente/solicitud-gira/mis-solicitudes`;
  private apiCreateUrl = `${environment.apiUrl}/docente/solicitud-gira`;
  private apiStatsUrl = `${environment.apiUrl}/docente/solicitud-gira/stats`;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private auth: AuthService
  ) {
    // ✅ aquí ya existe this.fb
    this.form = this.fb.group({
      idAsignatura: ['', [Validators.required]],
      idEmpresa: ['', [Validators.required]],
      fechaInicio: ['', [Validators.required]],
      fechaFin: ['', [Validators.required]],
      semana: ['', [Validators.required]],
      cupo_Maximo: [1, [Validators.required, Validators.min(1)]],
    });
  }

  ngOnInit(): void {
    this.cargarStats();
    this.cargarContadorNotificaciones();
  }

  private n(v: unknown): number | null {
    const x = Number(String(v ?? '').trim());
    return Number.isFinite(x) ? x : null;
  }

  guardar(): void {
    this.msg = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.msg = 'Completa los campos obligatorios.';
      return;
    }

    const v = this.form.value;

    const payload = {
      idAsignatura: this.n(v.idAsignatura),
      idEmpresa: this.n(v.idEmpresa),
      fechaInicio: v.fechaInicio,
      fechaFin: v.fechaFin,
      semana: v.semana,
      cupoMaximo: this.n(v.cupo_Maximo), // si tu backend usa cupo_Maximo, cámbialo
    };

    this.loading = true;

    this.http.post(this.apiCreateUrl, payload).subscribe({
      next: () => {
        this.loading = false;
        this.msg = 'Solicitud registrada correctamente.';
        this.form.reset({ cupo_Maximo: 1 });
        this.cargarStats();
      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        this.msg =
          err?.status === 401 || err?.status === 403
            ? 'No autorizado (revisa token / rol docente).'
            : 'Error al guardar la solicitud.';
      },
    });
  }

  private cargarStats(): void {
    this.http.get<Stats>(this.apiStatsUrl).subscribe({
      next: (r) => {
        this.stats = r ?? { enviadas: 0, aprobadas: 0, pendientes: 0, rechazadas: 0 };
      },
      error: () => {
        // silencioso si no existe endpoint
      },
    });
  }
  getEstadoClass(estado: string | null | undefined): string {
    const e = (estado || '').toUpperCase();

    if (e.includes('APROB')) return 'badge-estado aprobado';
    if (e.includes('RECHAZ')) return 'badge-estado rechazado';
    if (e.includes('OBSERV')) return 'badge-estado observado';
    return 'badge-estado pendiente';
  }
  /*private cargarMisSolicitudes(): void {
    this.loadingSolicitudes = true;

    this.http.get<SolicitudEstadoItem[]>(this.apiMisSolicitudesUrl).subscribe({
      next: (data) => {
        this.solicitudes = data ?? [];
        this.loadingSolicitudes = false;
      },
      error: (err) => {
        this.loadingSolicitudes = false;
        console.error('Error cargando mis solicitudes', err);
      }
    });
  }*/

  // ✅ Cerrar sesión (para tu botón del HTML)
  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/auth/login');
  }

  // ✅ Ir a materias (si tu menú "Asignaturas" es un botón/div)
  irMaterias(): void {
    this.router.navigateByUrl('/docente/mis-materias');
  }
  notiNoLeidas = 0;
  private apiNotiUrl = `${environment.apiUrl}/docente/notificaciones`;

  private cargarContadorNotificaciones(): void {
    this.http.get<any[]>(this.apiNotiUrl, {
      params: { noLeidas: true as any }
    }).subscribe({
      next: (r) => {
        this.notiNoLeidas = Array.isArray(r) ? r.length : 0;
      },
      error: () => {
        this.notiNoLeidas = 0;
      }
    });
  }
}
