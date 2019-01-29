import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { UserService } from '../services/user.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private authService: UserService,
              private router: Router) {}

  canActivate() {
    const isLoggedIn = this.authService.isLoggedIn();
    
    if (!isLoggedIn) {
      this.router.navigate(['auth']);
    }

    return isLoggedIn;
  }
}