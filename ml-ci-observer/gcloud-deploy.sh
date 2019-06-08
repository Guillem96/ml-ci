if [[ $1 != "skip-build" ]]; then
    gcloud builds submit --tag gcr.io/neon-rampart-243108/ml-ci-observer
fi

gcloud compute instances create-with-container ml-ci-observer-vm \
     --container-image gcr.io/neon-rampart-243108/ml-ci-observer \
     --container-env=PROFILE=cloud \
     --machine-type=n1-standard-2 \
     --zone=us-central1-a