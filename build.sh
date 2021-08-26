#!/usr/bin/env bash

set -ex

if [[ $GIT_BRANCH =~ "feature" ]]; then
   mvn clean package -U  sonar:sonar
else
   mvn clean deploy -U sonar:sonar
fi