import { environment } from './../../../environments/environment';
import { Injectable, Injector } from '@angular/core';
import { RestService } from 'angular4-hal-aot';
import { TrackedRepository } from '../models/tracked-repository';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MlCiLog } from '../models/logs';

@Injectable()
export class TrackedRepositoryService extends RestService<TrackedRepository> {
  constructor(injector: Injector, private http: HttpClient) {
    super(TrackedRepository, 'trackedRepositories', injector);
  }

  public startTraining(trackedRepository: TrackedRepository): Observable<any> {
    return this.http.post(`${environment.API}/trackedRepositories/${trackedRepository.id}/train`, {});
  }

  public getLogsByBuildNum(trackedRepository: TrackedRepository, buildNum: number): Observable<MlCiLog[]> {
    return this.http.get<MlCiLog[]>(`${environment.API}/trackedRepositories/${trackedRepository.id}/logs/${buildNum}`)
  }
}
