# quicksec-jakarta
This is the version of QuickSec which was converted to Jakarta EE using the transformation tool as per [this TER](https://github.ibm.com/websphere/system-test/issues/403). This application is being complied and run with jdk 8 as there are still issues comlping java client with java 11.

Java EE version of this application is at [quicksec-jee](https://github.ibm.com/was-svt/quicksec-jee)

1. This project uses travis to build the application ear file as well create a container image. Container images are pushed to artifactory.
1. Application can be deployed to OCP cluster using app-deploy.yaml file which has the correct image loacation from artifactory. You need to make sure that `open liberty operator` is installed on the cluster first.
1. Pre-reqs: 
  - Deploy DB2 in containers or VM. Update the variables in app-deploy.sh file
  Use the DB2 QuickSec image
  - Server.xml is using NEST LDAP server. We can continue to use the LDAP server

1. To deploy application you could use the script `./build_deploy.sh deploy` after cloning the repo.
    ```
    oc new-project quicksec
    # Add artifactory credential to global pull secret or create a secret in your project.
   ./create_artsec.sh $password
    oc apply -f app-deploy.yaml
    # Manually access application by using the below command: https://<host>/QuickSecForm/webclient
    echo https://$(oc get route | grep quicksec-jakarta | awk '{print $2;}')/QuickSecForm/webclient
  ```
