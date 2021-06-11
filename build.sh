#! /bin/bash

arg=$1
domain=$2

# copy to dir
cpt=$3

echo "build aar for [${arg}]"

./gradlew build_tool:run --args="${arg} ${domain}" && ./gradlew libit:assemble && ./gradlew libit:generateSourcesJar
mv "./libit/build/libs/libit.aar" "./libit/build/libs/${arg}.aar"
mv "./libit/build/libs/libit-sources.jar" "./libit/build/libs/${arg}-sources.jar"

if [ ${cpt} != "" ]; then
    cp "./libit/build/libs/${arg}.aar" "${cpt}"
    cp "./libit/build/libs/${arg}-sources.jar" "${cpt}"
fi

