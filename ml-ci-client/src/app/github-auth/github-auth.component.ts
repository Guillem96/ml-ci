import { AuthService } from '../shared/services/auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../shared/services/user.service';

@Component({
  selector: 'app-github-auth',
  templateUrl: './github-auth.component.html',
  styleUrls: ['./github-auth.component.scss']
})
export class GithubAuthComponent implements OnInit {

  constructor(private authService: AuthService,
              private userService: UserService,
              private router: Router) { }

  ngOnInit() {
    if (this.userService.isLoggedIn()) {
      this.router.navigate(['']);
    } else {
      this.authService.loginWithGithub();
      this.authService.user$.subscribe(() => this.router.navigate(['']));
    }
  }

  public githubAuth() {
    this.authService.redirectToGitHubLogin();
  }
}
