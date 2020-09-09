FROM maven:3-jdk-8 AS BUILD  
COPY src /usr/src/app/
RUN mvn -f /usr/src/app/pom.xml clean package

FROM maven:3-jdk-8 AS FLYWAY_BUILD
COPY flyway/pom.xml /build/
COPY flyway/src /build/src/
WORKDIR /build/
RUN mvn clean package

FROM open-liberty:kernel
COPY --from=BUILD /usr/src/app/orderdomainEAR/target/*.ear /liberty/usr/servers/defaultServer/apps/orderdomainEAR.ear
COPY --chown=1001:0 resources/liberty/config/server.xml /liberty/usr/servers/defaultServer/server.xml
RUN (mkdir -p /liberty/usr/shared/resources/postgresql && cd /liberty/usr/shared/resources/postgresql && curl -O https://repo1.maven.org/maven2/org/postgresql/postgresql/42.2.14/postgresql-42.2.14.jar)
RUN configure.sh
COPY --from=BUILD /usr/src/app/migrateAndRunServer.sh /
COPY --from=FLYWAY_BUILD /build/target/databaseMigrator-0.0.1.jar /app/

ENTRYPOINT [ "./migrateAndRunServer.sh" ]
EXPOSE 9080 9443
