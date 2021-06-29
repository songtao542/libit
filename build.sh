#! /bin/bash

build=""
domain=""
suffix=""
copyto=""
hilt=" hilt:enable"

while getopts b:d:s:c:h opt; do
  case $opt in
  b)
    build="$OPTARG"
    ;;
  d)
    domain="$OPTARG"
    ;;
  s)
    suffix=" $OPTARG"
    ;;
  h)
    hilt=" hilt:disable"
    ;;
  c)
    copyto="$OPTARG"
    ;;
  ?)
    echo "$opt is an invalid option"
    ;;
  esac
done

echo "build for >>>>>>>>: ${build}"
echo "domain is >>>>>>>>: ${domain}"
echo "manifest suffix >>: ${suffix}"
echo "use hilt >>>>>>>>>: ${hilt}"
echo "copy to dir >>>>>>: ${copyto}"

./gradlew build_tool:run --args="${build} ${domain}${suffix}${hilt}" && ./gradlew libit:assemble && ./gradlew libit:generateSourcesJar
mv "./libit/build/libs/libit.aar" "./libit/build/libs/${build}.aar"
mv "./libit/build/libs/libit-sources.jar" "./libit/build/libs/${build}-sources.jar"

if [[ ${copyto} != "" ]]; then
  cp "./libit/build/libs/${build}.aar" "${copyto}"
  cp "./libit/build/libs/${build}-sources.jar" "${copyto}"
fi
