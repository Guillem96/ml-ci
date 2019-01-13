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
import { HttpClientModule } from '@angular/common/http';
import { NavbarComponent } from './navbar/navbar.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { RepoDetailsComponent } from './repo-details/repo-details.component';

@NgModule({
  declarations: [
    AppComponent,
    GithubAuthComponent,
    NavbarComponent,
    SidebarComponent,
    RepoDetailsComponent
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
    MDBBootstrapModule.forRoot()
  ],
  providers: [
    { provide: 'ExternalConfigurationService', useClass: ExternalConfigurationService },
    AuthService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
