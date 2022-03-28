[![Build Status](https://travis.ibm.com/was-svt/quicksec-jakarta.svg?token=bkZSz9PKTsrkjdeyzxEd&branch=master)](https://travis.ibm.com/was-svt/quicksec-jakarta)

# quicksec-jakarta
This is the version of QuickSec which is converted to Jakarta EE using the transformation tool as per [this TER](https://github.ibm.com/websphere/system-test/issues/403). This application is being complied and run with jdk 8 as there are still issues comlping java client with java 11.

Java EE version of this application is at [quicksec-jee](https://github.ibm.com/was-svt/quicksec-jee)
 
 * Prerequisite: 
   Server.xml is using NEST LDAP server. We can continue to use the LDAP server. 

1. This project uses travis to build the application ear file as well create a container image. Container images are pushed to artifactory at hyc-wassvt-team-image-registry-docker-local.artifactory.swg-devops.com/quicksec/quicksec-jakarta:`<tag>`
1. Application builds different images based on 
   *  Open Liberty latest GA kernel image in icr (cr.io/appcafe/open-liberty:kernel-slim-java8-openj9-ubi): tags are `latest`, `jdk8` and `ga`
   *  Open Liberty daily full image at docker hub (openliberty/daily:full-java8-openj9): tags is `daily`
   *  WebSphere Liberty full ga image (icr.io/appcafe/websphere-liberty:full-java8-openj9-ubi): tag is `wl-ga`
   *  It also use the git commit SHA value to roll back to previous images which match to a specific commit of git. Make sure not to have too many of these images and delete some time to time. 
3. SVT utility applications are added int the Containerfile by downloading the EE9 versions and copying to the docker image. 
https://github.ibm.com/was-svt/svtMessageApp/releases
 
https://github.ibm.com/was-svt/microwebapp/releases

https://github.ibm.com/was-lumberjack/badapp/releases
 
6. DB2 repo has correct files to configure SVT DB2 contianer for QuickSec at https://github.ibm.com/was-svt/db2Container
7. Yaml files to deploy DB2, QuickSec application and Jmeter can be applied directly or using argoCD
8. Quicksec application can be deployed to OCP cluster using app-deploy.yaml file. This file needs to be updated when using non-default ( default is to use `latest` tag) applicaiton image from artifactory. You need to make sure that `open liberty/WebSphere Liberty operator` is installed on the cluster first manually or using argoCD.
9. Use JMETER yaml files to deploy them to OCP cluster. Update jmeter yaml file to adjust script, threads and time of the jmeter run. Jmeter image has most of the application scripts already added to the jmeter image: https://github.ibm.com/was-svt/jmeterStressContainer

URLs: As we want to use different context roots for the application including utility applications, app-deploy.yaml is not using path to add context root to the route. 

**QuickSec Application**

https://quicksec-jakarta-quicksec.apps.mst10.cp.fyre.ibm.com/QuickSecForm/

Login with persona1/ppersona1
Click on Go/Get to access DB2. If the DB2 is not setup correctly to work with application there will be errors on this page.

https://quicksec-jakarta-quicksec.apps.mst10.cp.fyre.ibm.com/QuickSecBaisc/

**Utility apps**

https://quicksec-jakarta-qs.apps.mst10.cp.fyre.ibm.com/svtMessageApp/printMessage?message=mstmsg

https://quicksec-jakarta-qs.apps.mst10.cp.fyre.ibm.com/badapp/Angry

https://quicksec-jakarta-qs.apps.mst10.cp.fyre.ibm.com/microwebapp/

 
---------

NOTE: This repo does not utilizes submodule anymore. Below commands are kept for reference in future if submodule is used.

```
git clone git@github.ibm.com:was-svt/quicksec-jakarta.git
cd quicksec-jakarta
git submodule update --init --recursive
```
