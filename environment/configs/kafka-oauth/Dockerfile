ARG  versionconfluent
FROM confluentinc/cp-kafka:$versionconfluent

ADD /build/kafka-oauth.jar /usr/share/java/kafka/kafka-oauth.jar
ADD config/kafka_server_jaas.conf /opt/kafka/config/kafka_server_jaas.conf
ADD config/adminClient.properties /admin/config/adminClient.properties