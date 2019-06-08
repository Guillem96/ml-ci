mvn package -DskipTests

gcloud config set project ci-ml-66452
gcloud builds submit --tag gcr.io/ci-ml-66452/ml-ci-webservice

gcloud compute instances create-with-container ml-ci-webservice-vm \
     --container-image gcr.io/ci-ml-66452/ml-ci-webservice \
     --address=ml-ci-webservice-ip \
     --container-env-file=gcloud-docker.env \
     --machine-type=n1-standard-4 \
     --tags=http-server \
     --zone=us-central1-a