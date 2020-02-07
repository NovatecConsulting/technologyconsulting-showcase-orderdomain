#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./.env
popd > /dev/null
export PROJECT_NAME="${COMPOSE_PROJECT_NAME}"
export PROJECT_NETWORK="${PROJECT_NAME}_default"