import { Resource } from 'angular4-hal-aot';
import { TrackedRepository } from './tracked-repository';

export class MlCiLog extends Resource {
  public id: number;
  public level: string;
  public buildNum: number;
  public message: string;
  public trackedRepository: TrackedRepository;
}