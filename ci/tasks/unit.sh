#!/bin/sh
BASEDIR=`pwd`
M2REPO=$BASEDIR/../m2/rootfs/opt/m2

cd $1
./mvnw test -Dmaven.repo.local=$M2REPO