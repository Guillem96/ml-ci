echo "Token: $2"
echo "Repo ID: $1"

curl -X POST -H "Authorization: Bearer $2" \
        http://localhost:8080/trackedRepositories/$1/train