import os
from git import Repo
import requests
import json

from ml_ci.utils import delete_dir

class Network(object):
    
    _WEBSERVICE = "http://localhost:8080"

    def __init__(self, trackedRepository=2):
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

    @staticmethod
    def post(path, body, token=None):
        headers = {
            'Content-type': 'application/json', 
            'Accept': 'application/json'
        }

        if token:
            headers["Authorization"] = "Bearer " + token

        res = requests.post(Network._WEBSERVICE + path, 
                                json=body,
                                headers=headers)
        return res.json()


    def authenticate(self):
        res = Network.post("/auth/signIn", {"username": "MlModule", "password": "MlModule"})
        self.token = res["token"]


    def create_model(self, model):
        model_json = {
            "algorithm": model.name,
            "hyperParameters": model.params,
            "status": "PENDENT",
            "trackedRepository": self.trackedRepository
        }
        res = Network.post("/models/withTrackedRepository", model_json, self.token)
        model.id = int(res)

    def update_model_status(self, model, new_status):
        Network.post("/models/{}/status/{}".format(model.id, new_status), {}, self.token)
    
    def add_evaluations(self, model, eval):
        pass