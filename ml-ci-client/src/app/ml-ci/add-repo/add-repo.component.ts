import { Component, OnInit } from '@angular/core';
import { GithubService } from 'src/app/shared/services/github.service';
import { GitHubRepository } from 'src/app/shared/models/github.repo';
import { UserService } from 'src/app/shared/services/user.service';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { TrackedRepositoryService } from 'src/app/shared/services/tracked-repository.service';
import { Router } from '@angular/router';
import { Observable, forkJoin } from 'rxjs';

@Component({
  selector: 'app-add-repo',
  templateUrl: './add-repo.component.html',
  styleUrls: ['./add-repo.component.scss']
})
export class AddRepoComponent implements OnInit {

  public githubRepos: { 
    prev?: GitHubRepository[], 
    current?: GitHubRepository[], 
    next?: GitHubRepository[]} = {};

  public selectedIndex: number = 0;
  public repoPage: number = 1;

  constructor(public userService: UserService, 
              private gitHubService: GithubService,
              private router: Router,
              private trackedRepositoryService: TrackedRepositoryService) { }

  ngOnInit() {
    this.fetchRepositories();
  }

  public confirmModal(index: number, basicModal: any) {
    this.selectedIndex = index;    
    basicModal.show();
  }

  public addTrackedRepo() {
    this.addTrackedRepoAsync().then(
      () => this.router.navigate([''])
    );
  }

  private fetchRepositories() {
    forkJoin(
      this.gitHubService.getRepos(this.repoPage - 1),
      this.gitHubService.getRepos(this.repoPage),
      this.gitHubService.getRepos(this.repoPage + 1)
    ).subscribe(res => {
      this.githubRepos.prev = res[0];
      this.githubRepos.current = res[1];
      this.githubRepos.next = res[2];
    });
  }

  private async addTrackedRepoAsync(): Promise<void> {
    try {
      const repo = await this.gitHubService.getRepoInfo(this.githubRepos.current[this.selectedIndex].full_name).toPromise();
      
      let trackedRepository = new TrackedRepository();
      trackedRepository.lastCommit = repo.commits[0].sha;
      trackedRepository.url = `https://github.com/${repo.full_name}`;
      
      const createdTrackedRepo = await this.trackedRepositoryService.create(trackedRepository).toPromise() as TrackedRepository;
      await createdTrackedRepo.addRelation('user', this.userService.authUser).toPromise();
    } catch (err) {
      console.log(err);
    }
  }

  public nextPage(): void {
    this.repoPage++;
    this.fetchRepositories();
  }

  public prevPage(): void {
    if (this.repoPage > 0) {
      this.repoPage--;
      this.fetchRepositories();
    }
  }
}
