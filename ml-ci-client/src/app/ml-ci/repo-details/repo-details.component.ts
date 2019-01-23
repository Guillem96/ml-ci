import { GitHubRepository } from './../../shared/models/github.repo';
import { GithubService } from 'src/app/shared/services/github.service';
import { TrackedRepositoryService } from './../../shared/services/tracked-repository.service';
import { UserService } from './../../shared/services/user.service';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { Component, OnInit, Input, OnDestroy, OnChanges } from '@angular/core';
import { interval, Subscription, Observable, forkJoin } from 'rxjs';
import { Model } from 'src/app/shared/models/model';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';

@Component({
  selector: 'app-repo-details',
  templateUrl: './repo-details.component.html',
  styleUrls: ['./repo-details.component.scss']
})
export class RepoDetailsComponent implements OnInit, OnDestroy, OnChanges {

  @Input() repo: TrackedRepository;
  public githubRepo: GitHubRepository;
  public loading = true;
  public loadingMetadata = true;

  private subscription: Subscription;

  constructor(private userService: UserService,
              private trackedRepositoryService: TrackedRepositoryService,
              private githubService: GithubService) { }

  ngOnInit() {
    this.subscription = interval(5000).subscribe(
      () => {
        this.fetchRepoData();
      }
    );
  }

  ngOnChanges() {
    this.fetchRepoData();
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  private fetchRepoData() {
    const urlSplit = this.repo.url.split('/');
    forkJoin(
      this.trackedRepositoryService.get(this.repo.id),
      this.repo.getRelationArray(Model, 'models'),
      this.githubService.getRepoInfo(urlSplit[urlSplit.length - 2] + '/' + urlSplit[urlSplit.length - 1])
    ).subscribe(
      res => {
        this.repo = res[0];
        this.repo.models = res[1];
        this.githubRepo = res[2];
        this.loading = false;
      }
    );
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
