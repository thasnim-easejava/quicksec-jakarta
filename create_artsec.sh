password=$1
oc create secret docker-registry artifactory-sec --docker-server=hyc-wassvt-team-image-registry-docker-local.artifactory.swg-devops.com \
  --docker-username=mtamboli@us.ibm.com \
  --docker-password=$password 
   
