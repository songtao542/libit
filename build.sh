#! /bin/bash

arg=$1

echo "build aar for [${arg}]"

if [ ${arg} != "vpn" -a ${arg} != "theme" -a ${arg} != "sport" ]; then
    echo "loss arg: [vpn/theme/sport]"
else
    ./gradlew build_tool:run --args=${arg} && ./gradlew libit:assemble && ./gradlew libit:generateSourcesJar
fi

