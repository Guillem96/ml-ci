import { Network } from './network';
import { TrackedRepository } from './models';
import { GitHub } from './github';


class Observer {

  constructor (private network: Network,
               private github: GitHub) {}

  private async exploreRepository(repository: TrackedRepository) {
    console.log('Handling repository with id: ' + repository.id);
    
    // Retrieve last commit id from github api
    this.github.token = await this.network.getUserGitHubToken(repository);
    const splitUrl = repository.url.split('/');
    const last = splitUrl.length -1;
    const lastCommit = await this.github.getLastCommit(`${splitUrl[last - 1]}/${splitUrl[last]}`);
    
    // Compare current commit id with retrieved commit
    if (lastCommit.sha !== repository.lastCommit) {
      console.log(`New commit detected at ${splitUrl[last - 1]}/${splitUrl[last]} with id ${lastCommit.sha}`);
      console.log('Pushing training task');
      
      // Update repository with last commit sha
      repository.lastCommit = lastCommit.sha;
      await this.network.updateRepository(repository);

      // Push training task
      await this.network.train(repository);
    }
  }

  public async run() {
    await this.network.authencticate();

    console.log('Fetching repositories...');
    const trackedRepositories = await this.network.getTrackedRepositories();
    trackedRepositories.forEach(r => this.exploreRepository(r));
  }
}


const observer = new Observer(new Network(), 
                              new GitHub());

setInterval(() => {
  observer.run();
}, 20000);
