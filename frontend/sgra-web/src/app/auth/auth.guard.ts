import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

function normalizeRole(r: string): string {
  // limpia y mayus
  const x = (r ?? '').trim().toUpperCase();

  // si viene ROL_DOCENTE => ROLE_DOCENTE
  if (x.startsWith('ROL_')) return 'ROLE_' + x.substring(4);

  // si viene ROLE_ROLE_DOCENTE (doble) => ROLE_DOCENTE
  if (x.startsWith('ROLE_ROLE_')) return 'ROLE_' + x.substring('ROLE_ROLE_'.length);

  return x;
}

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) return true;

  auth.logout();
  router.navigateByUrl('/auth/login');
  return false;
};

export const roleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) {
    auth.logout();
    router.navigateByUrl('/auth/login');
    return false;
  }

  const allowedRaw = (route.data?.['roles'] ?? []) as string[];
  if (allowedRaw.length === 0) return true;

  const userRoles = auth.getRoles().map(normalizeRole);
  const allowed = allowedRaw.map(normalizeRole);

  console.log('ROLE GUARD =>', {
    logged: auth.isLoggedIn(),
    roles: userRoles,
    allowed,
  });

  const ok = userRoles.some(r => allowed.includes(r));
  if (ok) return true;

  router.navigateByUrl('/auth/login');
  return false;
};
