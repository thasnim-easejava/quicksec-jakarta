#!/bin/bash

###########Input needed############
#Provide artifactory password here
password=

#Currently maven build, container image are built by travis build and pushed to artifactory 
# There are issues building quicksec application  client project building with Java 11
if [ $# -eq 0 ]
then
	echo Pass parameter 'build' or 'deploy' 
	curl_out="$(curl -k -Is https://$(oc get route | grep quicksec-jakarta | awk '{print $2;}')/QuickSecForm/webclient|grep HTTP)"
	echo $curl_out
	exit
fi

if [ $1 == 'build' ]
then	

# Build project and container images
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
mvn clean package
docker build -t quicksec-jakarta .
docker tag quicksec-jakarta hyc-wassvt-team-image-registry-docker-local.artifactory.swg-devops.com/quicksec/quicksec-jakarta:latest
docker push  hyc-wassvt-team-image-registry-docker-local.artifactory.swg-devops.com/quicksec/quicksec-jakarta:latest
fi

#Deployment
if [ $1 == 'deploy' ]
then	
  oc new-project quicksec
  ./create_artsec.sh $password
  oc delete -f app-deploy.yaml
  oc apply -f app-deploy.yaml
  sleep 10
  oc get routes
  sleep 10

  #Verify with curl command. -k to avoid ssl verification of self signed cert
  while [ true ]
  do
  curl_out="$(curl -k -Is https://$(oc get route | grep quicksec-jakarta | awk '{print $2;}')/QuickSecForm/webclient|grep HTTP)"
  echo $curl_out
  if [[ $curl_out == *"302"* ]]
  then
    echo "Connection Successful"
    exit
  fi
  sleep 5
 done
fi
