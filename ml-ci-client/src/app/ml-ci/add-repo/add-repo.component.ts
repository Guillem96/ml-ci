import { Component, OnInit } from '@angular/core';
import { GithubService } from 'src/app/shared/services/github.service';
import { GitHubRepository } from 'src/app/shared/models/github.repo';
import { UserService } from 'src/app/shared/services/user.service';

@Component({
  selector: 'app-add-repo',
  templateUrl: './add-repo.component.html',
  styleUrls: ['./add-repo.component.scss']
})
export class AddRepoComponent implements OnInit {

  public githubRepos: GitHubRepository[] = [];

  constructor(public userService: UserService, 
              private gitHubService: GithubService) { }

  ngOnInit() {
    this.gitHubService.getRepos().subscribe(res => { this.githubRepos = res; console.log(res)});
  }

}
