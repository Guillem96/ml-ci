MODULES=" ml-ci-webservice ml-ci-client ml-module ml-ci-observer"

for m in $MODULES; do
    cd $m
    echo "Deploying $m..."
    source gcloud-deploy.sh $1
    cd ..
done