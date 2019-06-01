#!/bin/bash

cd ml-ci-client 

echo Building MlCi Client...

ng build -c production
if [ $? -eq 0 ]; then
    echo Done
else
    echo Error building MlCi Client
    return 1
fi

cd ..


cd ml-ci-webservice 

echo Building MlCi Webservice...

mvn package -DskipTests
if [ $? -eq 0 ]; then
    echo Done
else
    echo Error building MlCi Webservice
    return 1
fi

cd ..

cd ml-ci-observer 

echo Building MlCi Repository Observer...

npm run build
if [ $? -eq 0 ]; then
    echo Done
else
    echo Error building MlCi Observer
    return 1
fi

docker-compose build