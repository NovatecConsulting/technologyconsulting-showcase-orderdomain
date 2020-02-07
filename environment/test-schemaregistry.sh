#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./env.sh
source ./run-clitools.sh
popd > /dev/null

SCHEMAREGISTRY_HOST=schemaregistry
SCHEMAREGISTRY_PORT=8081
SCHEMAREGISTRY_URL="http://${SCHEMAREGISTRY_HOST}:${SCHEMAREGISTRY_PORT}"

function log() {
  local severity=${1:?"Missing severity as first parameter!"}
  local message=${2:?"Missing message as second parameter!"}
  echo "$(date)|${severity}|${message}|"
}

function wait_until_sr_started () {
    log "INFO" "Waiting until schema registry is launched"
    run_net_cmd bash -c "while ! nc -z ${SCHEMAREGISTRY_HOST} ${SCHEMAREGISTRY_PORT}; do sleep 0.1; done"
}

function generate_example_schema () {
    echo '{"schema":"{\"type\":\"record\",\"name\":\"atestrecord\",\"namespace\":\"this.is.a.test\",\"fields\":[{\"name\":\"field\",\"type\":\"string\"}]}"}'
}

function register_schema () {
    local subject=${1:?"Missing subject name as first parameter!"}
    local schema=${2:?"Missing schema as second parameter!"}
    log "INFO" "Registering schema for subject '${subject}'"
    local result=$(run_net_cmd curl -s -XPOST -H "Content-Type: application/vnd.schemaregistry.v1+json" -H "Accept: application/vnd.schemaregistry.v1+json" --data "${schema}" ${SCHEMAREGISTRY_URL}/subjects/${subject}/versions)
    local schemaId=$(run_net_cmd echo $(echo ${result} | jq ". | .id"))
    if [[ ${schemaId} =~ ^[0-9]+ ]]; then
        log "INFO" "ok, registerd schema with id ${schemaId}"
    else    
        log "ERROR" "nok|Could not register schema: ${result}"
        return 1
    fi
}

function delete_subject(){
  local subject=${1:?"Missing subject name as first parameter!"}
  log "INFO" "Deleting subject '${subject}'"
  run_net_cmd curl -s -XDELETE ${SCHEMAREGISTRY_URL}/subjects/${subject} > /dev/null
}

function test_registerschema () {
    log "INFO" "Starting Schema Registration test"
    local subject=atestsubject
    register_schema ${subject} $(generate_example_schema)
    local returnCode=$?
    delete_subject ${subject}
    return ${returnCode}
}

function test_schemaregistry () {
    wait_until_sr_started
    test_registerschema
    local returnCode=$?
    return ${returnCode}
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    test_schemaregistry
fi