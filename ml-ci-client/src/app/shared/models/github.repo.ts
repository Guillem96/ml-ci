import { GitHubCommit } from "./github.commit";

export interface GitHubRepository {
  forks: number;
  private: boolean;
  commits: GitHubCommit[];
  stargazers_count: number;
  description: string;
}