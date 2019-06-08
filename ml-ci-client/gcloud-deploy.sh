if [[ $1 != "skip-build" ]]; then
    ng build -c cloud
fi

firebase deploy