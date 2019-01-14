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

  public signIn(username: string, password: string): Observable<User> {
    return this.http.post(`${environment.API}/auth/signIn`, {username, password})
      .pipe(
        map((res: any) => {
          this.token = res.token;
          this.authUser = Object.assign(<User>{}, res.user);
          this.storeUser();
          return this.authUser;
        })
    );
  }

  public signUp(username: string, password: string, email: string): Observable<User> {
    return this.http.post(`${environment.API}/users`, {username, password, email})
      .pipe(
        map((res: any) => {
          this.authUser = Object.assign(<User>{}, res.user);
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
    this.authUser = Object.assign(<User>{}, JSON.parse(localStorage.getItem("user")));
  }

  public storeUser() {
    localStorage.setItem("token", this.token);
    localStorage.setItem("user", JSON.stringify(this.authUser));
  }
}
