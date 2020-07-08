FROM maven:3-jdk-8 AS build  
COPY src /usr/src/app/
RUN mvn -f /usr/src/app/pom.xml clean package

FROM open-liberty:kernel
COPY --from=build /usr/src/app/orderdomainEAR/target/*.ear /liberty/usr/servers/defaultServer/apps/orderdomainEAR.ear
COPY --chown=1001:0 resources/liberty/config/server.xml /liberty/usr/servers/defaultServer/server.xml
RUN (mkdir -p /liberty/usr/shared/resources/h2 && cd /liberty/usr/shared/resources/h2 && curl -O https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar)
RUN (mkdir -p /liberty/usr/shared/resources/postgresql && cd /liberty/usr/shared/resources/postgresql && curl -O https://repo1.maven.org/maven2/org/postgresql/postgresql/42.2.14/postgresql-42.2.14.jar)
RUN configure.sh

EXPOSE 9080 9443
