import { environment } from './../../../environments/environment';
import { TrackedRepository } from 'src/app/shared/models/tracked-repository';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserService } from './user.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MlModuleService {

  constructor(private http: HttpClient,
              private userService: UserService) { }

  public startTraining(trackedRepository: TrackedRepository): Observable<any> {
    return this.http.post(`${environment.ML_MODULE}/train`, {
      trackedRepositoryId: trackedRepository.id,
      githubUrl: trackedRepository.url,
      githubToken: this.userService.authUser.gitHubInfo.accessToken,
    });
  }
}
