#!/usr/bin/env python

import os
from git import Repo
import requests
import json
import pickle

from ml_ci.utils import delete_dir

class Network(object):
    
    _WEBSERVICE = os.environ["COORDINATOR_URL"]

    def __init__(self, tracked_repository):
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
        directory = os.path.join('cloned', name)

        # In case repository has already been downloaded, remove it
        delete_dir(directory)
        
        # Clone
        Repo.clone_from(url, directory)
        return directory

    def post(self, path, body={}):
        headers = {
            'Content-type': 'application/json', 
            'Accept': 'application/json'
        }

        if self.token:
            headers["Authorization"] = "Bearer " + self.token

        res = requests.post(Network._WEBSERVICE + path, 
                                json=body,
                                headers=headers)
        return res.json()


    def authenticate(self):
        res = self.post("/auth/signIn", {"username": os.environ["ML_MODULE_USER"], "password": os.environ["ML_MODULE_PASSWORD"]})
        self.token = res["token"]


    def create_model(self, model):
        model_json = {
            "algorithm": model.name,
            "hyperParameters": model.params,
            "status": "PENDENT",
            "trackedRepository": self.tracked_repository
        }
        res = self.post("/models/withTrackedRepository", model_json)
        model.id = int(res)

    def update_model_status(self, model, new_status):
        self.post("/models/{}/status/{}".format(model.id, new_status))
    
    def add_evaluations(self, model, evaluations):
        self.post("/models/{}/evaluations".format(model.id), evaluations)

    def increment_build(self):
        self.post("/trackedRepositories/{}/incrementBuild".format(self.tracked_repository))

    def upload_model(self, model, model_id):
        dst_dit = "trained_models"
        
        if not os.path.exists(dst_dit):
            os.makedirs(dst_dit)
            
        name = "{}_{}_{}" \
          .format(model.__class__.__name__, model_id, self.tracked_repository)
        
        with open(os.path.join(dst_dit, name), "wb") as f:
            pickle.dump(model, f)

        with open(os.path.join(dst_dit, name), "rb") as f:
          res = requests.post(Network._WEBSERVICE + "/static/models", 
                                headers={ "Authorization": "Bearer " + self.token },
                                files={'file': f })
        
        os.remove(os.path.join(dst_dit, name))
        