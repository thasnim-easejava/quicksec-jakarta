
FROM icr.io/appcafe/open-liberty:kernel-slim-java8-openj9-ubi

COPY  --chown=1001:0  ./QuickSec/target/QuickSec.ear /config/apps/
COPY  --chown=1001:0 config/server.xml /config/server.xml
COPY  --chown=1001:0  config/jvm.options /config/jvm.options

# It was decided not to use submodules for getting utility apps but using curl commands to get released versions
# COPY  --chown=1001:0  sharedApps/badapp.war /config/dropins
# COPY  --chown=1001:0  sharedApps/microwebapp.war /config/dropins
# COPY  --chown=1001:0  sharedApps/svtMessageApp.war /config/dropins
#COPY  --chown=1001:0 ltpa.keys /output/resources/security/ltpa.keys

ARG REG_USER
ARG REG_PASSWORD

# Getting war files for utility applications

User root

RUN mkdir -p /mytemp && cd /mytemp && curl -sSf -u "$REG_USER:$REG_PASSWORD" \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-local/svtMessageApp/svtMessageApp/2.0.1/svtMessageApp-2.0.1.war' \
      && curl -sSf -u "$REG_USER:$REG_PASSWORD" \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/microwebapp/microwebapp-ee9/2.0.0/microwebapp-ee9-2.0.0.war' \
      && curl -sSf -u "$REG_USER:$REG_PASSWORD" \
      -O 'https://na.artifactory.swg-devops.com/ui/native/hyc-wassvt-team-maven-virtual/com/ibm/ws/lumberjack/badapp-ee9/1.0.0/badapp-ee9-2.0.0.war' \
      && chown -R 1001:0 /mytemp/*.war  && mv /mytemp/*.war /config/dropins
      
user 1001

#DB2 files
COPY ./db2jars /config/db2jars


#truststore for LDAP
COPY  --chown=1001:0 config/trustStore.jks /config/trustStore.jks

# This script will add the requested XML snippets to enable Liberty features and grow image to be fit-for-purpose using featureUtility. 
# Only available in 'kernel-slim'. The 'full' tag already includes all features for convenience.

RUN features.sh

# Add interim fixes (optional)
COPY --chown=1001:0  interim-fixes /opt/ol/fixes/

# This script will add the requested XML snippets and grow image to be fit-for-purpose
RUN configure.sh
