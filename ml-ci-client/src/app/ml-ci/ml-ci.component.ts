import { interval, Subscription } from 'rxjs';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { UserService } from '../shared/services/user.service';
import { TrackedRepository } from '../shared/models/tracked-repository';
import { TrackedRepositoryService } from '../shared/services/tracked-repository.service';

@Component({
  selector: 'app-ml-ci',
  templateUrl: './ml-ci.component.html',
  styleUrls: ['./ml-ci.component.scss']
})
export class MlCiComponent implements OnInit, OnDestroy {

  public trackedRepositories: TrackedRepository[] = [];
  public selectedRepo: TrackedRepository;
  private selectedRepoIdx = 0;

  private subscription: Subscription;

  constructor(private userService: UserService,
              private trackedRepositoryService: TrackedRepositoryService) { }

  ngOnInit() {
    this.fetchTrackedRepos(true);
    this.subscription = interval(5000).subscribe(() => {
      this.fetchTrackedRepos();
    });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private fetchTrackedRepos(first: boolean = false) {
    this.userService.authUser.getRelationArray(TrackedRepository, 'trackedRepositories')
    .subscribe(res => {
      this.trackedRepositories = res;
      if (first) {
        this.selectedRepo = res[0];
      }
      this.userService.authUser.trackedRepositories = res;
    });
  }

  public selectRepo(index: number) {
    this.selectedRepoIdx = index;
    this.selectedRepo = this.trackedRepositories[index];
  }
}
