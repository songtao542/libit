#! /bin/bash

arg=$1
domain=$2

echo "build aar for [${arg}]"

if [ ${arg} != "vpn" -a ${arg} != "theme" -a ${arg} != "sport" ]; then
    echo "loss arg: [vpn/theme/sport]"
else
    ./gradlew build_tool:run --args="${arg} ${domain}" && ./gradlew libit:assemble && ./gradlew libit:generateSourcesJar
    mv "./libit/build/libs/libit.aar" "./libit/build/libs/${arg}.aar"
    mv "./libit/build/libs/libit-sources.jar" "./libit/build/libs/${arg}-sources.jar"
fi

