#!/bin/sh

set -e
source /docker-lib.sh
start_docker
# This loading is not nesessary but enalbes caching by Concourse
docker load -i mysql/image
docker tag "$(cat mysql/image-id)" "$(cat mysql/repository):$(cat mysql/tag)"
docker images

docker-compose -f repo/uaa-server/docker-compose-for-test.yml up -d
sh utils/scripts/add-repos-in-pom-xml.sh repo && \
sh utils/scripts/mvn.sh test repo
docker-compose -f repo/uaa-server/docker-compose-for-test.yml down