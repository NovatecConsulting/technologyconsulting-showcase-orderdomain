#!/bin/sh
echo "------------HELLO FROM NEW ENTRYPOINT---------------"
# For clean up the database use the clean profile
#java -jar -Dspring.profiles.active=clean /app/databaseMigrator-0.0.1.jar
java -jar /app/databaseMigrator-0.0.1.jar
./opt/ol/helpers/runtime/docker-server.sh /opt/ol/wlp/bin/server run defaultServer
