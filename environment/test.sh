#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
CWD_COMPOSE=$(pwd)
source ./test-kafka.sh
source ./test-schemaregistry.sh
popd > /dev/null

KAFKA=false
SCHEMAREGISTRY=false

function usage () {
    echo "$0: $1" >&2
    echo
    echo "Usage: $0 all kafka schemaregistry"
    echo
    return 1
}

function parseCmd () {
    local any_selected=false
    while [[ $# -gt 0 ]]; do
        case "$1" in
            all)
                any_selected=true
                KAFKA=true
                SCHEMAREGISTRY=true
                shift
                ;;
            kafka)
                any_selected=true
                KAFKA=true
                shift
                ;;
            schemaregistry)
                any_selected=true
                SCHEMAREGISTRY=true
                shift
                ;;
            *)
                usage "Unknown option: $1"
                return $?
                ;;
        esac
    done
    if [ "${any_selected}" == false ]; then
        usage "Specifiy at least one service"
        return $?
    fi
    return 0
}

function main () {
    parseCmd "$@"
    local retval=$?
    if [ $retval != 0 ]; then
        exit $retval
    fi
    
    if [ "$KAFKA" = true ]; then
        test_kafka
    fi
    retval=$?
    if [ $retval != 0 ]; then
        echo "\nKafka test failed!"
        exit $retval
    fi

    if [ "$SCHEMAREGISTRY" = true ]; then
        test_schemaregistry
    fi
    retval=$?
    if [ $retval != 0 ]; then
        echo "\nSchema registry test failed!"
        exit $retval
    fi

    echo "\nAll tests have been successfully executed :)"
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    main "$@"
fi