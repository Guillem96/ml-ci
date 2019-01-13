import { GitHubUser } from './github.user';
import { Resource } from 'angular4-hal-aot';

export class User extends Resource {
  public id: number;
  public username: string;
  public email: string;
  public gitHubInfo: GitHubUser;
}
