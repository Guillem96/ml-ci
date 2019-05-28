#!/usr/bin/env python

import os
import requests
from pathlib import Path

from git import Repo

from ml_ci.utils import delete_dir

class Network(object):
    """Class responisble of sending http requests to coordinator module
    """

    _WEBSERVICE = os.environ["COORDINATOR_URL"]

    def __init__(self, tracked_repository):
        """Force clone a github repository
        
        Arguments:
            tracked_repository {integer} -- ID of the tracked repository which is being trained
        """
        self.tracked_repository = tracked_repository
        self.token = None

    @staticmethod
    def clone_github_repository(url):
        """Force clone a github repository
        
        Arguments:
            url {string} -- Github repository https clone link
        
        Returns:
            string -- Directory where repo has been cloned
        """
        name = url.split('/')[-1]
        directory = str(Path('cloned', name))

        # In case repository has already been downloaded, remove it
        delete_dir(directory)
        
        # Clone
        Repo.clone_from(url,directory)
        return directory

    def _post(self, path, body={}):
        """Performs a post to the specified path
        
        Arguments:
            path {string}   -- Coordinator endpoint
            body {dict}     -- Request body
        
        Returns:
            Response -- requests module Response object
        """

        # Communication using json
        headers = {
            'Content-type': 'application/json', 
            'Accept': 'application/json'
        }

        # If the authentication has already been done send the token too
        if self.token:
            headers["Authorization"] = "Bearer " + self.token

        # Perform the post
        res = requests.post(Network._WEBSERVICE + path, 
                                json=body,
                                headers=headers)
        if res.status_code > 300:
            print(res)

        return res


    def authenticate(self):
        """Authenticate as ROLE_MODULE
        """
        credentials = dict(username=os.environ["ML_MODULE_USER"], 
                           password=os.environ["ML_MODULE_PASSWORD"])
        res = self._post("/auth/signIn", credentials)
        self.token = res.json()["token"]


    def create_approach(self, approach):
        """Creates a new approach

        Arguments:
            approach {dict} -- Approach configuration
        """
        approach_json = {
            "name": approach['name'],
            "status": "PENDENT",
            "trackedRepository": self.tracked_repository
        }
        res = self._post("/approaches/withTrackedRepository", approach_json)
        approach['id'] = int(res.json())

    def update_approach_status(self, approach, new_status):
        """Update approach status
        
        Arguments:
            approach {dict} -- Approach configuration
            status { PENDENT | TRAINING | ERROR | TRAINED } -- New status
        """
        self._post("/approaches/{}/status/{}".format(approach['id'], new_status))
    
    def update_repository_status(self, new_status):
        """Update tracked repository status
        
        Arguments:
            status { PENDENT | TRAINING | ERROR | TRAINED } -- New status
        """
        self._post("/trackedRepositories/{}/status/{}".format(self.tracked_repository, new_status))
    
    def increment_build(self):
        """Increment training batch
        """
        res = self._post("/trackedRepositories/{}/incrementBuild".format(self.tracked_repository))
        self.build_num = int(res.json())

    def upload_evaluations(self, evaluations_df, approach):
        """Upload csv file containing the evaluations results

        Arguments:
            evaluations_path {pd.DataFrame} -- Evaluations dataframe
            approach_id {int} -- Model ID referencing webservice instance
        """

        dst_dir = Path("evaluations")
        dst_dir.mkdir(exist_ok=True)
        
        name = "{}_{}_{}_{}.csv" \
          .format(approach['name'], 
                  approach['id'], 
                  self.tracked_repository,
                  self.build_num)
        
        evaluations_df.to_csv(dst_dir.joinpath(name), index=False)

        with dst_dir.joinpath(name).open() as f:
            requests.post(Network._WEBSERVICE + "/static/evaluations", 
                          headers={ "Authorization": "Bearer " + self.token },
                          files={'file': f })
        
        dst_dir.joinpath(name).unlink()
