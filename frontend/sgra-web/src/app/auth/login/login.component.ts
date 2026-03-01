import { Component } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { finalize, timeout } from 'rxjs';
import { AuthService, AuthResponse } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loading = false;
  errorMsg = '';

  form = this.fb.group({
    nombreUsuario: ['', Validators.required],
    password: ['', Validators.required],
  });

  constructor(private fb: FormBuilder, private router: Router, private auth: AuthService) {}

  private normalizeRole(r: string): string {
    return (r ?? '').trim().toUpperCase();
  }

  submit(): void {
    if (this.form.invalid || this.loading) return;

    const { nombreUsuario, password } = this.form.getRawValue();
    this.loading = true;
    this.errorMsg = '';

    this.auth.logout();

    this.auth
      .login(nombreUsuario!, password!)
      .pipe(
        timeout(10000),
        finalize(() => (this.loading = false))
      )
      .subscribe({
        next: (res: AuthResponse) => {
          const normalizedRoles = (res.roles ?? []).map((r: string) => this.normalizeRole(r));

          // guarda roles normalizados también (opcional)
          // localStorage.setItem('roles', JSON.stringify(normalizedRoles));

          if (normalizedRoles.includes('ROLE_ADMIN')) {
            this.router.navigateByUrl('/admin/users');
            return;
          }
          if (normalizedRoles.includes('ROLE_COORDINADOR')) {
            this.router.navigateByUrl('/coordinador/dashboard');
            return;
          }
          if (normalizedRoles.includes('ROLE_DOCENTE')) {
            this.router.navigateByUrl('/docente/solicitud-gira');
            return;
          }

          this.errorMsg = 'Tu usuario no tiene un rol permitido.';
        },
        error: (err: any) => {
          console.error('LOGIN ERROR =>', err);
          this.errorMsg =
            err?.error?.message ??
            err?.message ??
            'No se pudo iniciar sesión (revisa consola y Network).';
        },
      });
  }
}
