# Arrow Meta

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Farrow-kt%2Farrow-meta%2Fbadge%3Fref%3Dmaster&style=flat)](https://actions-badge.atrox.dev/arrow-kt/arrow-meta/goto?ref=master)

## Documentation

### Getting started
#### Hello World Compiler Plugin
#### Hello World Compiler + IDE Plugin

### Quote Templates DSL
##### quote
##### classOrObject
##### func

### Compiler DSL

#### Config
##### updateConfig
##### storageComponent
##### enableIr

#### Analysis
##### additionalSources
##### analysys
##### preprocessedVirtualFileFactory
##### extraImports
##### suppressDiagnostic

#### Resolve
##### declarationAttributeAlterer
##### packageFragmentProvider
##### syntheticScopes
##### syntheticResolver

#### Codegen
##### ASM
###### codegen
##### IR
###### IrGeneration

### IDE DSL

## Plugins

### Higher Kinded Types
### Type classes
### Comprehensions
### Optics

## Use cases

## Contributing

Arrow-meta project is split into 4 packages:
- compiler-plugin: Kotlin compiler plugin
- gradle-plugin: Plugin for Gradle for activating Kotlin compiler plugin
- idea-plugin: Plugin for Intellij IDEA for IDE integration (icons, autocomplete etc.)
- testing-plugin: Tools for testing compiler and idea plugin

Quick start development environment:

- Run `publishAndRunIde` Gradle task
- Import sample project () in new IDE instance

When updating:
- compiler-plugin: Run `buildMeta` Gradle task and refresh Gradle in second IDE instance
- gradle-plugin: Run `buildmeta` gradle task and refresh gradle in second ide instance
- idea-plugin: Stop and run `publishAndRunIde` or `:idea-plugin:runIde` task (live plugin refresh may be available in 2020.1: https://twitter.com/intelliyole/status/1187715664263421953)

Debugging:

- compiler and gradle plugin:
    - Add following line to `properties.gradle` in project opened in second ide:
       ```
       org.gradle.jvmargs=-Dkotlin.compiler.execution.strategy="in-process" -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5006
       ```
    - Add `Remote` task in main IDE with host `localhost` and port `5006`
    - Refresh Gradle in second IDE and run build (for gradle daemon to restart and open debug port)
    - Debug remote task in first IDE

- idea-plugin:
    - Debug `:idea-plugin:runIde` gradle task in main IDE

## License

## Credits
