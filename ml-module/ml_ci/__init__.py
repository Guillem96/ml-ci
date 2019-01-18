from flask import Flask, request
from flask_basicauth import BasicAuth
from ml_ci.middleware.after_response import AfterResponse

import json

from ml_ci.entrypoint import train

import os



class MlCiApp(object):

    def _setup_auth(self):
        self.app.config['BASIC_AUTH_USERNAME'] = 'user'
        self.app.config['BASIC_AUTH_PASSWORD'] = 'pass'
        self.app.config['BASIC_AUTH_FORCE'] = True

        BasicAuth(self.app)

    def _setup_after_response_callback(self):
        self.should_train = False
        self.repo_data = None 
        AfterResponse(self.app)

    def _validate_train_data(self):
        repo_url = self.repo_data.get("githubUrl")
        repo_id = self.repo_data.get("trackedRepositoryId")
        access_token = self.repo_data.get("githubToken")

        return repo_url and repo_id and access_token

    def __init__(self):
        # create and configure the app
        self.app = Flask(__name__, instance_relative_config=True)
        self.app.config.from_mapping(SECRET_KEY='dev')

        self._setup_auth()

        self._setup_after_response_callback()


        @self.app.after_response
        def train_after_res():
            if self.should_train and self.repo_data:
                repo_url = self.repo_data.get("githubUrl")
                repo_id = self.repo_data.get("trackedRepositoryId")
                access_token = self.repo_data.get("githubToken")
                train(repo_id, repo_url)


        @self.app.route('/train', methods=['POST'])
        def train_repo_models():
            try:
                self.should_train = False
                self.repo_data = json.loads(request.data)
                if self._validate_train_data():
                    self.should_train = True
                    return 'Training started', 200
                else:
                    return 'Parameters missing', 400

            except:
                self.repo_data = None
                self.should_train = False
                return "Invalid JSON body", 400


def create_app():
    return MlCiApp().app