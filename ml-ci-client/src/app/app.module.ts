import { AuthService } from './shared/services/auth.service';
import { environment } from './../environments/environment.prod';
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MDBBootstrapModule } from 'angular-bootstrap-md';

// HAL
import { AngularHalModule } from 'angular4-hal-aot';
import { ExternalConfigurationService } from './external-config';

// Firebase modules
import { AngularFireModule } from 'angularfire2';
import { AngularFireAuthModule } from 'angularfire2/auth';
import { AngularFireDatabaseModule } from 'angularfire2/database';
import { AngularFirestoreModule } from 'angularfire2/firestore';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { GithubAuthComponent } from './github-auth/github-auth.component';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NavbarComponent } from './navbar/navbar.component';
import { AuthGuard } from './shared/guards/auth.guard';
import { MlCiComponent } from './ml-ci/ml-ci.component';
import { MlCiModule } from './ml-ci/ml-ci.module';
import { AuthInterceptor } from './shared/auth.interceptor';
import { TrackedRepositoryService } from './shared/services/tracked-repository.service';
import { MlModuleService } from './shared/services/ml-module.service';

@NgModule({
  declarations: [
    AppComponent,
    GithubAuthComponent,
    NavbarComponent,
    MlCiComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,

    // Firebase modules
    AngularFireModule.initializeApp(environment.firebase, 'ci-ml'),
    AngularFireAuthModule,
    AngularFireDatabaseModule,
    AngularFirestoreModule,

    // Hal modules
    AngularHalModule.forRoot(),

    // MDBBootstrap
    MDBBootstrapModule.forRoot(),

    MlCiModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: 'ExternalConfigurationService', useClass: ExternalConfigurationService },
    AuthService,
    AuthGuard,
    TrackedRepositoryService,
    MlModuleService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
