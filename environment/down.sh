#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./docker-compose.sh
popd > /dev/null

function main() {
    docker_compose_in_environment down -v --remove-orphans
}

main