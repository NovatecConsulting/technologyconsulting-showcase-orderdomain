#!/usr/bin/env bash
set -e
PORT="${1:?'Missing port!'}"
CONFIG_FILE="${2:?'Missing config file path!'}"
exec java -jar /usr/local/lib/jmx-prometheus-exporter.jar ${PORT} ${CONFIG_FILE}