import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { TrackedRepository } from './../../../shared/models/tracked-repository';
import { GithubService } from './../../../shared/services/github.service';
import { GitHubRepository } from 'src/app/shared/models/github.repo';

@Component({
  selector: 'app-repo-metadata',
  templateUrl: './repo-metadata.component.html',
  styleUrls: ['./repo-metadata.component.scss']
})
export class RepoMetadataComponent implements OnInit, OnChanges {

  @Input() repo: TrackedRepository;

  public githubRepo: GitHubRepository;

  public statusIcon = {
    PENDENT: 'far fa-circle',
    NONE: 'far fa-circle',
    ERROR: 'fas fa-times',
    TRAINED: 'far fa-check-circle',
    TRAINING: 'far fa-clock fa-spin'
  }

  constructor(private githubService: GithubService) { }

  ngOnInit() {
  }

  ngOnChanges() {
    const urlSplit = this.repo.url.split('/');
    this.githubService.getRepoInfo(urlSplit[urlSplit.length - 2] + '/' + urlSplit[urlSplit.length - 1])
      .subscribe(res => this.githubRepo = res);
  }


  // private setDate(models: Model[]) {
  //   const dates = models.filter(m => m.status != "NONE" && m.status != "PENDENT").map(m => new Date(m.trainDate));
  //   if (dates.length > 0) {
  //     this.trainedDate = new Date(Math.max.apply(null, dates));
  //   }
  // }

}
