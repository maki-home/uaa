#!/bin/sh
set -e

BASEDIR=`pwd`
M2REPO=$BASEDIR/m2/rootfs/opt/m2
echo "M2REPO=$M2REPO"
VERSION=`cat uaa-version/number`
MESSAGE="[Concourse CI] Release $VERSION"

shopt -s dotglob
mv -f uaa-repo/* master-out
echo "Bump to $VERSION"
cd master-out
./mvnw versions:set -DnewVersion=${VERSION} -Dmaven.repo.local=${M2REPO}
git config --global user.email "${GIT_EMAIL}"
git config --global user.name "${GIT_NAME}"
git add -A
git commit -m "${MESSAGE}"