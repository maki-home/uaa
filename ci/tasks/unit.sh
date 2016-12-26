#!/bin/sh
set -e

BASEDIR=`pwd`
M2REPO=$BASEDIR/m2/rootfs/opt/m2
echo "M2REPO=$M2REPO"

cd $1
./mvnw test -Dmaven.repo.local=$M2REPO
cd $BASEDIR