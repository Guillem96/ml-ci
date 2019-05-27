import { Resource } from 'angular4-hal-aot';
import { User } from './user';
import { Approach } from './approach';

export class TrackedRepository extends Resource {
  public id: number;
  public url: string;
  public buildNum: number;
  public lastCommit: string;
  public user: User;
  public models: Approach[];
  public lastTrain: Approach[];
  public status: string;
  public trainDate: Date;
}
