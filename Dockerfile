#FROM openliberty/open-liberty:21.0.0.5-full-java8-openj9-ubi
FROM openliberty/daily:full-java8-openj9

COPY  --chown=1001:0  ./QuickSec/target/QuickSec.ear /config/apps/
#COPY  --chown=1001:0  dropins/badapp.war /config/dropins
#COPY  --chown=1001:0  dropins/microwebapp.war /config/dropins
#COPY  --chown=1001:0  dropins/svtMessageApp.war /config/dropins
COPY  --chown=1001:0 server.xml /config/server.xml
COPY  --chown=1001:0  jvm.options /config/jvm.options
COPY  --chown=1001:0 ltpa.keys /output/resources/security/ltpa.keys

#DB2 files
COPY ./db2jars /config/db2jars


#truststore for LDAP
COPY  --chown=1001:0 config/trustStore.jks /config/trustStore.jks


# This script will add the requested XML snippets and grow image to be fit-for-purpose
RUN configure.sh
