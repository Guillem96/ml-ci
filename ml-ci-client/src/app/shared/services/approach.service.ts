import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Approach } from '../models/approach';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApproachService {

  constructor(private http: HttpClient) { }

  public downloadEvaluation(approach: Approach, trackedRepositoryId: number): Observable<any> {
    return this.http.get(
      `${environment.API}/static/evaluations/${approach.name}_${approach.id}_${trackedRepositoryId}_${approach.buildNum}.csv`,
      { responseType: 'arraybuffer' });
  }
}
