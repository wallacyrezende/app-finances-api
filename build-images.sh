#!/bin/bash

cd service-discovery
sh docker-image.sh
cd ..

cd config-server
sh docker-image.sh
cd ..

cd api-gateway
sh docker-image.sh
cd ..

cd auth-server
sh docker-image.sh
cd ..

cd ms-user-release
sh docker-image.sh