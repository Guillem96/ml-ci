import { User } from './../shared/models/user';
import { AuthService } from './../shared/services/auth.service';
import { Component, OnInit } from '@angular/core';
import { UserService } from '../shared/services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {


  constructor(public userService: UserService,
              private router: Router) { }

  ngOnInit() {
  }

  public signOut() {
    this.userService.logout();
    this.router.navigate(['auth']);
  }

}
