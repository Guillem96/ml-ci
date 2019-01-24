#!/usr/bin/env python
import os
import json
import pika

from flask import Flask
from flask_cors import CORS

from ml_ci.entrypoint import train


def train_callback(ch, method, properties, body):
    repo_id = None
    repo_url = None
    access_token = None
    try:
        # Parse the message
        repo_data = json.loads(body)
        repo_url = repo_data["githubUrl"]
        repo_id = repo_data["trackedRepositoryId"]
        access_token = repo_data["githubToken"]
    except:
        # TODO: Notify error to coordinator
        print("Error parsing the message from the queue")
        ch.basic_ack(delivery_tag = method.delivery_tag)
    else:
        # Process the message (Means train all models in config file) 
        train(repo_id, repo_url)
        # Send the ack to notify that the message has been processed correctly
        ch.basic_ack(delivery_tag = method.delivery_tag)
        
def setup_amqp_connection():
    """Creates channel, queue and start consume at the queue
    """
    print(" * [Cloud AMQP] Starting cloud amqp connection...")

    params = pika.URLParameters(os.environ["AMQP_URL"])
    connection = pika.BlockingConnection(params)
    
    channel = connection.channel()
    channel.queue_declare(queue='train_repos')           # Declare the queue
    
    print(" * [Cloud AMQP] Waiting for messages on train_repos queue.")

    channel.basic_qos(prefetch_count=1)                         # Only accept one message
    channel.basic_consume(train_callback, queue='train_repos')  # Setup training callback
    channel.start_consuming()

def setup_flask():
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_mapping(SECRET_KEY='dev')
    CORS(app)
    return app

def create_app():
    setup_amqp_connection()
    return setup_flask()