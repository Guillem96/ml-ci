from flask import Flask, request
from flask_basicauth import BasicAuth

from ml_ci.entrypoint import train

import os

def setup_auth(app):
    app.config['BASIC_AUTH_USERNAME'] = 'user'
    app.config['BASIC_AUTH_PASSWORD'] = 'pass'
    app.config['BASIC_AUTH_FORCE'] = True
    
    BasicAuth(app)

def create_app():
    # create and configure the app
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(
        SECRET_KEY='dev',
    )

    setup_auth(app)

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    # a simple page that says hello
    @app.route('/train')
    def train_repo_models():
        repo_url = request.args.get('url', type = str)
        repo_id = request.args.get('id', type = int)
        train(repo_id, repo_url)
        return 'Hello, World!'

    return app