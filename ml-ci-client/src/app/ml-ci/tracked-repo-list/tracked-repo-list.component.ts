import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { TrackedRepository } from './../../shared/models/tracked-repository';

@Component({
  selector: 'app-tracked-repo-list',
  templateUrl: './tracked-repo-list.component.html',
  styleUrls: ['./tracked-repo-list.component.scss']
})
export class TrackedRepoListComponent implements OnInit {

  @Input() trackedRepositories: TrackedRepository[];
  @Output() clickRepo: EventEmitter<number> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  onClickRepo(index: number) {
    this.clickRepo.emit(index);
  }
}
