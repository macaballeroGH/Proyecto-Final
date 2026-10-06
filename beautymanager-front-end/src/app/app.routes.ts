import { Routes } from "@angular/router";

import { PublicLayout } from "./layouts/public-layout/public-layout/public-layout";
import { PrivateLayout } from "./layouts/private-layout/private-layout/private-layout";
import { AdminLayout } from "./layouts/admin-layout/admin-layout/admin-layout";

import { Login } from "./features/auth/login/login";
import { Registro } from "./features/auth/registro/registro";
import { ForgotPassword } from "./features/auth/forgot-password/forgot-password";
import { ResetPassword } from "./features/auth/reset-password/reset-password";
import { Landing } from "./features/public/landing/landing";
import { PerfilAdmin } from "./features/admin/perfil/perfil";
import { PerfilCliente } from "./features/clientes/perfil/perfil";
import { PerfilEmpleado } from "./features/empleados/perfil/perfil";
import { InicioAdmin } from "./features/admin/inicio/inicio";
import { InicioCliente} from "./features/clientes/inicio/inicio"
import { InicioEmpleado } from "./features/empleados/inicio/inicio";
import { AgendaAdmin } from "./features/admin/agenda/agenda";
import { AgendaCliente } from "./features/clientes/agenda/agenda";
import { AgendaEmpleado } from "./features/empleados/agenda/agenda";

import { authGuard } from "./core/guards/auth-guard";
import { roleGuard } from "./core/guards/role-guard";

import { Rol } from "./core/enums/rol";
import { TiendaAdmin } from "./features/admin/tienda/tienda";
import { TiendaCliente } from "./features/clientes/tienda/tienda";
import { HistorialClientes } from "./features/admin/historial-clientes/historial-clientes";
import { Empleados } from "./features/admin/empleados/empleados";
import { Gastos } from "./features/admin/gastos/gastos";
import { Estadisticas } from "./features/admin/estadisticas/estadisticas";
import { Recordatorios } from "./features/admin/recordatorios/recordatorios";
import { Billetera } from "./features/common/billetera/billetera";
import { ClienteLayout } from "./layouts/cliente-layout/cliente-layout/cliente-layout";
import { HistorialTratamientos } from "./features/clientes/historial-tratamientos/historial-tratamientos";
import { Notificaciones } from "./features/clientes/notificaciones/notificaciones";
import { EmpleadoLayout } from "./layouts/empleado-layout/empleado-layout/empleado-layout";
import { Tratamientos } from "./features/empleados/tratamientos/tratamientos";

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
                path: 'billetera',
                component: Billetera
            },

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
                        component: InicioAdmin
                    },
                    {
                        path: 'perfil',
                        component: PerfilAdmin
                    },
                    {
                        path: 'agenda',
                        component: AgendaAdmin
                    },
                    {
                        path: 'tienda',
                        component: TiendaAdmin
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
            },

            {
                path: 'cliente',
                component: ClienteLayout,
                canActivate: [roleGuard(Rol.CLIENTE)],
                children: [
                    {
                        path: '',
                        redirectTo: 'inicio',
                        pathMatch: 'full'
                    },
                    {
                        path: 'inicio',
                        component: InicioCliente
                    },
                    {
                        path: 'perfil',
                        component: PerfilCliente
                    },
                    {
                        path: 'agenda',
                        component: AgendaCliente
                    },
                    {
                        path: 'historial-tratamientos',
                        component: HistorialTratamientos
                    },
                    {
                        path: 'notificaciones',
                        component: Notificaciones
                    },
                    {
                        path: 'tienda',
                        component: TiendaCliente
                    }
                ]
            },

            {
                path: 'empleado',
                component: EmpleadoLayout,
                canActivate: [roleGuard(Rol.EMPLEADO)],
                children: [
                    {
                        path: '',
                        redirectTo: 'inicio',
                        pathMatch: 'full'
                    },
                    {
                        path: 'inicio',
                        component: InicioEmpleado
                    },
                    {
                        path: 'agenda',
                        component: AgendaEmpleado
                    },
                    {
                        path: 'perfil',
                        component: PerfilEmpleado
                    },
                    {
                        path: 'tratamientos',
                        component: Tratamientos
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
