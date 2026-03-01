import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SolicitudGiraCreate {
  idAsignatura: number;
  idEmpresa: number;
  fechaInicio: string; // yyyy-MM-dd
  fechaFin: string;    // yyyy-MM-dd
  semana: string;
  cupoMaximo: number;
}

@Injectable({ providedIn: 'root' })
export class SolicitudGiraService {
  private api = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

    crearSolicitud(body: SolicitudGiraCreate): Observable<any> {
      return this.http.post(`${this.api}/docente/solicitudes`, body);
    }

  }
}
