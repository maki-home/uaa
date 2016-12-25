#!/bin/sh

BASEDIR=`pwd`
M2REPO=$BASEDIR/m2/rootfs/opt/m2
echo "M2REPO=$M2REPO"

cd $1
./mvnw deploy -s settings.xml -DskipTests=true -Dmaven.repo.local=$M2REPO
cd $BASEDIR