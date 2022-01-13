---
layout: docs
title: Overview
permalink: /
video: WKR384ZeBgk
---

[![Latest snapshot](https://img.shields.io/maven-metadata/v?color=0576b6&label=latest%20snapshot&metadataUrl=https%3A%2F%2Foss.sonatype.org%2Fservice%2Flocal%2Frepositories%2Fsnapshots%2Fcontent%2Fio%2Farrow-kt%2Farrow-meta%2Fmaven-metadata.xml)](https://oss.sonatype.org/service/local/repositories/snapshots/content/io/arrow-kt/arrow-meta/)
[![Publish artifacts](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Artifacts/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Artifacts%22)
[![Publish documentation](https://github.com/arrow-kt/arrow-meta/workflows/Publish%20Documentation/badge.svg)](https://github.com/arrow-kt/arrow-meta/actions?query=workflow%3A%22Publish+Documentation%22)
[![Kotlin version badge](https://img.shields.io/badge/kotlin-1.5-blue.svg)](https://kotlinlang.org/docs/whatsnew15.html)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Functional companion to Kotlin's Compiler & IDE

> Λrrow Meta is a meta-programming library designed to build compiler plugins for Kotlin.

## Built with Meta

### [Analysis]({{ '/analysis' | relative_url }})

Λrrow Analysis extends the capabilities of the Kotlin compiler with compile-time checked pre and post-conditions for functions, and invariants for types and mutable variables. This allows Λrrow Analysis to detect many common types of mistakes (like out-of-bounds indexing), which you can extend with additional checks for your particular domain.

[Get started with Λrrow Analysis]({{ '/analysis' | relative_url }})

## Previously built with Meta

These plug-ins are no longer developed as part of the Λrrow Meta organization.

### [Optics](https://arrow-kt.io/docs/optics/)

[Λrrow Optics](https://arrow-kt.io/docs/optics/) provide a nice DSL to query and transform immutable values. A compiler plug-in to provide automatic generation of the necessary boilerplate was previously built with Meta, but it has been replaced by [KSP](https://github.com/google/ksp).

### Proofs

Λrrow Proofs provides a way to declare contextual values, which are resolved at compile-time by the plug-in. Dependency Injection without the hassle! Λrrow Proofs is no longer in development.