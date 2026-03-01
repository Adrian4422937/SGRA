import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { environment } from '../../../environments/environment';

type MateriaAsignadaDto = {
  idAsignatura?: number;
  nombreAsignatura?: string;
  creditos?: number;
  periodo?: string;
  idMateria?: number;
  id?: number;
  nombre?: string;
  nombreMateria?: string;
  materia?: string;
  asignatura?: string;
};

type NotificacionDto = {
  tipo?: string;
  mensaje?: string;
  leido?: boolean;
  fecha?: string;
};

@Component({
  standalone: true,
  selector: 'app-mis-materias',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './mis-materias.component.html',
  styleUrls: ['./mis-materias.component.css'],
})
export class MisMateriasComponent implements OnInit {
  private apiUrl = `${environment.apiUrl}/docente/mis-materias`;
  private notiUrl = `${environment.apiUrl}/docente/notificaciones`;

  sidebarOpen = false;

  loading = false;
  msg = '';
  q = '';

  items: MateriaAsignadaDto[] = [];
  filtered: MateriaAsignadaDto[] = [];

  stats = { materias: 0, creditos: 0, periodo: '-' };

  notiNoLeidas = 0;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.load();
    this.cargarContadorNotificaciones();
  }

  getId(m: MateriaAsignadaDto): number | string {
    return m.idAsignatura ?? m.idMateria ?? m.id ?? '-';
  }

  // Helper para obtener número real (sin usar "as number" en HTML)
  getIdNumero(m: MateriaAsignadaDto): number | null {
    const id = m.idAsignatura ?? m.idMateria ?? m.id;
    return typeof id === 'number' ? id : null;
  }

  getNombre(m: MateriaAsignadaDto): string {
    return (
      m.nombreAsignatura ??
      m.nombreMateria ??
      m.asignatura ??
      m.materia ??
      m.nombre ??
      '(Sin nombre)'
    );
  }

  private recomputeStats(): void {
    this.stats.materias = this.items.length;
    this.stats.creditos = this.items.reduce((acc, x) => acc + (x.creditos ?? 0), 0);
    this.stats.periodo = this.items.find(x => (x.periodo ?? '').trim())?.periodo ?? '-';
  }

  applyFilter(): void {
    const q = (this.q ?? '').trim().toLowerCase();
    this.filtered = !q
      ? [...this.items]
      : this.items.filter(m => JSON.stringify(m).toLowerCase().includes(q));
  }

  load(): void {
    this.loading = true;
    this.msg = '';

    this.http.get<MateriaAsignadaDto[]>(this.apiUrl).subscribe({
      next: (r) => {
        this.items = Array.isArray(r) ? r : [];
        this.applyFilter();
        this.recomputeStats();
        this.loading = false;

        if (!this.items.length) {
          this.msg = 'No tienes materias asignadas todavía.';
        }
      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        this.msg = (err?.status === 401 || err?.status === 403)
          ? 'No autorizado (revisa token / rol docente).'
          : 'No se pudo cargar Asignaturas.';
      },
    });
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('access_token');
    localStorage.removeItem('jwt');
    localStorage.removeItem('auth');
    this.router.navigateByUrl('/auth/login');
  }

  irSolicitarGira(idAsignatura: number): void {
    this.router.navigate(['/docente/solicitud-gira'], { queryParams: { idAsignatura } });
  }

  irVerEstados(idAsignatura: number): void {
    this.router.navigate(['/docente/estado-solicitudes'], { queryParams: { idAsignatura } });
  }

  // Wrappers para usar desde HTML (sin cast)
  goSolicitar(m: MateriaAsignadaDto): void {
    const id = this.getIdNumero(m);
    if (id == null) return;
    this.irSolicitarGira(id);
  }

  goVerEstados(m: MateriaAsignadaDto): void {
    const id = this.getIdNumero(m);
    if (id == null) return;
    this.irVerEstados(id);
  }

  cargarContadorNotificaciones(): void {
    this.http.get<NotificacionDto[]>(this.notiUrl, {
      params: { noLeidas: 'true' }
    }).subscribe({
      next: (r) => {
        this.notiNoLeidas = Array.isArray(r) ? r.length : 0;
      },
      error: (err) => {
        console.warn('No se pudo cargar contador de notificaciones', err);
        this.notiNoLeidas = 0;
      }
    });
  }
}
