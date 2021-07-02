#!/bin/bash
set -e
CONTAINER_TAG="${TAG:-development}"

DOCKER_COMPLETE_PATH="fandango.azurecr.io/jsonpedia:${CONTAINER_TAG}"
echo "Building Docker image: ${DOCKER_COMPLETE_PATH}....."
docker build . -t ${DOCKER_COMPLETE_PATH}
echo "pushing Docker image: ${DOCKER_COMPLETE_PATH}....."
docker push ${DOCKER_COMPLETE_PATH}
echo "Finished pushing Docker image to ACR..${DOCKER_COMPLETE_PATH}"
