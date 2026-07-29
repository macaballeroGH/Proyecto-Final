import { CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";
import { Session } from "../services/session";
import { Rol } from "../enums/rol";

export function roleGuard(rolEsperado: Rol): CanActivateFn {
  
  return(route, state) => {

    const session = inject(Session);
    const router = inject(Router);

    const usuario = session.obtenerSesion();

    if(!usuario) {

      router.navigate(['/login']);

      return false;
    }

    if(usuario.rol !== rolEsperado) {

      router.navigate(['/login']);

      return false
    }

    return true;
  };
}
