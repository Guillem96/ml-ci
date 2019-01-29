import { environment } from './../../environments/environment';

import {Injectable} from '@angular/core';
import {HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import { UserService } from './services/user.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authentication: UserService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    if (req.url.includes(environment.API) && this.authentication.isLoggedIn()) {
      const authToken = this.authentication.token;
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${authToken}`)
      });
      return next.handle(authReq);
    } else if (req.url.includes('https://api.github.com') && this.authentication.isLoggedIn()) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `token ${this.authentication.authUser.gitHubInfo.accessToken}`)
      });
      return next.handle(authReq);
    } else {
      return next.handle(req);
    }
  }
}
