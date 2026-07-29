import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Session } from '../services/session';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const session = inject(Session);

  const token = session.obtenerToken();

  if(token){

    const requestConToken = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(requestConToken);
  }
  return next(req);
};
