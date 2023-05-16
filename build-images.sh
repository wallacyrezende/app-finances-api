#!/bin/bash

cd service-discovery
sh docker-image.sh

cd config-server
sh docker-image.sh

cd api-gateway
sh docker-image.sh

cd auth-server
sh docker-image.sh

cd ms-user-release
sh docker-image.sh