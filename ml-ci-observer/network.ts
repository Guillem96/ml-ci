import { COORDINATOR_URL, COORDINATOR_CREDENTIALS } from './environment.prod';
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
    const res = await request.post(url);
  }
}