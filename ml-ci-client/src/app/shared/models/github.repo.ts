import { GitHubCommit } from "./github.commit";

export interface GitHubRepository {
  full_name: string;
  name: string;
  url: string;
  forks: number;
  private: boolean;
  commits: GitHubCommit[];
  stargazers_count: number;
  description: string;
}