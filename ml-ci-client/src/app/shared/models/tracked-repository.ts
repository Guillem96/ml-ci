import { Resource } from "angular4-hal-aot";
import { User } from "./user";
import { Model } from "./model";

export class TrackedRepository extends Resource {
  public id: number;
  public url: string;
  public lastCommit: string;
  public user: User;
  public models: Model[];
}