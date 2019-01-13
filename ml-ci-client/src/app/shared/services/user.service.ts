import { environment } from './../../../environments/environment';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private token: string;

  constructor(private http: HttpClient) { }

  public signIn(username: string, password: string): Observable<User> {
    return this.http.post(`${environment.API}/auth/signIn`, {username, password})
      .pipe(
        map((res: any) => {
          this.token = res.token;
          return Object.assign(<User>{}, res.user);
        })
    );
  }

  public signUp(username: string, password: string, email: string): Observable<User> {
    return this.http.post(`${environment.API}/users`, {username, password, email})
      .pipe(
        map((res: any) => {
          return Object.assign(<User>{}, res);
        })
    );
  }
}
