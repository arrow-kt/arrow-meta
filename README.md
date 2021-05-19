# Λrrow Meta

[![Latest snapshot](https://img.shields.io/maven-metadata/v?color=0576b6&label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.sonatype.org%2Fservice%2Flocal%2Frepositories%2Fsnapshots%2Fcontent%2Fio%2Farrow-kt%2Farrow-meta%2Fmaven-metadata.xml)](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/arrow-meta/)
[![Publish artifacts](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Artifacts/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Artifacts%22)
[![Publish documentation](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Documentation/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Documentation%22)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.5-blue.svg)](https://kotlinlang.org/docs/reference/whatsnew15.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Functional companion to Kotlin's Compiler

## Getting started

Λrrow Meta is a meta-programming library that cooperates with the Kotlin compiler in all it's phases bringing its full power to the community.

Writing compiler plugins, source transformations, linters, type search engines, automatic code refactoring,... are just a few of the [use cases](#use-cases) of the things that can be accomplished with Meta.

<a href="http://www.youtube.com/watch?feature=player_embedded&v=WKR384ZeBgk
" target="_blank"><img src="http://img.youtube.com/vi/WKR384ZeBgk/0.jpg" 
alt="Lambda World 2019 - Arrow Meta - Enabling Functional Programming in the Kotlin Compiler" width="100%" border="10" /></a>

## Λrrow Meta examples

![Hello World Compiler Plugin Demo](docs/docs/img/demos/hello-world-compiler-plugin.gif)

Take a look at [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository for getting more details.

## Build and run in your local environment

Pre-requirements: JDK 8

**Build and run tests**

```
./gradlew buildMeta
```

**Generate API Doc and validate it**

```
./gradlew buildMetaDoc
```

**Run the docs in your local server**

```
./gradlew buildMetaDoc
bundle install --gemfile docs/Gemfile --path vendor/bundle
BUNDLE_GEMFILE=docs/Gemfile bundle exec jekyll serve -s docs/build/site/
```

## Testing

Λrrow Meta provides an easy way to write tests about plugins, quotes, etc. thanks to [Kotlin Compile Testing](https://github.com/tschuchortdev/kotlin-compile-testing), a library developed by [Thilo Schuchort](https://github.com/tschuchortdev).

## Contributing

Λrrow Meta is an inclusive community powered by awesome individuals like you. As an actively growing ecosystem, Λrrow Meta and its associated libraries and toolsets are in need of new contributors! We have issues suited for all levels, from entry to advanced, and our maintainers are happy to provide 1:1 mentoring. All are welcome in Λrrow Meta.

If you’re looking to contribute, have questions, or want to keep up-to-date about what’s happening, please follow us here and say hello!

- [#arrow-meta on Kotlin Slack](https://kotlinlang.slack.com/)

## Licence

```
Copyright (C) 2017 The Λrrow Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
