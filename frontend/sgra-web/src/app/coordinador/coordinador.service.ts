import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';




export interface CoordinadorDashboardResumen {
  total: number;
  pendientes: number;
  aprobadas: number;
  rechazadas: number;
  observadas: number;
}

export interface CoordinadorSolicitudItem {
  idSolicitud: number;
  idDocente: number;
  idAsignatura: number;
  idEmpresa: number;
  fechaSolicitud: string;
  fechaInicio: string;
  fechaFin: string;
  semana: string;
  cupoMaximo: number;
  estadoActual: string;
}

export interface CoordinadorSolicitudDetalle {
  idSolicitud: number;
  idDocente: number;
  idAsignatura: number;
  idEmpresa: number;
  fechaSolicitud: string;
  fechaInicio: string;
  fechaFin: string;
  semana: string;
  cupoMaximo: number;
  estadoActual: string;

  docente?: string;
    asignatura?: string;
    empresa?: string;
}
export interface HistorialSolicitudItem {
  idHistorial: number;
  idSolicitud: number;
  idPersona: number | null;
  persona: string;
  accion: string;
  observacion: string;
  fecha: string; // ISO
}


export interface UsuarioLiteDto {
  id: number;
  nombres: string;
  apellidos: string;
  correo: string;
}

export interface AsignaturaLiteDto {
  id: number;
  nombre: string;
  creditos: number;
}

export interface PeriodoLiteDto {
  id: number;
  periodo: string;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}

export interface AsignarMateriasRequest {
  docenteId: number;
  periodoId: number;
  asignaturaIds: number[];
}


@Injectable({ providedIn: 'root' })
export class CoordinadorService {
  private api = `${environment.apiUrl}/coordinador`;

  constructor(private http: HttpClient) {}

  getResumen(): Observable<CoordinadorDashboardResumen> {
    return this.http.get<CoordinadorDashboardResumen>(`${this.api}/dashboard/resumen`);
  }

  getDocentes(): Observable<UsuarioLiteDto[]> {
      return this.http.get<UsuarioLiteDto[]>(`${this.api}/catalogo/docentes`);
    }

    getAsignaturas(): Observable<AsignaturaLiteDto[]> {
      return this.http.get<AsignaturaLiteDto[]>(`${this.api}/catalogo/asignaturas`);
    }

    getPeriodos(): Observable<PeriodoLiteDto[]> {
      return this.http.get<PeriodoLiteDto[]>(`${this.api}/catalogo/periodos`);
    }

    getAsignadas(docenteId: number, periodoId: number): Observable<AsignaturaLiteDto[]> {
      const params = new HttpParams().set('periodoId', String(periodoId));
      return this.http.get<AsignaturaLiteDto[]>(`${this.api}/docentes/${docenteId}/asignaturas`, { params });
    }

    asignarMaterias(req: AsignarMateriasRequest): Observable<void> {
      return this.http.post<void>(`${this.api}/asignar-materias`, req);
    }



  getSolicitudes(estado?: string): Observable<CoordinadorSolicitudItem[]> {
    let params = new HttpParams();
    if (estado && estado.trim()) {
      params = params.set('estado', estado.trim());
    }
    return this.http.get<CoordinadorSolicitudItem[]>(`${this.api}/solicitudes`, { params });
  }

  getSolicitudDetalle(id: number): Observable<CoordinadorSolicitudDetalle> {
    return this.http.get<CoordinadorSolicitudDetalle>(`${this.api}/solicitudes/${id}`);
  }

  aprobarSolicitud(id: number, observacion?: string): Observable<void> {
    return this.http.put<void>(`${this.api}/solicitudes/${id}/aprobar`, { observacion: observacion ?? '' });
  }

  rechazarSolicitud(id: number, observacion?: string): Observable<void> {
    return this.http.put<void>(`${this.api}/solicitudes/${id}/rechazar`, { observacion: observacion ?? '' });
  }

  listarDocumentosSolicitud(idSolicitud: number) {
    return this.http.get<any[]>(`${this.api}/solicitudes/${idSolicitud}/documentos`);
  }

  /*abrirDocumento(idSolicitud: number, idDocumento: number) {
    window.open(`${this.api}/solicitudes/${idSolicitud}/documentos/${idDocumento}/download`, '_blank');
  }*/

  descargarDocumentoPdf(idSolicitud: number, idDocumento: number) {
    // this.api ya es: `${environment.apiUrl}/coordinador`
    return this.http.get(
      `${this.api}/solicitudes/${idSolicitud}/documentos/${idDocumento}/download`,
      { responseType: 'blob' } //
    );
  }
  getHistorialSolicitud(idSolicitud: number): Observable<HistorialSolicitudItem[]> {
    return this.http.get<HistorialSolicitudItem[]>(
      `${this.api}/solicitudes/${idSolicitud}/historial`
    );
  }

}
