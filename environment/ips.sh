#!/usr/bin/env bash
set -e
pushd . > /dev/null
cd $(dirname ${BASH_SOURCE[0]})
source ./env.sh
popd > /dev/null

function ips_of_all_containers() {
    docker ps -f network=${PROJECT_NETWORK} | while read line; do
        if `echo $line | grep -q 'CONTAINER ID'`; then
			printf "%-25s\t%-25s\n" 'Name' 'IP Address'
        else
            CID=$(echo $line | awk '{print $1}');
            NAME=$(docker inspect --format '{{ .Name }}' $CID);
            IP=$(docker inspect --format "{{ .NetworkSettings.Networks.${PROJECT_NETWORK}.IPAddress }}" $CID);
			printf "%-25s\t%-25s\n" ${NAME} ${IP}
        fi
    done;
}

function ip_of_service () {
    service=${1:?'Missing service name!'}
    instance=${2:?'Missing instance no!'}
    ip_of_container "${PROJECT_NAME}_${service}_${instance}" ${PROJECT_NETWORK}
}

function ip_of_container () {
    container=${1:?'Missing container name!'}
    network=${2:?'Missing network name!'}
    docker inspect -f "{{ .NetworkSettings.Networks.${network}.IPAddress }}" ${container}
}

if [ "${BASH_SOURCE[0]}" == "$0" ]; then
    ips_of_all_containers
fi
