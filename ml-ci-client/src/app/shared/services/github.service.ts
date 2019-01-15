import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { GitHubRepository } from './../models/github.repo';
import { GitHubCommit } from '../models/github.commit';

@Injectable({
  providedIn: 'root'
})
export class GithubService {

  private GITHUB_API = 'https://api.github.com';

  constructor(private http: HttpClient,
              private userService: UserService) { }

  public getRepoInfo(repoName: string): Observable<GitHubRepository> {
    const username = this.userService.authUser.gitHubInfo.username;
    const repo$ = this.http.get<GitHubRepository>(`${this.GITHUB_API}/repos/${username}/${repoName}`, this.getAuthHeaders());
    const commits$ = this.http.get(`${this.GITHUB_API}/repos/${username}/${repoName}/commits`, this.getAuthHeaders());
    
    return forkJoin(repo$, commits$)
    .pipe(
      map((res: any[]) => {
        console.log(res[0])
        res[0].commits = res[1].map(c => {
          return <GitHubCommit>{
            url: c.commit.url,
            sha: c.sha,
            author: c.commit.author.name,
            authorAvatar: c.author.avatar_url,
            message: c.commit.message
          }
        });
        return res[0] as GitHubRepository;
      }
    ));
  }

  private getAuthHeaders() {
    return {
      headers: new HttpHeaders({
        Authorization: 'token ' + this.userService.authUser.gitHubInfo.accessToken
      })
    }
  }
}
