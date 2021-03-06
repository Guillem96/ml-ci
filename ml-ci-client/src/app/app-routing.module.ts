import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './shared/guards/auth.guard';
import { GithubAuthComponent } from './github-auth/github-auth.component';
import { MlCiComponent } from './ml-ci/ml-ci.component';
import { AddRepoComponent } from './ml-ci/add-repo/add-repo.component';

const routes: Routes = [
  { path: 'auth', component: GithubAuthComponent, pathMatch: 'full' },
  { path: '', component: MlCiComponent, canActivate: [AuthGuard], pathMatch: 'full' },
  { path: 'add-repo', component: AddRepoComponent, canActivate: [AuthGuard], pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
