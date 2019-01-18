import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { UserService } from 'src/app/shared/services/user.service';
import { Model } from 'src/app/shared/models/model';

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

  constructor(private userService: UserService) { }

  ngOnInit() {
    const splitUrl = this.repo.url.split('/');
    this.repoName = this.userService.authUser.gitHubInfo.username + '/' + splitUrl[splitUrl.length - 1];

    // Fetch all related models
    this.repo.getRelationArray(Model, 'models').subscribe(res => {
      this.repo.models = res;
    });
  }

  // private setStatus(models: Model[]) {
  //   const statuses = models.map(m => m.status);
  //   if (statuses.some(s => s === 'ERROR')) {
  //     this.repo.status = 'error';
  //   } else if (statuses.some(s => s === 'NONE' || s == 'PENDENT')) {
  //     this.repo.status = 'pendent';
  //   } else if (statuses.some(s => s === 'TRAINING')) {
  //     this.repo.status = 'training';
  //   } else {
  //     this.repo.status = 'trained';
  //   }
  // }

  // private setDate(models: Model[]) {
  //   const dates = models.filter(m => m.status != "NONE" && m.status != "PENDENT").map(m => new Date(m.trainDate));
  //   if (dates.length > 0) {
  //     this.trainedDate = new Date(Math.max.apply(null, dates));
  //   }
  // }
}
