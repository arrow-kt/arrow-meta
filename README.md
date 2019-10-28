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

## License

## Credits

**Build and run tests**

```
./gradlew buildMeta -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```

**Build and run test + IDE plugin**

```
./gradlew publishAndRunIde -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```
