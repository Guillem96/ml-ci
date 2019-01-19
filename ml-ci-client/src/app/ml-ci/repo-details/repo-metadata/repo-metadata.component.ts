import { MlModuleService } from './../../../shared/services/ml-module.service';
import { Component, OnInit, Input, OnChanges, Output, EventEmitter } from '@angular/core';
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
  @Output() startTrain = new EventEmitter<void>();

  public githubRepo: GitHubRepository;

  public statusIcon = {
    PENDENT: 'far fa-circle',
    NONE: 'far fa-circle',
    ERROR: 'fas fa-times',
    TRAINED: 'far fa-check-circle',
    TRAINING: 'far fa-clock fa-spin'
  }

  constructor(private githubService: GithubService,
              private mlModule: MlModuleService) { }

  ngOnInit() {
  }

  ngOnChanges() {
    const urlSplit = this.repo.url.split('/');
    this.githubService.getRepoInfo(urlSplit[urlSplit.length - 2] + '/' + urlSplit[urlSplit.length - 1])
      .subscribe(res => this.githubRepo = res);
  }

  public startTraining() {
    this.mlModule.startTraining(this.repo).subscribe(
      () => {
        console.log('Start training');
        this.startTrain.emit();
      }
    );
  }
}
