curl -d '{ "username": "test", "password": "password"}' \
        -X POST -H "Content-Type: application/json" \
        http://localhost:8080/auth/signIn