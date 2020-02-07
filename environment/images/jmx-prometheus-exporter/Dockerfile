FROM anapsix/alpine-java:8

RUN apk update \
    && apk upgrade \
    && apk --update add coreutils curl python python-dev py-pip \
    && rm -rf /tmp/* /var/cache/apk/*
RUN pip install j2cli

ARG VERSION="0.12.0"
ARG CHECKSUM_SHA256="cc30ca71aebaf64231b4fb62b7aa6569456fe2c7422bec48f823c2262ab8d34c"
RUN curl -vsLo /usr/local/lib/jmx-prometheus-exporter.jar "https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_httpserver/${VERSION}/jmx_prometheus_httpserver-${VERSION}-jar-with-dependencies.jar" && \
    echo "${CHECKSUM_SHA256} /usr/local/lib/jmx-prometheus-exporter.jar" | sha256sum -c

COPY jmx-prometheus-exporter.yaml.j2 /etc/jmx-prometheus-exporter/jmx-prometheus-exporter.yaml.j2
COPY rules.yaml /etc/jmx-prometheus-exporter/rules.yaml
COPY jmx-prometheus-exporter.sh /usr/local/bin/jmx-prometheus-exporter.sh
COPY jmx-prometheus-exporter-entrypoint.sh /usr/local/bin/jmx-prometheus-exporter-entrypoint.sh

ENV CONFIG_FILE="/etc/jmx-prometheus-exporter/jmx-prometheus-exporter.yaml.j2"
ENV RULE_FILE="/etc/jmx-prometheus-exporter/rules.yaml"
ENV EXPORTER_PORT=9404
ENTRYPOINT ["/usr/local/bin/jmx-prometheus-exporter-entrypoint.sh"]