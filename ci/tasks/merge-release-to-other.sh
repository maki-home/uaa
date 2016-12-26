#!/bin/sh
set -e

BASEDIR=`pwd`
shopt -s dotglob
mv -f uaa-repo-other/* other-out/
cd other-out
git config --global user.email "${GIT_EMAIL}"
git config --global user.name "${GIT_NAME}"
git remote add -f staging $BASEDIR/uaa-repo-staging
git merge --no-edit staging/release