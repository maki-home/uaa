#!/bin/sh

BASEDIR=`pwd`
shopt -s dotglob
mv -f uaa-repo/* master-out/
cd master-out
git config --global user.email "${GIT_EMAIL}"
git config --global user.name "${GIT_NAME}"
git remote add -f staging $BASEDIR/uaa-repo-staging
git merge --no-edit staging/release