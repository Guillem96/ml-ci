MODULES="ml-ci-webservice ml-module ml-ci-observer"

for m in $MODULES; do
    cd $m
    echo "Stopping $m..."
    source gcloud-stop.sh
    cd ..
done