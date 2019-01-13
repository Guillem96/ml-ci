import { User } from './../shared/models/user';
import { AuthService } from './../shared/services/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

  public user: User;

  constructor(private auth: AuthService) { }

  ngOnInit() {
    this.auth.user$.subscribe(res => this.user = res);
  }

}
