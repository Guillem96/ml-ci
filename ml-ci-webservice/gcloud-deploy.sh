if [[ $1 != "skip-build" ]]; then
     mvn package -DskipTests
     gcloud builds submit --tag gcr.io/neon-rampart-243108/ml-ci-webservice
fi

gcloud compute instances create-with-container ml-ci-webservice-vm \
     --container-image gcr.io/neon-rampart-243108/ml-ci-webservice \
     --address=ml-ci-webservice-ip \
     --container-env-file=gcloud-docker.env \
     --machine-type=n1-standard-4 \
     --tags=http-server,https-server \
     --zone=us-central1-a