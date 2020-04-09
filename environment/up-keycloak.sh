#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./docker-compose.sh
popd > /dev/null

function start_service () {
    docker_compose_in_environment up --build -d $1
}

function start_keycloak () {
    start_service "keycloak"
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    start_keycloak
fi