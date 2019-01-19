import { MlModuleService } from './../../../shared/services/ml-module.service';
import { Component, OnInit, Input } from '@angular/core';
import { TrackedRepository } from './../../../shared/models/tracked-repository';
import { GithubService } from './../../../shared/services/github.service';
import { GitHubRepository } from 'src/app/shared/models/github.repo';

@Component({
  selector: 'app-repo-metadata',
  templateUrl: './repo-metadata.component.html',
  styleUrls: ['./repo-metadata.component.scss']
})
export class RepoMetadataComponent implements OnInit {

  @Input() repo: TrackedRepository;
  @Input() githubRepo: GitHubRepository;

  public statusIcon = {
    PENDENT: 'far fa-circle',
    NONE: 'far fa-circle',
    ERROR: 'fas fa-times',
    TRAINED: 'far fa-check-circle',
    TRAINING: 'far fa-clock fa-spin'
  };

  constructor(private githubService: GithubService,
              private mlModule: MlModuleService) { }

  ngOnInit() {
  }

  public startTraining() {
    this.mlModule.startTraining(this.repo).subscribe(
      () => {
        console.log('Start training');
      }
    );
  }
}
