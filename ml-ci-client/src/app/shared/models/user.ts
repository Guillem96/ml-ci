import { GitHubUser } from './github.user';
import { Resource } from 'angular4-hal-aot';
import { TrackedRepository } from './tracked-repository';

export class User extends Resource {
  public id: number;
  public username: string;
  public email: string;
  public gitHubInfo: GitHubUser;
  public trackedRepositories: TrackedRepository[];
}
