if [[ $1 != "skip-build" ]]; then
    gcloud builds submit --tag gcr.io/neon-rampart-243108/ml-module
fi

gcloud compute instances create ml-module-vm \
    --image-family=tf-latest-gpu-experimental \
    --image-project=deeplearning-platform-release \
    --accelerator=type=nvidia-tesla-t4,count=1 \
    --machine-type=n1-standard-4 \
    --maintenance-policy TERMINATE --restart-on-failure \
    --zone=us-central1-a \
    --metadata-from-file startup-script=cloud-env.sh \
    --metadata="install-nvidia-driver=True"

