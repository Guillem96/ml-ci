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
    const repo$ = this.http.get<GitHubRepository>(`${this.GITHUB_API}/repos/${repoName}`);
    const commits$ = this.http.get(`${this.GITHUB_API}/repos/${repoName}/commits`);
    
    return forkJoin(repo$, commits$)
    .pipe(
      map((res: any[]) => {
        res[0].commits = res[1].map((c: any) => {
          return <GitHubCommit> {
            url: c.commit.url,
            sha: c.sha,
            author: c.commit.author.name,
            authorAvatar: c.author.avatar_url,
            message: c.commit.message
          }
        });
        return res[0] as GitHubRepository;
      })
    );
  }

  public getRepos(): Observable<GitHubRepository[]> {
    return this.http.get<GitHubRepository[]>(`${this.GITHUB_API}/user/repos`);
  }
}
