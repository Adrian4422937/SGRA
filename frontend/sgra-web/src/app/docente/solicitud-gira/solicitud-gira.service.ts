import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface SolicitudGiraCreate {
  idAsignatura: number;
  idEmpresa: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;    // yyyy-MM-dd
  semana: string;
  cupoMaximo: number;
  tipoDocumento?: string;
}

@Injectable({ providedIn: 'root' })
export class SolicitudGiraService {
  private baseUrl = `${environment.apiUrl}/docente/solicitud-gira`;

  constructor(private http: HttpClient) {}

  // (opcional) crear normal JSON
  crearSolicitud(body: SolicitudGiraCreate): Observable<any> {
    return this.http.post(`${this.baseUrl}`, body);
  }

  // ✅ crear solicitud + PDF (multipart)
  crearSolicitudConPdf(data: SolicitudGiraCreate, pdfFile?: File): Observable<any> {
    const fd = new FormData();
    fd.append('idAsignatura', String(data.idAsignatura));
    fd.append('idEmpresa', String(data.idEmpresa));
    fd.append('fechaInicio', data.fechaInicio);
    fd.append('fechaFin', data.fechaFin);
    fd.append('semana', data.semana);

    // IMPORTANTE: en multipart el backend espera cupo_Maximo
    fd.append('cupo_Maximo', String(data.cupoMaximo));

    fd.append('tipoDocumento', data.tipoDocumento || 'PLAN_GIRA');

    if (pdfFile) {
      fd.append('file', pdfFile, pdfFile.name);
    }

    return this.http.post(`${this.baseUrl}/con-pdf`, fd);
  }
}
