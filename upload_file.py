import requests
import os

token = None
_WEBSERVICE = "http://localhost:8080"

def post(path, body={}):
    headers = {
        'Content-type': 'application/json', 
        'Accept': 'application/json'
    }

    if token:
        headers["Authorization"] = "Bearer " + token

    res = requests.post(_WEBSERVICE + path, 
                            json=body,
                            headers=headers)
    return res.json()


def authenticate():
    res = post("/auth/signIn", {"username": "MlModule", "password": "MlModule"})
    return res["token"]

token = authenticate()

with open("docker-compose.yml", "rb") as f:
    res = requests.post(_WEBSERVICE + "/static/models", 
                        headers={ "Authorization": "Bearer " + token },
                        files={'file': f })
    print(res.json())
