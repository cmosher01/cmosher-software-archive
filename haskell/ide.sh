#!/bin/sh

docker build --tag=haskell_ide --pull=true .

xauth nlist $DISPLAY | sed -e 's/^..../ffff/' | xauth -f /tmp/.docker.xauth nmerge -
docker run -it --rm -v /tmp/.X11-unix:/tmp/.X11-unix -v /tmp/.docker.xauth:/tmp/.docker.xauth haskell_ide bash
