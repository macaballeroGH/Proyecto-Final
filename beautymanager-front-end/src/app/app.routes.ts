import { Routes } from "@angular/router";

import { PublicLayout } from "./layouts/public-layout/public-layout/public-layout";
import { PrivateLayout } from "./layouts/private-layout/private-layout/private-layout";
import { AdminLayout } from "./layouts/admin-layout/admin-layout/admin-layout";

import { Login } from "./features/auth/login/login";
import { Registro } from "./features/auth/registro/registro";
import { ForgotPassword } from "./features/auth/forgot-password/forgot-password";
import { ResetPassword } from "./features/auth/reset-password/reset-password";
import { Landing } from "./features/public/landing/landing";
import { Perfil } from "./features/admin/perfil/perfil";
import { Inicio } from "./features/admin/inicio/inicio";
import { Agenda } from "./features/admin/agenda/agenda";

import { authGuard } from "./core/guards/auth-guard";
import { roleGuard } from "./core/guards/role-guard";

import { Rol } from "./core/enums/rol";
import { Tienda } from "./features/admin/tienda/tienda";
import { HistorialClientes } from "./features/admin/historial-clientes/historial-clientes";
import { Empleados } from "./features/admin/empleados/empleados";
import { Gastos } from "./features/admin/gastos/gastos";
import { Estadisticas } from "./features/admin/estadisticas/estadisticas";
import { Recordatorios } from "./features/admin/recordatorios/recordatorios";

export const routes: Routes = [
    {
        path: '',
        component: PublicLayout,
        children: [
            {
                path: '',
                component: Landing
            },
            {
                path: 'login',
                component: Login
            },
            {
                path: 'registro',
                component: Registro
            },
            {
                path: 'forgot-password',
                component: ForgotPassword
            },
            {
                path: 'reset-password',
                component: ResetPassword
            }
        ]
    },

    {
        path: '',
        component: PrivateLayout,
        canActivate: [authGuard],
        children: [

            {
                path: 'admin',
                component: AdminLayout,
                canActivate: [roleGuard(Rol.ADMIN)],
                children: [
                    {
                        path: '',
                        redirectTo: 'inicio',
                        pathMatch: 'full'
                    },
                    {
                        path: 'inicio',
                        component: Inicio
                    },
                    {
                        path: 'perfil',
                        component: Perfil
                    },
                    {
                        path: 'agenda',
                        component: Agenda
                    },
                    {
                        path: 'tienda',
                        component: Tienda
                    },
                    {
                        path: 'historial-clientes',
                        component: HistorialClientes
                    },
                    {
                        path: 'empleados',
                        component: Empleados
                    },
                    {
                        path: 'gastos',
                        component: Gastos
                    },
                    {
                        path: 'estadisticas',
                        component: Estadisticas
                    },
                    {
                        path: 'recordatorios',
                        component: Recordatorios
                    }
                ]
            }
        ]
    },
    
    {
        path: '**',
        redirectTo: ''
    }
];
