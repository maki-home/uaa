#!/bin/sh

cd $1
./mvnw test -Dmaven.repo.local=$BASEDIR/m2/rootfs/opt/m2