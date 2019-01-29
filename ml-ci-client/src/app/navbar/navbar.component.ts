import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../shared/services/user.service';
import { AuthService } from '../shared/services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {


  constructor(public userService: UserService,
              private authService: AuthService,
              private router: Router) { }

  ngOnInit() {
  }

  public signOut() {
    this.authService.logout();
    this.router.navigate(['auth']);
  }
}
