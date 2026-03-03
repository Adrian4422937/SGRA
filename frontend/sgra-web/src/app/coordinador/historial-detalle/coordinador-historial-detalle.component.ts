import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CoordinadorService, HistorialSolicitudItem, CoordinadorSolicitudDetalle } from '../coordinador.service';

@Component({
  selector: 'app-coordinador-historial-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './coordinador-historial-detalle.component.html',
  styleUrls: ['./coordinador-historial-detalle.component.css']
})
export class CoordinadorHistorialDetalleComponent implements OnInit {
  idSolicitud!: number;

  loading = false;
  errorMsg = '';

  detalle?: CoordinadorSolicitudDetalle;
  historial: HistorialSolicitudItem[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: CoordinadorService
  ) {}

  ngOnInit(): void {
    this.idSolicitud = Number(this.route.snapshot.paramMap.get('id'));
    if (!this.idSolicitud || Number.isNaN(this.idSolicitud)) {
      this.errorMsg = 'ID de solicitud inválido.';
      return;
    }
    this.cargar();
  }

  cargar(): void {
    this.loading = true;
    this.errorMsg = '';

    // detalle (para mostrar encabezado)
    this.service.getSolicitudDetalle(this.idSolicitud).subscribe({
      next: d => this.detalle = d,
      error: () => {}
    });

    // historial (timeline)
    this.service.getHistorialSolicitud(this.idSolicitud).subscribe({
      next: (h) => {
        this.historial = (h ?? []).slice().sort((a,b) => (a.fecha > b.fecha ? 1 : -1));
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo cargar el historial.';
        this.loading = false;
      }
    });
  }

  volver(): void {
    this.router.navigateByUrl('/coordinador/solicitudes');
  }

  accionLabel(a: string): string {
    const x = (a || '').toUpperCase();
    if (x.includes('CREACION')) return 'Solicitud creada por docente';
    if (x.includes('DOCUMENTO_SUBIDO')) return 'Documento adjuntado';
    if (x.includes('APROB')) return 'Aprobada por Coordinación';
    if (x.includes('RECHAZ')) return 'Rechazada por Coordinación';
    if (x.includes('OBSERV')) return 'Observada por Coordinación';
    return a;
  }

  fmtFecha(iso: string): string {
    if (!iso) return '-';
    // deja yyyy-mm-dd hh:mm
    return iso.replace('T', ' ').substring(0, 16);
  }
}
