export interface TrackedRepository {
  id: number;
  url: string;
  buildNum: number;
  lastCommit: string;
}