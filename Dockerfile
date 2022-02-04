#FROM openliberty/open-liberty:21.0.0.5-full-java8-openj9-ubi
FROM openliberty/daily:full-java8-openj9

COPY  --chown=1001:0  ./QuickSec/target/QuickSec.ear /config/apps/
# COPY  --chown=1001:0  sharedApps/badapp.war /config/dropins
# COPY  --chown=1001:0  sharedApps/microwebapp.war /config/dropins
# COPY  --chown=1001:0  sharedApps/svtMessageApp.war /config/dropins
COPY  --chown=1001:0 config/server.xml /config/server.xml
COPY  --chown=1001:0  config/jvm.options /config/jvm.options
#COPY  --chown=1001:0 ltpa.keys /output/resources/security/ltpa.keys

ARG REG_USER
ARG REG_PASSWORD

User root
RUN mkdir -p /mytemp && cd /mytemp && curl -sSf -u "$REG_USER:$REG_PASSWORD" \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-local/svtMessageApp/svtMessageApp/2.0.1/svtMessageApp-2.0.1.war' \
      && chown -R 1001:0 /mytemp/svtMessageApp-2.0.1.war  && mv /mytemp/svtMessageApp-2.0.1.war /config/dropins

user 1001

#DB2 files
COPY ./db2jars /config/db2jars


#truststore for LDAP
COPY  --chown=1001:0 config/trustStore.jks /config/trustStore.jks


# This script will add the requested XML snippets and grow image to be fit-for-purpose
RUN configure.sh
