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

The Compiler DSL offers the low level functional API that interfaces directly with the compiler.
In this API the user is given the chance to interface with all compiler phases and steps of compilation.
Interfacing with the compiler alongside the compilation process allows the user to alter the compilation outcome by
interacting with the compiler configuration, analysis, resolution and code generation phases.

[Compiler video with meta]
[Talk from LW segment where we explain this part]

#### Config

The configuration phase allows changing the compiler configuration prior to compilation.
In this phase we can programmatically activate and change all compiler flags and system
properties the compiler uses to enable/disable the different features in compilation.

##### updateConfig

The [updateConfig] function provides access to the [CompilerConfiguration] which contains the map of properties used
to enable/disable the different features in compilation.

##### storageComponent

The [storageComponent] function allows access to the [StorageComponentContributor]. 
This is the Dependency Injector and service registry the compiler uses in all phases.
In this function you can register new services or modify existing ones before the container is composed and sealed prior to compilation.

##### typeChecker

The [typeChecker] function allows the user to provide a custom implementation of the [TypeChecker].
By replacing the type checker but still having access to the default one the user can define new subtype relationships or type equality.

##### enableIr

The [enableIr] function enables the Intermediate Representation Backend. 
The IR Backend is a part of the code generation phase and emits code in the IR format.
The IR Format is a tree structure with significant indentation that contains all the information needed to generate bytecode
for all platforms the Kotlin programming language targets.

When the IR backend is disabled which is the current default in the Kotlin Compiler, the [JVM ASM Backend] is used instead.

[IR Example]

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
