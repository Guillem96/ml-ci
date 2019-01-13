import { GitHubUser } from './../models/github.user';
import { Injectable } from '@angular/core';
import { of as observableOf, Observable, of, BehaviorSubject, Subject } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AngularFireAuth } from 'angularfire2/auth';
import { AngularFirestore, AngularFirestoreDocument } from 'angularfire2/firestore';
import * as firebase from 'firebase/app';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  user$: Subject<any>;

  constructor(private afAuth: AngularFireAuth,
              private afStore: AngularFirestore,
              private userService: UserService) {
    this.user$ = new Subject<any>();
  }

  public redirectToGitHubLogin() {
    const provider = new firebase.auth.GithubAuthProvider();
    provider.addScope('repo');
    provider.addScope('user');
    this.afAuth.auth.signInWithRedirect(provider);
  }

  public loginWithGithub() {
    this.afAuth.auth.getRedirectResult().then((credential) => {
      if (credential.additionalUserInfo) {
        const githubUser: GitHubUser = {
          username: credential.additionalUserInfo.username,
          accessToken: credential.credential['accessToken'],
          avatar: credential.additionalUserInfo.profile['avatar_url'],
          email: credential.additionalUserInfo.profile['email'],
          followers: credential.additionalUserInfo.profile['followers'],
          following: credential.additionalUserInfo.profile['following'],
          location: credential.additionalUserInfo.profile['location'],
          name: credential.additionalUserInfo.profile['name'],
        };
        this.updateUser(credential.user.uid, githubUser);
      }
    });
  }

  public logout() {
    this.afAuth.auth.signOut();
  }

  public haveUser(): boolean {
    return firebase.auth().currentUser != null;
  }

  private updateUser(uid: any, gitHubUser: GitHubUser) {
    const userRef: AngularFirestoreDocument<any> = this.afStore.doc<any>(`users/${uid}`);

    const data = {
      email: gitHubUser.email,
      username: gitHubUser.username,
      password: this.generatePassword(),              // Generate random password for firebase users
    };

    userRef.get().subscribe((doc: any) => {
      if (!doc.exists) { // If not exists create new user on firebase storage
        // Register to our backend too
        console.log('REGISTER NEW USER');
        // Store to firebase
        userRef.set(Object.assign({}, data));
        // Register the user to our backend
        this.userService.signUp(data.username, data.password, data.email).subscribe(
          user => {
            user.gitHubInfo = gitHubUser;
            this.user$.next(user);
          },
          err => console.log(err)
        );
      } else {
        // Get the existing password
        data.password = doc.data().password;
        console.log('LOGGING EXISTING USER');

        // Comment for production
        data.username = 'test';
        data.password = 'password';

        // Get token to interact with our server
        this.userService.signIn(data.username, data.password).subscribe(
          user => {
            user.gitHubInfo = gitHubUser;
            this.user$.next(user);
          },
          err => console.log(err)
        );

        return false;
      }
    });

  }

  private generatePassword(): string {
    let text = '';
    const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

    for (let i = 0; i < 10; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
  }
}
