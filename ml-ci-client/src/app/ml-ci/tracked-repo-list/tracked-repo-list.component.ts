import { Component, OnInit, Input } from '@angular/core';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';

@Component({
  selector: 'app-tracked-repo-list',
  templateUrl: './tracked-repo-list.component.html',
  styleUrls: ['./tracked-repo-list.component.scss']
})
export class TrackedRepoListComponent implements OnInit {

  @Input() trackedRepositories: TrackedRepository[];

  constructor() { }

  ngOnInit() {
  }

}
