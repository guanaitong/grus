#!/usr/bin/env bash

#
# Copyright 2007-2024, CIIC Guanaitong, Co., Ltd.
# All rights reserved.
#

GRUS_VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=revision -q -DforceStdout)
echo "tag is $GRUS_VERSION"
git tag -a $GRUS_VERSION -m "Version created by GitLab CI Build"
git push origin --tags

