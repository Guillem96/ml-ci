import { GitHubUser } from './../models/github.user';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { AngularFireAuth } from 'angularfire2/auth';
import { AngularFirestore, AngularFirestoreDocument } from 'angularfire2/firestore';
import * as firebase from 'firebase/app';
import { UserService } from './user.service';
import { User } from '../models/user';

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

  public async loginWithGithub() {
    const credential = await this.afAuth.auth.getRedirectResult();
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
      await this.updateUser(credential.user.uid, githubUser);
    }
  }

  public logout() {
    this.userService.logout();
    this.afAuth.auth.signOut();
  }

  public haveUser(): boolean {
    return firebase.auth().currentUser != null;
  }

  private async updateUser(uid: any, gitHubUser: GitHubUser) {
    const userRef: AngularFirestoreDocument<any> = this.afStore.doc<any>(`users/${uid}`);

    const data = {
      email: gitHubUser.email,
      username: gitHubUser.username,
      password: this.generatePassword(),              // Generate random password for firebase users
    };

    const doc = await userRef.get().toPromise();
    if (!doc.exists) { // If not exists create new user on firebase storage
      console.log('REGISTER NEW USER');

      // Store to firebase
      userRef.set(Object.assign({}, data));

      // Register the user to our backend
      const user = await this.signUpAndSignIn(data, gitHubUser.accessToken);
      user.gitHubInfo = gitHubUser;
      this.userService.storeUser();
      this.user$.next(user);
    } else {
      // Get the existing password
      data.password = doc.data().password;
      console.log('LOGGING EXISTING USER');

      // Get token to interact with our server
      const user = await this.userService.signIn(data.username, data.password, gitHubUser.accessToken).toPromise();
      user.gitHubInfo = gitHubUser;
      this.userService.storeUser();
      this.user$.next(user);
    }
  }

  private async signUpAndSignIn(data: any, githubToken: string): Promise<User> {
    try {
      await this.userService.signUp(data.username, data.password, data.email).toPromise();
      return await this.userService.signIn(data.username, data.password, githubToken).toPromise();
    } catch (err) {
      console.log(err);
      return null;
    }
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
