cd ml-ci-webservice

source gcloud-stop.sh
source gcloud-deploy.sh

cd ..

cd ml-ci-client

source heroku-deploy.sh

cd ..