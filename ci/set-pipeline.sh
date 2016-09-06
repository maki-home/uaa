#!/bin/sh
echo y | fly -t home sp -p home-uaa -c pipeline.yml -l ../../credentials.yml
