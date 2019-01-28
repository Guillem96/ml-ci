#!/bin/bash

cd ml-ci-client 

echo Building MlCi Client...

ng build --prod
if [ $? -eq 0 ]; then
    echo Done
else
    echo Error building MlCi Client
    exit 1
fi

cd ..


cd ml-ci-webservice 

echo Building MlCi Webservice...

mvn package -DskipTests
if [ $? -eq 0 ]; then
    echo Done
else
    echo Error building MlCi Webservice
    exit 1
fi

cd ..

docker-compose build