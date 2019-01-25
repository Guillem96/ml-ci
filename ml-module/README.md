# ML Module

## Flask config

```
$ export FLASK_APP=ml_ci
$ export FLASK_ENV=development
$ flask run
```

```
$ export FLASK_APP=ml_ci && export FLASK_ENV=development && flask run
> set FLASK_APP=ml_ci && set FLASK_ENV=development && flask run
```

## Docker build & run

Create `docker.env` file with the following variables.

```
# Coordinator module settings
COORDINATOR_URL=<url>
ML_MODULE_USER=<user>
ML_MODULE_PASSWORD=<pass>

AMQP_URL=<cloud amqp url>

FLASK_APP=ml_ci
FLASK_ENV=development
```

```
$ docker build . -t ml-module
$ docker run --env-file docker.env -p 5000:5000 ml-module
```