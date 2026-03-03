import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent),
  },

  // ===================== COORDINADOR =====================
  {
    path: 'coordinador',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_COORDINADOR'] },
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./coordinador/dashboard/coordinador-dashboard.component')
            .then(m => m.CoordinadorDashboardComponent),

        // ✅ HIJO: se renderiza dentro del dashboard (manteniendo header+cards)
        children: [
          {
            path: 'asignar-materias',
            loadComponent: () =>
              import('./coordinador/asignar-materias/coordinador-asignar-materias.component')
                .then(m => m.CoordinadorAsignarMateriasComponent),
          }
        ]
      },

      // ✅ si alguien entra a /coordinador/asignar-materias, lo mando al dashboard hijo
      { path: 'asignar-materias', redirectTo: 'dashboard/asignar-materias', pathMatch: 'full' },

      {
        path: 'solicitudes',
        loadComponent: () =>
          import('./coordinador/solicitudes/coordinador-solicitudes.component')
            .then(m => m.CoordinadorSolicitudesComponent),
      },
      {
        path: 'solicitudes/:id',
        loadComponent: () =>
          import('./coordinador/solicitud-detalle/coordinador-solicitud-detalle.component')
            .then(m => m.CoordinadorSolicitudDetalleComponent),
      },

      {
        path: 'historial/:id',
        loadComponent: () =>
          import('./coordinador/historial-detalle/coordinador-historial-detalle.component')
            .then(m => m.CoordinadorHistorialDetalleComponent),
      },

      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ],
  },


  // ===================== DOCENTE (DASHBOARD FIJO) =====================
  {
    path: 'docente',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_DOCENTE'] },

    // ✅ aquí va tu dashboard fijo
    loadComponent: () =>
      import('./docente/docente-shell.component')
        .then(m => m.DocenteShellComponent),

    // ✅ aquí van las páginas dentro del dashboard
    children: [
      {
        path: 'solicitud-gira',
        loadComponent: () =>
          import('./docente/solicitud-gira/solicitud-gira.component')
            .then(m => m.SolicitudGiraComponent),
      },
      {
        path: 'estado-solicitudes',
        loadComponent: () =>
          import('./docente/estado-solicitudes/estado-solicitudes.component')
            .then(m => m.EstadoSolicitudesComponent),
      },
      {
        path: 'mis-materias',
        loadComponent: () =>
          import('./docente/mis-materias/mis-materias.component')
            .then(m => m.MisMateriasComponent),
      },
      {
        path: 'mis-giras',
        loadComponent: () =>
          import('./docente/mis-giras/mis-giras.component')
            .then(m => m.MisGirasComponent),
      },
      {
        path: 'notificaciones',
        loadComponent: () =>
          import('./docente/notificaciones/notificaciones.component')
            .then(m => m.NotificacionesComponent),
      },

      // ✅ como NO tienes "inicio", mejor manda a mis-materias
      { path: '', redirectTo: 'mis-materias', pathMatch: 'full' }
    ]
  },


  // ===================== ADMIN =====================
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_ADMIN'] },
    children: [
      {
        path: 'users',
        loadComponent: () =>
          import('./admin/users/users.component').then(m => m.UsersComponent),
      },
      {
        path: 'roles',
        loadComponent: () =>
          import('./admin/roles/roles.component').then(m => m.RolesComponent),
      },
      { path: '', redirectTo: 'users', pathMatch: 'full' }
    ],
  },

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' },
];
