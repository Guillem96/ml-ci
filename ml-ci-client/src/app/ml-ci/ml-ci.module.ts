import { SharedModule } from './../shared/shared.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RepoDetailsComponent } from './repo-details/repo-details.component';
import { TrackedRepoListComponent } from './tracked-repo-list/tracked-repo-list.component';
import { RepoEntryComponent } from './tracked-repo-list/repo-entry/repo-entry.component';
import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { ModelDetailsComponent } from './repo-details/model-details/model-details.component';
import { RepoMetadataComponent } from './repo-details/repo-metadata/repo-metadata.component';
import { AddRepoComponent } from './add-repo/add-repo.component';
import { AppRoutingModule } from '../app-routing.module';

@NgModule({
  declarations: [
    RepoDetailsComponent,
    TrackedRepoListComponent,
    RepoEntryComponent,
    ModelDetailsComponent,
    RepoMetadataComponent,
    AddRepoComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    AppRoutingModule,
    MDBBootstrapModule.forRoot()
  ],
  exports: [
    RepoDetailsComponent,
    TrackedRepoListComponent,
    AddRepoComponent
  ]
})
export class MlCiModule { }
