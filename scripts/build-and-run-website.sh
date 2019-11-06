#!/bin/bash

cd $(dirname $0)/.. 
./gradlew dokka :docs:runAnk
bundle install --gemfile docs/Gemfile --path vendor/bundle
BUNDLE_GEMFILE=docs/Gemfile bundle exec jekyll serve -s docs
