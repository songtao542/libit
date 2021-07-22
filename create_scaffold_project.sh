#! /bin/bash

#./build.sh -b scaffold -d com.domain.scaffold -s library -c /Users/developer/src/libit/scaffold/libs/

config_file_path=$1

echo "config file: ${config_file_path}"

packageName=$(grep "packageName" "$config_file_path")
#echo "pkn0: ${packageName}"
packageName=${packageName//"packageName"/}
#echo "pkn1: ${packageName}"
packageName=${packageName//"\""/}
#echo "pkn2: ${packageName}"
packageName=${packageName//":"/}
#echo "pkn3: ${packageName}"
packageName=${packageName//","/}
#echo "pkn4: ${packageName}"
packageName=${packageName//" "/}
#echo "pkn5:${packageName}:"
echo "packageName(${packageName})"

#echo "${packageName}" | sed "s/packageName//g"

if [[ ${config_file_path} != "" ]]; then
  ./build.sh -b scaffold -d "${packageName}" -s library -c /Users/developer/src/libit/scaffold/libs/
  ./gradlew build_tool:run --args="create_scaffold ${config_file_path}"
else
  echo "config file not specified"
fi
