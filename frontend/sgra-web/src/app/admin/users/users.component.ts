
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormControl, FormRecord, ReactiveFormsModule, Validators,} from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../auth/auth.service';

type UserDto = {
  id: number;
  nombreUsuario: string | null;
  nombres: string;
  apellidos: string;
  correo: string;
  perfilUsuario: string;
  roles: string[];
};

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit {
   sidebarOpen: boolean = false;
  users: UserDto[] = [];
  roles: string[] = ['ROLE_ADMIN', 'ROLE_USER'];

  creating = false;
  errorMsg = '';
  successMsg = '';

  form = this.fb.group({
    nombreUsuario: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
    password: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
    nombres: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
    apellidos: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
    identificacion: this.fb.control('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(10), Validators.maxLength(10)],
    }),
    correo: this.fb.control('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    telefono: this.fb.control<string | null>(null),
    perfilUsuario: this.fb.control('', { nonNullable: true, validators: [Validators.required] }),
    idGenero: this.fb.control<number | null>(null),

    // ✅ roles dinámicos
    roles: new FormRecord<FormControl<boolean>>({}),
  });

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRoles();
    this.loadUsers();
  }

  // helper para usar en el HTML
  roleControl(role: string): FormControl<boolean> {
    return this.form.controls.roles.get(role) as FormControl<boolean>;
  }

  loadRoles() {
    this.http.get<string[]>(`${environment.apiUrl}/admin/roles`).subscribe({
      next: (r) => {
        // normaliza roles
        this.roles = (r && r.length ? r : this.roles).map(x => x.trim().toUpperCase());

        const rolesRecord = this.form.controls.roles; // FormRecord<FormControl<boolean>>
        const rr = rolesRecord as any; // ✅ evita el lío de typing en Angular

        // borrar anteriores
        Object.keys(rr.controls).forEach((k: string) => rr.removeControl(k));

        // crear nuevos (ROLE_USER true por defecto)
        for (const roleName of this.roles) {
          rr.addControl(
            roleName,
            new FormControl(roleName === 'ROLE_USER', { nonNullable: true })
          );
        }
      },
      error: () => {
        // si falla el endpoint, arma defaults igual
        const rolesRecord = this.form.controls.roles;
        const rr = rolesRecord as any;

        Object.keys(rr.controls).forEach((k: string) => rr.removeControl(k));

        for (const roleName of this.roles) {
          rr.addControl(
            roleName,
            new FormControl(roleName === 'ROLE_USER', { nonNullable: true })
          );
        }
      },
    });
  }

  loadUsers() {
    this.http.get<UserDto[]>(`${environment.apiUrl}/admin/users`).subscribe({
      next: (r) => (this.users = r),
      error: () => (this.errorMsg = 'No se pudo cargar la lista de usuarios (¿token/ROLE_ADMIN?).'),
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/auth/login']);

  }

  createUser() {
    if (this.form.invalid || this.creating) return;

    this.creating = true;
    this.errorMsg = '';
    this.successMsg = '';

    const v = this.form.getRawValue();

    const rolesSelected = Object.entries(this.form.controls.roles.getRawValue())
      .filter(([, checked]) => checked)
      .map(([role]) => role);

    const body = {
      nombres: v.nombres,
      apellidos: v.apellidos,
      identificacion: v.identificacion,
      correo: v.correo,
      perfilUsuario: v.perfilUsuario,

      telefono: v.telefono || null,
      idGenero: (v.idGenero && v.idGenero > 0) ? v.idGenero : null,

      nombreUsuario: v.nombreUsuario,
      password: v.password,
      roles: rolesSelected,
    };

    this.http.post(`${environment.apiUrl}/admin/users`, body).subscribe({
      next: () => {
        this.creating = false;
        this.successMsg = 'Usuario creado correctamente.';
        this.loadUsers();

        // reset básico
        this.form.reset({
          nombreUsuario: '',
          password: '',
          nombres: '',
          apellidos: '',
          identificacion: '',
          correo: '',
          telefono: null,
          perfilUsuario: '',
          idGenero: null,
        });

        // reset roles (ROLE_USER por defecto)
        for (const r of this.roles) {
          this.roleControl(r).setValue(r === 'ROLE_USER');
        }
      },
      error: (e) => {
        this.creating = false;

        const err = e?.error;
        this.errorMsg =
          typeof err === 'string' ? err :
          err?.message ? err.message :
          JSON.stringify(err ?? 'No se pudo crear el usuario.');

        // 👆 esto evita el [object Object]
      },
    });
  }

  toggleRole(user: UserDto, role: string) {
    const set = new Set(user.roles ?? []);
    if (set.has(role)) set.delete(role);
    else set.add(role);

    const roles = Array.from(set);

    this.http.put<UserDto>(`${environment.apiUrl}/admin/users/${user.id}/roles`, { roles }).subscribe({
      next: (updated) => (user.roles = updated.roles),
      error: () => (this.errorMsg = 'No se pudo actualizar roles.'),
    });
  }

  deleteUser(user: UserDto) {
    if (!confirm(`¿Eliminar usuario "${user.nombreUsuario}"?`)) return;

    this.http.delete(`${environment.apiUrl}/admin/users/${user.id}`).subscribe({
      next: () => (this.users = this.users.filter((u) => u.id !== user.id)),
      error: () => (this.errorMsg = 'No se pudo eliminar el usuario.'),
    });
  }
}
