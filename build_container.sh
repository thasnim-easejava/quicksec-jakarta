#!/bin/bash
set -euo pipefail
echo "Starting to build QuickSec svt applications $0 $@"
# common vars for all svt applications
source wassvt-common/env.sh

set +x
rootDir=`pwd`
echo "rootDir=$rootDir"

if [ "$manifest" != 'true' ] ; then
    # remove any maven if installed by default
    sudo dnf erase -yq maven-openjdk*
    sudo dnf -yq module enable maven:3.8
    sudo dnf install -yq maven-openjdk11
    rm -rf ~/.m2
    echo 'mvn version:' `mvn -v`

    # cd into the repos to build
    cd $(dirname $(readlink -f $0))

    # build the application
    mvn -q clean package
fi

appImage='quicksec/quicksec-jakarta'
baseImage="icr.io/appcafe/websphere-liberty:full-java17-openj9-ubi"
[ "${container_branch}" == 'main' ] && branch_tag='' || branch_tag="${container_branch}-"

if [ "$manifest" == 'true' ] ; then
    podman manifest create $HYCSVT/${appImage}:${branch_tag}${tag} $HYCSVT/${appImage}:${branch_tag}${tag}-amd64 $HYCSVT/${appImage}:${branch_tag}${tag}-ppc64le $HYCSVT/${appImage}:${branch_tag}${tag}-s390x
    podman manifest inspect $HYCSVT/${appImage}:${branch_tag}${tag}
    podman manifest push $HYCSVT/${appImage}:${branch_tag}${tag}

else
    podman build -t tmpimage -f Containerfile --secret id=token,src=/tmp/.token --secret id=user,src=/tmp/.user --build-arg FULL_IMAGE=true  --build-arg BASE_IMAGE="${baseImage}" .
    podman tag tmpimage $HYCSVT/${appImage}:${branch_tag}${tag}
    podman push $HYCSVT/${appImage}:${branch_tag}${tag}
fi

#cleanup
[ -f /tmp/.user ] && rm -f /tmp/.user 
[ -f /tmp/.token ] && rm -f /tmp/.token