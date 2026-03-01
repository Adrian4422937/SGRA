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
}

@Injectable({ providedIn: 'root' })
export class CoordinadorService {
  private api = `${environment.apiUrl}/coordinador`;

  constructor(private http: HttpClient) {}

  getResumen(): Observable<CoordinadorDashboardResumen> {
    return this.http.get<CoordinadorDashboardResumen>(`${this.api}/dashboard/resumen`);
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
}
