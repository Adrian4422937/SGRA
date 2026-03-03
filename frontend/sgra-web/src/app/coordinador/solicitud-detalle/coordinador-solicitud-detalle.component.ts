import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CoordinadorService, CoordinadorSolicitudDetalle } from '../coordinador.service';

@Component({
  selector: 'app-coordinador-solicitud-detalle',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './coordinador-solicitud-detalle.component.html',
  styleUrls: ['./coordinador-solicitud-detalle.component.css']
})
export class CoordinadorSolicitudDetalleComponent implements OnInit {
  loading = false;
  saving = false;
  errorMsg = '';
  okMsg = '';
  mensajeAccion = '';
  tipoMensaje: 'ok' | 'error' | '' = '';

  idSolicitud!: number;
  detalle?: CoordinadorSolicitudDetalle;
  observacion = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private coordinadorService: CoordinadorService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.idSolicitud = Number(idParam);

    if (!this.idSolicitud || Number.isNaN(this.idSolicitud)) {
      this.errorMsg = 'ID de solicitud inválido.';
      return;
    }

    this.cargar();
  }

  cargar(): void {
    this.loading = true;
    this.errorMsg = '';
    this.okMsg = '';

    this.coordinadorService.getSolicitudDetalle(this.idSolicitud).subscribe({
      next: (data) => {
        this.detalle = data;
        this.loading = false;
      this.cargarDocumentos(this.detalle.idSolicitud);
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo cargar el detalle de la solicitud.';
        this.loading = false;
      }
    });
  }

  aprobar(): void {
    if (!this.detalle || this.saving) return;
    this.saving = true;
    this.errorMsg = '';
    this.okMsg = '';

    this.coordinadorService.aprobarSolicitud(this.idSolicitud, this.observacion).subscribe({
      next: () => {
        this.okMsg = 'Solicitud aprobada correctamente.';
        this.errorMsg = '';
        this.saving = false;

        // actualiza estado local sin recargar toda la página
        if (this.detalle) {
          this.detalle.estadoActual = 'APROBADA';
        }
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo aprobar la solicitud.';
        this.saving = false;
      }
    });
  }

  rechazar(): void {
    if (!this.detalle || this.saving) return;
    this.saving = true;
    this.errorMsg = '';
    this.okMsg = '';

    this.coordinadorService.rechazarSolicitud(this.idSolicitud, this.observacion).subscribe({
      next: () => {
        this.okMsg = 'Solicitud rechazada correctamente.';
        this.errorMsg = '';
        this.saving = false;

        if (this.detalle) {
          this.detalle.estadoActual = 'RECHAZADA';
        }
      },
      error: (err) => {
        console.error(err);
        this.errorMsg = 'No se pudo rechazar la solicitud.';
        this.saving = false;
      }
    });
  }

  volver(): void {
    this.router.navigateByUrl('/coordinador/solicitudes');
  }



  documentos: any[] = [];

  cargarDocumentos(idSolicitud: number) {
    this.coordinadorService.listarDocumentosSolicitud(idSolicitud).subscribe({
      next: (docs) => this.documentos = docs,
      error: (e) => console.error(e)
    });
  }

  abrirPdf(idSolicitud: number, idDocumento: number) {
    this.coordinadorService.descargarDocumentoPdf(idSolicitud, idDocumento).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        window.open(url, '_blank');

        // opcional: liberar memoria
        setTimeout(() => window.URL.revokeObjectURL(url), 10000);
      },
      error: (err) => {
        console.error(err);
        alert(err?.status === 401 ? 'No autorizado (token no enviado o expirado).' : 'No se pudo abrir el PDF.');
      }
    });
  }

  estadoBadge(estado: string | null | undefined): string {
    const e = (estado || '').toUpperCase();
    if (e.includes('APROB')) return 'ok';
    if (e.includes('RECHAZ')) return 'bad';
    if (e.includes('OBSERV')) return 'warn';
    return 'info'; // pendiente por defecto
  }
}
