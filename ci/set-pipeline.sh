#!/bin/sh
echo y | fly -t home sp -p uaa -c pipeline.yml -l ../../credentials.yml