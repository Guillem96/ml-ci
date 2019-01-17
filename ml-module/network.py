import os
from git import Repo
import requests
import json

from utils import delete_dir

class Network(object):
    
    _WEBSERVICE = "http://localhost:8080"

    def __init__(self, trackedRepository):
        self.trackedRepository = trackedRepository

    @staticmethod
    def clone_github_repository(url):
        """Force clone a github repository
        
        Arguments:
            url {string} -- Github repository https clone link
        
        Returns:
            string -- Directory where repo has been cloned
        """
        name = url.split('/')[-1]
        directory = os.path.join('cloned', name)

        # In case repository has already been downloaded, remove it
        delete_dir(directory)
        
        # Clone
        Repo.clone_from(url, directory)
        return directory


    def create_model(self, model):
        model_json = {
            "algorithm": model.name,
            "hyperparameters": model.params,
            "status": "PENDENT",
            "trackedRepository": self.trackedRepository
        }
        res = requests.post(self._WEBSERVICE + "/models", json=json.dumps(model_json))
        print(res.json()

    def update_model_status(self, model, new_status):
        pass
    
    def add_evaluations(self, model, eval):
        pass