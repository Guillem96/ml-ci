import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RepoDetailsComponent } from './repo-details/repo-details.component';
import { TrackedRepoListComponent } from './tracked-repo-list/tracked-repo-list.component';
import { RepoEntryComponent } from './tracked-repo-list/repo-entry/repo-entry.component';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { ModelDetailsComponent } from './repo-details/model-details/model-details.component';

@NgModule({
  declarations: [
    RepoDetailsComponent,
    TrackedRepoListComponent,
    RepoEntryComponent,
    ModelDetailsComponent,
  ],
  imports: [
    CommonModule,
    MDBBootstrapModule.forRoot()
  ],
  exports: [
    RepoDetailsComponent,
    TrackedRepoListComponent,
  ]
})
export class MlCiModule { }
