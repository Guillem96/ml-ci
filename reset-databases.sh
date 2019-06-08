heroku pg:reset postgresql-curved-74899 \
    --app ml-ci-message-broker \
    --confirm=ml-ci-message-broker
    
firebase firestore:delete \
    -r --all-collections \
    --project=neon-rampart-243108 -y