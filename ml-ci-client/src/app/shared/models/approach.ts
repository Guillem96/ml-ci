import { Resource } from 'angular4-hal-aot';
import { TrackedRepository } from './tracked-repository';

export class Approach extends Resource {
  public id: number;
  public buildNum: number;
  public name: string;
  public status: string;
  public trackedRepository: TrackedRepository;
  public trainDate: Date;
}
