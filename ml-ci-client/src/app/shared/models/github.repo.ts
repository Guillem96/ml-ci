import { GitHubCommit } from "./github.commit";

export interface GitHubRepository {
  name: string;
  url: string;
  forks: number;
  private: boolean;
  commits: GitHubCommit[];
  stargazers_count: number;
  description: string;
}