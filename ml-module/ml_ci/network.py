#!/usr/bin/env python

import os
from git import Repo
import requests
import json
import pickle

from utils import delete_dir

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
        directory = os.path.join('cloned', name)

        # In case repository has already been downloaded, remove it
        delete_dir(directory)
        
        # Clone
        Repo.clone_from(url, directory)
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
        return res


    def authenticate(self):
        """Authenticate as ROLE_MODULE
        """
        credentials = dict(username=os.environ["ML_MODULE_USER"], password=os.environ["ML_MODULE_PASSWORD"])
        res = self._post("/auth/signIn", credentials)
        self.token = res.json()["token"]


    def create_model(self, model):
        """Creates a model

        Arguments:
            model {ModelCfg} -- Model configuration
        """
        model_json = {
            "algorithm": model.name,
            "hyperParameters": model.params,
            "status": "PENDENT",
            "trackedRepository": self.tracked_repository
        }
        res = self._post("/models/withTrackedRepository", model_json)
        model.id = int(res.json())

    def update_model_status(self, model, new_status):
        """Update model status
        
        Arguments:
            model {ModelCfg} -- Model configuration
            status { PENDENT | TRAINING | ERROR | TRAINED } -- New status
        """
        self._post("/models/{}/status/{}".format(model.id, new_status))
    
    def add_evaluations(self, model, evaluations):
        """Add evaluations to model
        
        Arguments:
            model {ModelCfg} -- Model configuration
            evaluations {dict} -- Evaluations dictionary
        """
        self._post("/models/{}/evaluations".format(model.id), evaluations)

    def increment_build(self):
        """Increment training batch
        """
        self._post("/trackedRepositories/{}/incrementBuild".format(self.tracked_repository))

    def upload_model(self, model, model_id):
        """Upload serialized trained model to webservice

        Arguments:
            model {Sklearn model} -- Trained model
            model_id {integer} -- Model ID referencing webservice instance
        """
        dst_dir = "trained_models"
        
        if not os.path.exists(dst_dir):
            os.makedirs(dst_dir)
            
        name = "{}_{}_{}" \
          .format(model.__class__.__name__, model_id, self.tracked_repository)
        
        with open(os.path.join(dst_dir, name), "wb") as f:
            pickle.dump(model, f)

        with open(os.path.join(dst_dir, name), "rb") as f:
            requests.post(Network._WEBSERVICE + "/static/models", 
                                headers={ "Authorization": "Bearer " + self.token },
                                files={'file': f })
        os.remove(os.path.join(dst_dir, name))
        
