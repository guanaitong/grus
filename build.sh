#!/usr/bin/env bash

set -ex

mvn clean package sonar:sonar

if [[ $GIT_BRANCH =~ "feature" ]]; then
  echo "end"
else
   mvn -Dmaven.test.skip=true deploy
   mvn -f grus-boot-starter-parent_pom.xml -Dmaven.test.skip=true deploy
fi