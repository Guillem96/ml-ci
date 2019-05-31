import { Network } from './network';
import { TrackedRepository } from './models';
import { GitHub } from './github';

class Main {

  constructor (private network: Network,
               private github: GitHub) {}

  private async exploreRepository(repository: TrackedRepository) {
    console.log('Handling repository with id: ' + repository.id);
    const splitUrl = repository.url.split('/');
    const last = splitUrl.length -1;
    await this.github.getLastCommit(`${splitUrl[last - 1]}/${splitUrl[last]}`);
  }

  public async run() {
    await this.network.authencticate();

    console.log('Fetching repositories...');
    const trackedRepositories = await this.network.getTrackedRepositories();
    trackedRepositories.forEach(this.exploreRepository)
  }
}

// setInterval(() => {
  new Main(new Network(), new GitHub()).run();
// }, 10000);
