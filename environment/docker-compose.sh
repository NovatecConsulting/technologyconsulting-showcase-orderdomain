#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./env.sh
CWD_COMPOSE=$(pwd)
popd > /dev/null

function docker_compose_in_environment () {
    (cd "$CWD_COMPOSE" && docker-compose $(for file in $(ls *.yaml); do echo "-f ${file}"; done) "$@")
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    docker_compose_in_environment "$@"
fi