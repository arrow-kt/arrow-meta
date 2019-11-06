#!/bin/bash

cd $(dirname $0)/..
. ./scripts/commons.sh

function ask_for_new_version()
{
    ACTUAL_VERSION=$(grep -e "^version = .*$" gradle-plugin/build.gradle | cut -d' ' -f3)
    echo -e "\n$ACTUAL_VERSION found!"
    read -p "What's the next release candidate? " EXPECTED_VERSION
    echo "Expected: $EXPECTED_VERSION"
}

function update_file()
{
    echo -e "\nUpdating the version in gradle-plugin/build.gradle ..."
    sed -i "s/version = $ACTUAL_VERSION/version = '$EXPECTED_VERSION'/g" gradle-plugin/build.gradle
}

function create_release_branch()
{
    echo -e "\nCreating a new branch ..."
    git checkout -b release/$EXPECTED_VERSION
    git add gradle-plugin/build.gradle
    git commit -m "Gradle Plugin: release candidate $EXPECTED_VERSION"
    git push origin release/$EXPECTED_VERSION
}

show_banner "RELEASE CANDIDATE FOR GRADLE PLUGIN"
jump_to_master_and_pull
ask_for_new_version
update_file
create_release_branch

echo -e "\nDone!"
echo -e "\nNext steps:\n"
echo " - Pull request from release/$EXPECTED_VERSION to release/gradle-plugin"
echo " - Pull request from release/$EXPECTED_VERSION to master"
