#!/bin/sh

cd repo
./mvnw package -DskipTests=true -Dmaven.repo.local=../m2/rootfs/opt/m2
./mvnw help:evaluate -Dexpression=project.artifactId -Dmaven.test.skip=true -Dmaven.repo.local=../m2/rootfs/opt/m2
artifactId=`./mvnw help:evaluate -Dexpression=project.artifactId -Dmaven.test.skip=true -Dmaven.repo.local=../m2/rootfs/opt/m2 | egrep -v '(^\[INFO])'`
cp target/${artifactId}.jar ../output/app.jar