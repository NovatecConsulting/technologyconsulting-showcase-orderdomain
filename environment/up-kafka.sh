#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./docker-compose.sh
popd > /dev/null

MONITORING=false

function usage () {
    echo "$0: $1" >&2
    echo
    echo "Usage: $0 [--with-monitoring]"
    echo
    return 1
}

function parseCmd () {
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --with-monitoring)
                MONITORING=true
                shift
                ;;
            *)
                usage "Unknown option: $1"
                return $?
                ;;
        esac
    done
    return 0
}

function start_service () {
    docker_compose_in_environment up -d $1
}

function start_kafka () {
    local monitoring=${1:?false}
    start_service "zookeeper1"
    start_service "zookeeper2"
    start_service "zookeeper3"
    start_service "kafka1"
    start_service "kafka2"
    start_service "kafka3"
    if [ "$monitoring" == true ]; then
        start_service "kafka1-prometheus"
        start_service "kafka2-prometheus"
        start_service "kafka3-prometheus"
    fi
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    parseCmd "$@"
    local retval=$?
    if [ $retval != 0 ]; then
        exit $retval
    fi

    start_kafka ${MONITORING}
fi