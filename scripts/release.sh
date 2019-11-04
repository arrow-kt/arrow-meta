#!/bin/bash

cd $(dirname $0)/..
. ./scripts/commons.sh

function ask_for_new_versions()
{
    GRADLE_PLUGIN_ACTUAL_VERSION=$(grep -e "^version = .*$" gradle-plugin/build.gradle | cut -d' ' -f3)
    ACTUAL_VERSION=$(grep -e "^VERSION_NAME=.*$" gradle.properties | cut -d= -f2)
    echo -e "\n$GRADLE_PLUGIN_ACTUAL_VERSION found for Gradle Plugin!"
    echo "$ACTUAL_VERSION found for the rest of artifacts!"
    read -p "What's the next release version for Gradle Plugin (without -rc-)? " GRADLE_PLUGIN_EXPECTED_VERSION
    read -p "What's the next release version for the rest of artifacts? " EXPECTED_VERSION
    echo "Expected versions:"
    echo " - $GRADLE_PLUGIN_EXPECTED_VERSION for Gradle Plugin"
    echo " - $EXPECTED_VERSION for the rest of artifacts"
}

function update_files()
{
    echo -e "\nUpdating the version in gradle-plugin/build.gradle ..."
    sed -i "s/version = $GRADLE_PLUGIN_ACTUAL_VERSION/version = '$GRADLE_PLUGIN_EXPECTED_VERSION'/g" gradle-plugin/build.gradle
    echo -e "\nUpdating the version in gradle.properties ..."
    sed -i "s/VERSION_NAME=$ACTUAL_VERSION/VERSION_NAME=$EXPECTED_VERSION/g" gradle.properties
}

function create_release_branch()
{
    echo -e "\nCreating a new branch ..."
    git checkout -b release/$EXPECTED_VERSION
    git add gradle-plugin/build.gradle
    git add gradle.properties
    git commit -m "Release $EXPECTED_VERSION and $GRADLE_PLUGIN_EXPECTED_VERSION for Gradle Plugin"
    git push origin release/$EXPECTED_VERSION
}

show_banner "RELEASE PROCESS"
jump_to_master_and_pull
ask_for_new_versions
update_files
create_release_branch

echo -e "\nDone!"
echo -e "\nNext steps:\n"
echo " - Pull request from release/$EXPECTED_VERSION to release/gradle-plugin"
echo " - Pull request from release/$EXPECTED_VERSION to master"

#TODO: Finish with the whole process, extract changelog, etc.
