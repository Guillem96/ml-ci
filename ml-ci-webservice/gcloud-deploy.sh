if [[ $1 != "skip-build" ]]; then
     mvn package -DskipTests
     gcloud builds submit --tag gcr.io/neon-rampart-243108/ml-ci-webservice
fi

DATABASE_URL=$(heroku config:get DATABASE_URL --app ml-ci-message-broker)
RABBIT_MQ_URL=$(heroku config:get CLOUDAMQP_URL --app ml-ci-message-broker)

gcloud compute instances create-with-container ml-ci-webservice-vm \
     --container-image gcr.io/neon-rampart-243108/ml-ci-webservice \
     --address=ml-ci-webservice-ip \
     --container-env-file=gcloud-docker.env \
     --container-env="DATABASE_URL=${DATABASE_URL},RABBIT_MQ_URL=${RABBIT_MQ_URL}" \
     --machine-type=n1-standard-4 \
     --tags=http-server,https-server \
     --zone=us-central1-a