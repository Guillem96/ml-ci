import * as request from 'request-promise-native';

export class GitHub {
  private GITHUB_API = 'https://api.github.com';
  public token : string; 

  private getAuthHeaders() {
    return {
      "User-Agent": 'Ml-Ci Observer',
      Authorization: `token ${this.token}`
    }
  }

  public async getLastCommit(repoName: string): Promise<any> {
    const commits = await request.get(`${this.GITHUB_API}/repos/${repoName}/commits?page=1&per_page=1`, {
      headers: this.getAuthHeaders()
    });    
    return JSON.parse(commits)[0];
  }
}