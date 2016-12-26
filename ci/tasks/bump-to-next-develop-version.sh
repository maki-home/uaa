#!/bin/sh

BASEDIR=`pwd`
M2REPO=$BASEDIR/m2/rootfs/opt/m2
echo "M2REPO=$M2REPO"
VERSION=`cat uaa-version/number`-SNAPSHOT
MESSAGE="[Concourse CI] Bump to Next Development Version ($VERSION)"

shopt -s dotglob
mv -f uaa-repo/* develop-out/
cd develop-out
git remote add -f prod $BASEDIR/uaa-prod
git merge --no-edit prod/master
echo "Bump to $VERSION"
./mvnw versions:set -DnewVersion=${VERSION} -DallowSnapshots -Dmaven.repo.local=$M2REPO
git config --global user.email "${GIT_EMAIL}"
git config --global user.name "${GIT_NAME}"
git add -A
git commit -m "${MESSAGE}"