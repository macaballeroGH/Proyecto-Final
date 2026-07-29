import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { Session } from '../services/session';

export const authGuard: CanActivateFn = (route, state) => {

  const session = inject(Session);
  const router = inject(Router);

  const token = session.obtenerToken();

  if(token) {

    return true;

  }

  router.navigate(['/login']);

  return false;
};
