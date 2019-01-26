# MlCi Web Service

## Docker build & run

```
$ mvn package -DskipTests
$ docker build . -t ml-ci-webservice
$ docker run --env PORT=8080 --env PROFILE=prod -p 8080:8080 ml-ci-webservice
```