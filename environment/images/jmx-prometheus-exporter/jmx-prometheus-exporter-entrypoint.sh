#!/usr/bin/env bash
set -e
j2 ${CONFIG_FILE} > /etc/jmx-prometheus-exporter/jmx-prometheus-exporter.yaml
echo "Using JMX prometheus exporter configuration:"
cat /etc/jmx-prometheus-exporter/jmx-prometheus-exporter.yaml
exec /usr/local/bin/jmx-prometheus-exporter.sh ${EXPORTER_PORT} /etc/jmx-prometheus-exporter/jmx-prometheus-exporter.yaml