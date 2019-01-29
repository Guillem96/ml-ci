#!/usr/bin/env python
import os
import json
import pika

from entrypoint import train
from network import Network

def train_callback(ch, method, properties, body):
    """Callback to handle queue message
    """
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
        # Send an ack in case message cannot be handled properly
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

    # Declare a queue
    channel.queue_declare(queue='train_repos')           

    print(" * [Cloud AMQP] Waiting for messages on train_repos queue.")

    channel.basic_qos(prefetch_count=1)                         # Only accept one message
    channel.basic_consume(train_callback, queue='train_repos')  # Setup training callback
    channel.start_consuming()


if __name__ == "__main__":
    setup_amqp_connection()