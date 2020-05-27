#!/usr/bin/env bash

echo 'Start up the infrastructure: keycloak, kafka, schema registry'
./up.sh infra --with-monitoring
echo -e '\n'

echo 'Grant operation rights to `kafka-schema-registry` client to create and manage internal schema topic'
./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-schema-registry --operation Create --cluster kafka-cluster
./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-schema-registry --operation Describe --topic __consumer_offsets
./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-schema-registry --operation ALL --topic _schemas
./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-schema-registry --operation ALL --group schema-registry #default group ID of schema registry Kafka client is 'schema-registry'
echo -e '\n'

echo 'Create Kafka topic `order` and grant operation rights on that topic to clients `kafka-order-service` and `kafka-inventory-service`'
./run-clitools.sh kafka kafka-topics --bootstrap-server kafka:9092 --create --topic order --partitions 6 --replication-factor 3 --command-config ./admin/config/adminClient.properties
./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-order-service --operation Read --operation Write --topic order

./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-inventory-service --operation Read --operation Write --topic order
./run-clitools.sh kafka kafka-acls --bootstrap-server kafka:9092 --command-config ./admin/config/adminClient.properties --add --allow-principal User:kafka-inventory-service --operation All --resource-pattern-type prefixed --topic inventory-service --group inventory-service
echo -e '\n'

echo 'Run the application and inventory service'
./up.sh app inventory