#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./env.sh
popd > /dev/null

function docker_run () {
    docker run --rm -it --net ${PROJECT_NETWORK} "$@"
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    docker_run "$@"
fi
