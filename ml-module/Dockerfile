FROM tensorflow/tensorflow:2.0.0b0-gpu-py3

RUN apt-get install -y git-core 
RUN apt-get install -y wget

# We copy just the requirements.txt first to leverage Docker cache
COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip install -r requirements.txt --ignore-installed
RUN pip install git+https://github.com/Appverse/DriftAI@development

COPY ml_ci ml_ci

ENTRYPOINT ["python", "-u", "-m", "ml_ci.app"]