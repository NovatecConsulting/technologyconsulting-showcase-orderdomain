#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./docker-compose.sh
CWD_COMPOSE=$(pwd)
popd > /dev/null

function start_service () {
    docker_compose_in_environment up -d $1
}

function start_monitoring () {
    start_service "prometheus"
    start_service "grafana"
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    start_monitoring
fi