import { environment } from './../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Model } from '../models/model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  constructor(private http: HttpClient) { }

  public downloadModel(model: Model, trackedRepositoryId: number): Observable<any> {
    return this.http.get(
      `${environment.API}/static/models/${model.algorithm}_${model.id}_${trackedRepositoryId}`,
      { responseType: 'arraybuffer' });
  }
}
