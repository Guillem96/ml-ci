import { TrackedRepositoryService } from './../../shared/services/tracked-repository.service';
import { UserService } from './../../shared/services/user.service';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { interval, Subscription, Observable, forkJoin } from 'rxjs';
import { Model } from 'src/app/shared/models/model';

@Component({
  selector: 'app-repo-details',
  templateUrl: './repo-details.component.html',
  styleUrls: ['./repo-details.component.scss']
})
export class RepoDetailsComponent implements OnInit, OnDestroy {

  @Input() repo: TrackedRepository;

  private isTraining = false;
  private subscription: Subscription;

  constructor(private userService: UserService,
              private trackedRepositoryService: TrackedRepositoryService) { }

  ngOnInit() {
    this.subscription = interval(5000).subscribe(
      () => {
        forkJoin(
          this.trackedRepositoryService.get(this.repo.id),
          this.repo.getRelationArray(Model, 'models')
        ).subscribe(
          res => {
            this.repo = res[0];
            this.repo.models = res[1];
          }
        );
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  public startTrain() {
    this.isTraining = true;
  }

  get repoName() {
    const splitUrl = this.repo.url.split('/');
    return this.userService.authUser.gitHubInfo.username + '/' + splitUrl[splitUrl.length - 1];
  }

  get currentRepos() {
    if (this.repo.models) {
      return this.repo.models.filter(m => m.buildNum === this.repo.buildNum);
    } else {
      return [];
    }
  }
}
