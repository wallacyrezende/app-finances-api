#!/bin/bash

export IMAGE_NAME=wallacyrezende/api-gateway:1.0-test

sh mvnw clean package -DskipTests
docker image rm $IMAGE_NAME
docker build -t $IMAGE_NAME .
docker push $IMAGE_NAME