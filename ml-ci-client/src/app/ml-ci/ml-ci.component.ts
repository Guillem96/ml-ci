import { Component, OnInit } from '@angular/core';
import { UserService } from '../shared/services/user.service';
import { TrackedRepository } from '../shared/models/tracked-repository';
import { TrackedRepositoryService } from '../shared/services/tracked-repository.service';

@Component({
  selector: 'app-ml-ci',
  templateUrl: './ml-ci.component.html',
  styleUrls: ['./ml-ci.component.scss']
})
export class MlCiComponent implements OnInit {

  public trackedRepositories: TrackedRepository[] = [];

  constructor(private userService: UserService,
              private trackedRepositoryService: TrackedRepositoryService) { }

  ngOnInit() {
    if (this.userService.isLoggedIn()) {
      this.userService.authUser.getRelationArray(TrackedRepository, 'trackedRepositories') 
        .subscribe(res => { 
          this.trackedRepositories = res;
          this.userService.authUser.trackedRepositories = res;
        });
    }
  }
}
