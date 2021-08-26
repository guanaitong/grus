#!/usr/bin/env bash

set -ex

function deploy_parent() {
   rm -rf tmp && mkdir tmp
   cp grus-boot-starter-parent_pom.xml tmp/.flattened-pom.xml
   GRUS_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=revision -q -DforceStdout)
   SPRING_BOOT_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=spring-boot.version -q -DforceStdout)
   sed -i "s/GRUS_VERSION/$GRUS_VERSION/g" tmp/.flattened-pom.xml
   sed -i "s/SPRING_BOOT_VERSION/$SPRING_BOOT_VERSION/g" tmp/.flattened-pom.xml
   mvn deploy -f tmp/.flattened-pom.xml -Dmaven.test.skip=true
}

# deploy for branches of master or release
if [[ $GIT_BRANCH =~ "master" ]] || [[ $GIT_BRANCH =~ "release" ]] ; then
  mvn deploy -Dmaven.test.skip=true
  deploy_parent
fi