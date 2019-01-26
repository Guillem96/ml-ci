FROM python:3.6

RUN apt-get install -y git-core 

# We copy just the requirements.txt first to leverage Docker cache
COPY ./requirements.txt /app/requirements.txt

WORKDIR /app

RUN pip install -r requirements.txt --ignore-installed

COPY ml_ci ml_ci

# ENV FLASK_APP=ml_ci/app.py
# ENV FLASK_ENV=development

ENTRYPOINT ["python", "-u", "ml_ci/app.py"]