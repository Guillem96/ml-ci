import { environment } from './../../../environments/environment';
import { Injectable, Injector } from '@angular/core';
import { RestService } from 'angular4-hal-aot';
import { TrackedRepository } from '../models/tracked-repository';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TrackedRepositoryService extends RestService<TrackedRepository> {
  constructor(injector: Injector, private http: HttpClient) {
    super(TrackedRepository, 'trackedRepositories', injector);
  }

  public startTraining(trackedRepository: TrackedRepository): Observable<any> {
    return this.http.post(`${environment.API}/trackedRepositories/${trackedRepository.id}/train`, {});
  }
}
