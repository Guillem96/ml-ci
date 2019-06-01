import { COORDINATOR_URL, COORDINATOR_CREDENTIALS } from './environment';
import * as request from 'request-promise-native';
import { TrackedRepository } from './models';

export class Network {
  private token: string;

  public async authencticate(): Promise<void> {
    const options = {
      json: COORDINATOR_CREDENTIALS,
    }
    
    const res = await request.post(COORDINATOR_URL + '/auth/signIn', options);
    this.token = res.token;
  }

  private getAuthHeaders(): {Authorization: string} {    
    return {
      Authorization: `Bearer ${this.token}`
    };
  }

  public async getTrackedRepositories(): Promise<TrackedRepository[]> {
    const res = await request.get(COORDINATOR_URL + '/trackedRepositories', 
                        { headers: this.getAuthHeaders() });
                        
    return JSON.parse(res)._embedded.trackedRepositories;
  }

  public async train(trackedRepository: TrackedRepository): Promise<void> {
    const url = `${COORDINATOR_URL}/trackedRepositories/${trackedRepository.id}/train`;
    await request.post(url, {
      headers: this.getAuthHeaders()
    });
  }

  public async getUserGitHubToken(repository: TrackedRepository): Promise<string> {
    const res = await request.get(repository._links.user.href);    
    return JSON.parse(res).githubToken;
  }

  public async updateRepository(repository: TrackedRepository): Promise<void> {
    const res = await request.patch(`${COORDINATOR_URL}/trackedRepositories/${repository.id}`, {
      headers: this.getAuthHeaders(),
      json: repository
    });
  }
}