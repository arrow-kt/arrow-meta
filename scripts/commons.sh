#!/bin/bash

function show_banner()
{
    echo '..........................................................'
    echo '                                    __  __      _         ' 
    echo '     /\                            |  \/  |    | |        ' 
    echo '    /  \   _ __ _ __ _____      __ | \  / | ___| |_ __ _  ' 
    echo '   / /\ \ | `__| `__/ _ \ \ /\ / / | |\/| |/ _ \ __/ _` | ' 
    echo '  / /  \ \| |  | | | (_) \ V  V /  | |  | |  __/ || (_| | ' 
    echo ' /_/    \_\_|  |_|  \___/ \_/\_/   |_|  |_|\___|\__\__,_| '
    echo '..........................................................'
    echo " - $1 -"
    echo '..........................................................'
    echo ''
    echo ''
    echo '             |                        '
    echo '             |                        '
    echo '             |                        '
    echo '             |<-------                '
    echo '             |        |               '
    echo '             |        |               '
    echo '             |        |               '
    echo '             |--------                '
    echo '             |     release/version    '
    echo '             |                        '
    echo '             |                        '
    echo '           master                     '
    echo ''
    echo '..........................................................'
    echo ''
    echo ''
}

function jump_to_master_and_pull()
{
    read -p "Jump to master branch and pull. Accept? [Enter] " -n 1 KEY
    if [ "$KEY" != "" ]; then
        echo -e "\nAborted"
        exit 0
    fi
    echo -e "\nJumping to master branch and pulling (it can take a few seconds)..."
    git checkout master
    if [[ $? -ne 0 ]]; then
        echo "Check the error and try again"
        exit $0
    fi
    git pull origin master
}
