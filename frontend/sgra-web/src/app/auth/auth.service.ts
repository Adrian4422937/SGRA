import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AuthResponse {
  token: string;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'token';
  private rolesKey = 'roles';

  // ✅ OJO: environment.apiUrl ya tiene /api
  private api = environment.apiUrl; // ej: http://localhost:8080/api

  constructor(private http: HttpClient) {}

  login(nombreUsuario: string, password: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.api}/auth/login`, { nombreUsuario, password })
      .pipe(
        tap((res: AuthResponse) => {
          localStorage.setItem(this.tokenKey, res.token);
          localStorage.setItem(this.rolesKey, JSON.stringify(res.roles ?? []));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.rolesKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getRoles(): string[] {
    const raw = localStorage.getItem(this.rolesKey);
    return raw ? (JSON.parse(raw) as string[]) : [];
  }

  hasAnyRole(allowed: string[]): boolean {
    const roles = this.getRoles();
    return roles.some(r => allowed.includes(r));
  }

  isAdmin(): boolean {
    return this.getRoles().includes('ROLE_ADMIN');
  }
}
