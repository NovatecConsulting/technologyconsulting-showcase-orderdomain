#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./docker-compose.sh
popd > /dev/null

function run_net_cmd () {
    docker_compose_in_environment run --rm net-cli "$@"
}

function run_kafka_cmd () {
    docker_compose_in_environment run --rm kafka-cli "$@"
}

function run_schemaregistry_cmd () {
    docker_compose_in_environment run --rm schemaregistry-cli "$@"
}

function usage () {
    echo "$0: $1" >&2
    echo
    echo "Usage: $0 net kafka schemaregistry [COMMAND]"
    echo
    return 1
}

function main () {
    if [[ $# -lt 1 ]]; then
        usage "Specifiy at least one cli type"
        exit $?
    fi
    case "$1" in
        net)
            shift
            run_net_cmd "$@"
            ;;
        kafka)
            shift
            run_kafka_cmd "$@"
            ;;
        schemaregistry)
            shift
            run_schemaregistry_cmd "$@"
            ;;
        *)
            usage "Unknown option: $1"
            exit $?
            ;;
    esac
    exit 0
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    main "$@"
fi