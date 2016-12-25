#!/bin/sh

cd $1
./mvnw test -Dmaven.repo.local=../m2/rootfs/opt/m2