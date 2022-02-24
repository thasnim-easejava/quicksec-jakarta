# quicksec-jakarta
This is the version of QuickSec which was converted to Jakarta EE using the transformation tool as per [this TER](https://github.ibm.com/websphere/system-test/issues/403). This application is being complied and run with jdk 8 as there are still issues comlping java client with java 11.

Java EE version of this application is at [quicksec-jee](https://github.ibm.com/was-svt/quicksec-jee)
 
 * Prerequisite: 
   Server.xml is using NEST LDAP server. We can continue to use the LDAP server
...

1. This project uses travis to build the application ear file as well create a container image. Container images are pushed to artifactory.
1. Application use the git commit SHA value as well as latest tag for the docker image. 
1. SVT utility applications are added int the Dockerfile by downloading and copying to the docker image.
1. Yaml files to deploy DB2, QuickSec application and Jmeter can be applied directly or using argoCD
    1. DB2 yaml file is used to deploy DB2 in containers. Make sure to have DB2 script for your application added under scripts directory at https://github.ibm.com/was-svt/db2Container. Also, pass your application in the yaml file.
    1. Use JMETER yaml files to deploy them to OCP cluster. Update jmeter yaml file to adjust script, threads and time of the jmeter run. Jmeter image has most of the application scripts already added to the jmeter image: https://github.ibm.com/was-svt/jmeterStressContainer
    1. Quicksec pplication can be deployed to OCP cluster using app-deploy.yaml file which has the correct image loacation from artifactory. You need to make sure that `open liberty operator` is installed on the cluster first manually or using argoCD.
 
---------
1. To deploy application you could use the script `./build_deploy.sh deploy` after cloning the repo.
    ```
    oc new-project quicksec
    # Add artifactory credential to global pull secret or create a secret in your project.
   ./create_artsec.sh $password
    oc apply -f app-deploy.yaml
    # Manually access application by using the below command: https://<host>/QuickSecForm/webclient
    echo https://$(oc get route | grep quicksec-jakarta | awk '{print $2;}')/QuickSecForm/webclient
  ```
