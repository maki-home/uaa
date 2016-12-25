#!/bin/sh

BASEDIR=`pwd`
M2REPO=$BASEDIR/../m2/rootfs/opt/m2

cd $1
./mvnw package -DskipTests=true -Dmaven.repo.local=$M2REPO
./mvnw help:evaluate -Dexpression=project.artifactId -Dmaven.test.skip=true -Dmaven.repo.local=$M2REPO
artifactId=`./mvnw help:evaluate -Dexpression=project.artifactId -Dmaven.test.skip=true -Dmaven.repo.local=$M2REPO | egrep -v '(^\[INFO])'`