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

  public loading: boolean;

  constructor(private authService: AuthService,
              private userService: UserService,
              private router: Router) { }

  ngOnInit() {
    this.loading = true;
    
    if (this.userService.isLoggedIn()) {
      this.loading = false;
      this.router.navigate(['']);
    } else {
      const that = this;
      this.authService.loginWithGithub().then(() => that.loading = false);
      this.authService.user$.subscribe(() => {
        this.loading = false;
        this.router.navigate(['']);
      });
    }
  }

  public githubAuth() {
    this.authService.redirectToGitHubLogin();
  }
}
