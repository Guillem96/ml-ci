import { Resource } from 'angular4-hal-aot';
import { User } from './user';
import { Model } from './model';

export class TrackedRepository extends Resource {
  public id: number;
  public url: string;
  public buildNum: number;
  public lastCommit: string;
  public user: User;
  public models: Model[];
  public lastTrain: Model[];
  public status: string;
  public trainDate: Date;
}
