import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  CoordinadorService,
  UsuarioLiteDto,
  AsignaturaLiteDto,
  PeriodoLiteDto
} from '../coordinador.service';

@Component({
  selector: 'app-coordinador-asignar-materias',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './coordinador-asignar-materias.component.html',
  styleUrls: ['./coordinador-asignar-materias.component.css']
})
export class CoordinadorAsignarMateriasComponent implements OnInit {

  loading = false;
  saving = false;
  msg = '';
  errorMsg = '';

  docentes: UsuarioLiteDto[] = [];
  asignaturas: AsignaturaLiteDto[] = [];
  periodos: PeriodoLiteDto[] = [];

  docenteId?: number;
  periodoId?: number;

  selected = new Set<number>(); // ids de asignaturas seleccionadas

  constructor(private coordinadorService: CoordinadorService) {}

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos(): void {
    this.loading = true;
    this.errorMsg = '';
    this.msg = '';

    // carga en paralelo (simple)
    let ok = 0;
    const done = () => { ok++; if (ok === 3) this.loading = false; };

    this.coordinadorService.getDocentes().subscribe({
      next: d => { this.docentes = d; done(); },
      error: _ => { this.errorMsg = 'No se pudieron cargar los docentes.'; this.loading = false; }
    });

    this.coordinadorService.getAsignaturas().subscribe({
      next: a => { this.asignaturas = a; done(); },
      error: _ => { this.errorMsg = 'No se pudieron cargar las asignaturas.'; this.loading = false; }
    });

    this.coordinadorService.getPeriodos().subscribe({
      next: p => { this.periodos = p; done(); },
      error: _ => { this.errorMsg = 'No se pudieron cargar los periodos.'; this.loading = false; }
    });
  }

  onChangeDocentePeriodo(): void {
    this.msg = '';
    this.errorMsg = '';
    this.selected.clear();

    if (!this.docenteId || !this.periodoId) return;

    this.coordinadorService.getAsignadas(this.docenteId, this.periodoId).subscribe({
      next: (asig) => {
        asig.forEach(a => this.selected.add(a.id));
      },
      error: _ => {
        // no es crítico, solo no marcamos checks
      }
    });
  }

  toggleAsignatura(id: number, checked: boolean): void {
    if (checked) this.selected.add(id);
    else this.selected.delete(id);
  }

  guardar(): void {
    this.msg = '';
    this.errorMsg = '';

    if (!this.docenteId) { this.errorMsg = 'Selecciona un docente.'; return; }
    if (!this.periodoId) { this.errorMsg = 'Selecciona un periodo.'; return; }
    if (this.selected.size === 0) { this.errorMsg = 'Selecciona al menos una asignatura.'; return; }

    this.saving = true;

    this.coordinadorService.asignarMaterias({
      docenteId: this.docenteId,
      periodoId: this.periodoId,
      asignaturaIds: Array.from(this.selected)
    }).subscribe({
      next: () => {
        this.msg = '✅ Materias asignadas correctamente.';
        this.saving = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo guardar la asignación.';
        this.saving = false;
      }
    });
  }
}
