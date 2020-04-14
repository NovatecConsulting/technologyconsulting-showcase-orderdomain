ARG  versionconfluent
FROM confluentinc/cp-schema-registry:$versionconfluent

#Add custom sasl login callback handler class to classpath
ADD /build/kafka-oauth.jar /usr/share/java/schema-registry/kafka-oauth.jar

#Add custom sasl login callback handler class to cub_classpath for docker container initialization
ADD /build/kafka-oauth.jar /etc/kafka/jars/kafka-oauth.jar
ENV CUB_CLASSPATH $CUB_CLASSPATH:/etc/kafka/jars/kafka-oauth.jar
