
ARG BASE_IMAGE=icr.io/appcafe/open-liberty:kernel-slim-java8-openj9-ubi

FROM $BASE_IMAGE

COPY --chown=1001:0 ./QuickSec/target/QuickSec.ear /config/apps/
COPY --chown=1001:0 config/server.xml /config/server.xml
COPY --chown=1001:0 config/ldap-config.xml /config/configDropins/defaults/
COPY --chown=1001:0 config/jvm.options /config/jvm.options

# Getting war files for utility applications

User root

RUN --mount=type=secret,id=token --mount=type=secret,id=user\
       mkdir -p /mytemp && cd /mytemp \
       && curl --retry 7 -sSf -u $(cat /run/secrets/user):$(cat /run/secrets/token) \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/svtMessageApp/svtMessageApp/2.0.2/svtMessageApp-2.0.2.war' \
      && curl --retry 7 -sSf -u $(cat /run/secrets/user):$(cat /run/secrets/token) \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/microwebapp/microwebapp/2.0.1/microwebapp-2.0.1.war' \
      && curl --retry 7 -sSf -u $(cat /run/secrets/user):$(cat /run/secrets/token) \
      -O 'https://na.artifactory.swg-devops.com/artifactory/hyc-wassvt-team-maven-virtual/com/ibm/ws/lumberjack/badapp/2.0.1/badapp-2.0.1.war' \
      && chown -R 1001:0 /mytemp/*.war  && mv /mytemp/*.war /config/dropins
      
user 1001

#DB2 files
COPY --chown=1001:0 ./db2jars /config/db2jars


#truststore for LDAP
COPY  --chown=1001:0 config/openldap.p12 /config/openldap.p12
COPY  --chown=1001:0 config/nest-ldap.p12 /config/nest-ldap.p12

# Setting for the verbose option
ARG VERBOSE=true
ARG FULL_IMAGE=false

# This script will add the requested XML snippets to enable Liberty features and grow image to be fit-for-purpose using featureUtility. 
# Only available in 'kernel-slim'. The 'full' tag already includes all features for convenience.

RUN if [ "$FULL_IMAGE" = "true" ] ; then echo "Skip running features.sh for full image" ; else features.sh ; fi

# Add interim fixes for WL/OL (optional)
COPY --chown=1001:0  interim-fixes /opt/ol/fixes/
COPY --chown=1001:0  interim-fixes /opt/ibm/fixes/

# This script will add the requested XML snippets and grow image to be fit-for-purpose
RUN configure.sh
