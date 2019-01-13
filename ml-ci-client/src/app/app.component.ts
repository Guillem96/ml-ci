import { AuthService } from './shared/services/auth.service';
import { Component, OnInit } from '@angular/core';
import { User } from './shared/models/user';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'ml-ci';
  public user: User;

  constructor(private auth: AuthService) {
  }

  ngOnInit() {
    this.auth.user$.subscribe(res => this.user = res);
  }
}
