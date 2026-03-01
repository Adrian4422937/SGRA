import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
/*import { Router, RouterLink } from '@angular/router';*/
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { environment } from '../../../environments/environment';
import { AuthService } from '../../auth/auth.service';

type UserDto = { id: number; nombreUsuario: string | null; roles: string[] };

@Component({

  selector: 'app-roles',
  standalone: true,
  /*imports: [CommonModule, RouterLink],*/
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './roles.component.html',
  styleUrls: ['./roles.component.css'],
})
export class RolesComponent implements OnInit {
  sidebarOpen: boolean = false;

  roles: string[] = ['ROLE_ADMIN', 'ROLE_USER'];
  users: UserDto[] = [];
  selectedUserId: number | null = null;
  selectedRoles = new Set<string>();
  msg = '';

  constructor(
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.http.get<string[]>(`${environment.apiUrl}/admin/roles`).subscribe({
      next: (r) => (this.roles = r && r.length ? r : this.roles),
      error: () => {},
    });

    this.http.get<UserDto[]>(`${environment.apiUrl}/admin/users`).subscribe({
      next: (r) => (this.users = r),
      error: () => (this.msg = 'No se pudo cargar usuarios.'),
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/auth/login']);

  }

  onSelectUser(idStr: string) {
    const id = Number(idStr);
    this.selectedUserId = id;
    const u = this.users.find(x => x.id === id);
    this.selectedRoles = new Set(u?.roles ?? []);
    this.msg = '';
  }

  toggle(role: string) {
    if (this.selectedRoles.has(role)) this.selectedRoles.delete(role);
    else this.selectedRoles.add(role);
  }

  save() {
    if (!this.selectedUserId) return;

    const roles = Array.from(this.selectedRoles);
    this.http
      .put<UserDto>(`${environment.apiUrl}/admin/users/${this.selectedUserId}/roles`, { roles })
      .subscribe({
        next: (u) => {
          const idx = this.users.findIndex(x => x.id === u.id);
          if (idx >= 0) this.users[idx] = u;
          this.msg = 'Roles guardados ';
        },
        error: () => (this.msg = 'No se pudo guardar roles.'),
      });
  }
}
