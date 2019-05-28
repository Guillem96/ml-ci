FROM python:3.6

RUN apt-get install -y git-core 

# We copy just the requirements.txt first to leverage Docker cache
COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip install -r requirements.txt --ignore-installed
RUN pip install git+https://github.com/Appverse/DriftAI@development

COPY ml_ci ml_ci

ENTRYPOINT ["python", "-m", "ml_ci.app"]