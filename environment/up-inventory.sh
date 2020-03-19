#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./docker-compose.sh
popd > /dev/null

function start_service () {
    docker_compose_in_environment up --build -d $1
}

function start_inventory () {
    start_service "inventory"
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    start_inventory
fi