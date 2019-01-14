import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { Model } from 'src/app/shared/models/model';

@Component({
  selector: 'app-tracked-repo-list',
  templateUrl: './tracked-repo-list.component.html',
  styleUrls: ['./tracked-repo-list.component.scss']
})
export class TrackedRepoListComponent implements OnInit {

  @Input() trackedRepositories: TrackedRepository[];
  @Output() clickRepo: EventEmitter<TrackedRepository> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  onClickRepo(index: number) {
    this.clickRepo.emit(this.trackedRepositories[index]);
  }
}
