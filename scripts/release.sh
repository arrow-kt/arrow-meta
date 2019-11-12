#!/bin/bash

cd $(dirname $0)/..
. ./scripts/commons.sh

function ask_for_new_versions()
{
    ACTUAL_VERSION=$(grep -e "^VERSION_NAME=.*$" gradle.properties | cut -d= -f2)
    echo "$ACTUAL_VERSION found!"
    read -p "What's the next release version? " EXPECTED_VERSION
    echo "Expected version: $EXPECTED_VERSION"
}

function update_file()
{
    echo -e "\nUpdating the version in gradle.properties ..."
    sed -i "s/VERSION_NAME=$ACTUAL_VERSION/VERSION_NAME=$EXPECTED_VERSION/g" gradle.properties
}

function create_release_branch()
{
    echo -e "\nCreating a new branch ..."
    git checkout -b release/$EXPECTED_VERSION
    git add gradle.properties
    git commit -m "Release $EXPECTED_VERSION"
    git push origin release/$EXPECTED_VERSION
}

show_banner "RELEASE PROCESS"
jump_to_master_and_pull
ask_for_new_versions
update_file
create_release_branch

echo -e "\nDone!"
echo -e "\nNext step:\n"
echo " - Pull request from release/$EXPECTED_VERSION to master"

#TODO: Finish with the whole process, extract changelog, etc.
