import { Component, OnInit, Input } from '@angular/core';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { UserService } from 'src/app/shared/services/user.service';

@Component({
  selector: 'app-repo-details',
  templateUrl: './repo-details.component.html',
  styleUrls: ['./repo-details.component.scss']
})
export class RepoDetailsComponent implements OnInit {

  @Input() repo: TrackedRepository;


  constructor(private userService: UserService) { }

  ngOnInit() {
   
  }

  get repoName() {
    const splitUrl = this.repo.url.split('/');
    return this.userService.authUser.gitHubInfo.username + '/' + splitUrl[splitUrl.length - 1];
  }
}
