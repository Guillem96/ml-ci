import * as request from 'request-promise-native';

export class GitHub {
  private GITHUB_API = 'https://api.github.com';

  public async getLastCommit(repoName: string): Promise<string> {
    const commits = await request.get(`${this.GITHUB_API}/repos/${repoName}/commits`);
    console.log(commits);
    
    return '';
  }
}