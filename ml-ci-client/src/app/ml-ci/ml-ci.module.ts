import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RepoDetailsComponent } from './repo-details/repo-details.component';
import { TrackedRepoListComponent } from './tracked-repo-list/tracked-repo-list.component';

@NgModule({
  declarations: [
    RepoDetailsComponent,
    TrackedRepoListComponent,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    RepoDetailsComponent,
    TrackedRepoListComponent,
  ]
})
export class MlCiModule { }
