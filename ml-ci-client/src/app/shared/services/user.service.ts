import { environment } from './../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user';
import { map } from 'rxjs/operators';
import { isNullOrUndefined } from 'util';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public token: string;
  public authUser: User = null;

  constructor(private http: HttpClient) { }

  private initUser(data: any): User {
    if (!data)
      return null;

    let user = new User();
    user.id = data.id;
    user.email = data.email;
    user.username = data.username;
    user.gitHubInfo = data.gitHubInfo;

    // Workarround in order to getRelationArray works
    user['_links'] = {}
    user['_links'].trackedRepositories = {}
    user['_links'].trackedRepositories.href = `${environment.API}/users/${data.id}/trackedRepositories`;

    user['_links'].self = {};
    user['_links'].self.href = `${environment.API}/users/${data.id}`;
    return user;
  }

  public signIn(username: string, password: string, githubToken: string): Observable<User> {
    return this.http.post(`${environment.API}/auth/signIn`, {username, password, githubToken})
      .pipe(
        map((res: any) => {
          this.token = res.token;
          this.authUser = this.initUser(res.user);
          this.storeUser();
          return this.authUser;
        })
    );
  }

  public signUp(username: string, password: string, email: string): Observable<User> {
    return this.http.post(`${environment.API}/users`, {username, password, email})
      .pipe(
        map((res: any) => {
          this.authUser = this.initUser(res.user);
          return this.authUser;
        })
    );
  }

  public isLoggedIn(): boolean {
    this.restoreUser();
    return !isNullOrUndefined(this.authUser)
            && !isNullOrUndefined(this.token)
            && this.token !== '';
  }

  public logout() {
    localStorage.clear();
    this.token = null;
    this.authUser = null;
  }

  private restoreUser() {
    this.token = localStorage.getItem("token");
    this.authUser = this.initUser(JSON.parse(localStorage.getItem("user")));
  }

  public storeUser() {
    localStorage.setItem("token", this.token);
    localStorage.setItem("user", JSON.stringify(this.authUser));
  }
}
