import { MDBBootstrapModule } from 'angular-bootstrap-md';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingComponent } from './loading/loading.component';
import { TabsComponent } from './tabs/tabs.component';
import { TabComponent } from './tabs/tab/tab.component';
import { AuthService } from './services/auth.service';
import { AuthGuard } from './guards/auth.guard';
import { TrackedRepositoryService } from './services/tracked-repository.service';
import { ModelService } from './services/model.service';
import { EmptyComponent } from './empty/empty.component';

@NgModule({
  declarations: [
    LoadingComponent,
    TabsComponent,
    TabComponent,
    EmptyComponent,
  ],
  exports: [
    LoadingComponent,
    TabsComponent,
    TabComponent,
    EmptyComponent
  ],
  imports: [
    CommonModule,
    MDBBootstrapModule.forRoot()
  ],
  providers: [
    AuthService,
    AuthGuard,
    TrackedRepositoryService,
    ModelService
  ]
})
export class SharedModule { }
