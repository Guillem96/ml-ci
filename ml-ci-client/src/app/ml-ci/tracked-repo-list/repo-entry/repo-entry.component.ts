import { Component, OnInit, Input } from '@angular/core';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { UserService } from 'src/app/shared/services/user.service';
import { Model } from 'src/app/shared/models/model';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-repo-entry',
  templateUrl: './repo-entry.component.html',
  styleUrls: ['./repo-entry.component.scss']
})
export class RepoEntryComponent implements OnInit {

  @Input() repo: TrackedRepository;

  public repoName: string;

  public statusIcon = {
    PENDENT: 'far fa-circle',
    NONE: 'far fa-circle',
    ERROR: 'fas fa-times',
    TRAINED: 'far fa-check-circle',
    TRAINING: 'far fa-clock fa-spin'
  }

  private intervalSubscription: Subscription;

  constructor(private userService: UserService) { }

  ngOnInit() {
    const splitUrl = this.repo.url.split('/');
    this.repoName = this.userService.authUser.gitHubInfo.username + '/' + splitUrl[splitUrl.length - 1];

    this.fetchModels(this.repo);

    this.intervalSubscription = interval(5000).subscribe(() => this.fetchModels(this.repo));
  }

  ngOnDestroy() {
    this.intervalSubscription.unsubscribe();
  }

  private fetchModels(repo: TrackedRepository) {
    repo.getRelationArray(Model, 'models').subscribe(res => {
      repo.models = res;
    });
  }
}
