import { AuthService } from '../shared/services/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-github-auth',
  templateUrl: './github-auth.component.html',
  styleUrls: ['./github-auth.component.scss']
})
export class GithubAuthComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit() {
    this.authService.loginWithGithub();
  }

  public githubAuth() {
    this.authService.redirectToGitHubLogin();
  }
}
