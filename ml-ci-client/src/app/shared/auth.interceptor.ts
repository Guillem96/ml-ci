
import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import { UserService } from './services/user.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authentication: UserService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    if (this.authentication.isLoggedIn()) {
      const authToken = this.authentication.token;
      
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${authToken}`)
      });
      return next.handle(authReq);
    } else {
      return next.handle(req);
    }
  }
}