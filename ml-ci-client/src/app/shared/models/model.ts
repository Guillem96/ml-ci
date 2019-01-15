import { Resource } from "angular4-hal-aot";
import { TrackedRepository } from "./tracked-repository";

export class Model extends Resource {
  public id: number;
  public algorithm: string;
  public status: string;
  public hyperparameters: {name: string, value: any};
  public trackedRepository: TrackedRepository;
  public trainDate: Date;
}