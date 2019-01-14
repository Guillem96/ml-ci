import { Injectable, Injector } from '@angular/core';
import { RestService } from 'angular4-hal-aot';
import { TrackedRepository } from '../models/tracked-repository';

@Injectable()
export class TrackedRepositoryService extends RestService<TrackedRepository> {
  constructor(injector: Injector) {
    super(TrackedRepository, 'trackedRepositories', injector);
  }
}
