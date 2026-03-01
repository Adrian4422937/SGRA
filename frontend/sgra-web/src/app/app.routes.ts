import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: 'auth/login',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent),
  },

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
      },
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
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      }
    ],
  },

  {
    path: 'docente',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ROLE_DOCENTE'] },
    children: [
      {
        path: 'inicio',
        // ✅ usa el componente que sí tienes
        loadComponent: () =>
          import('./docente/docente.component')
            .then(m => m.DocenteComponent),
      },
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

      // ❌ COMENTA estas rutas hasta que crees los componentes:
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

      { path: '', redirectTo: 'inicio', pathMatch: 'full' }
    ]
  }, // ✅ ESTA COMA TE FALTABA

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
      {
        path: '',
        redirectTo: 'users',
        pathMatch: 'full',
      }
    ],
  },

  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: '**', redirectTo: 'auth/login' },
];
