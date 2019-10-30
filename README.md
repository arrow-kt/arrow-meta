# Arrow Meta

Functional companion to Kotlin's Compiler & IDE

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Farrow-kt%2Farrow-meta%2Fbadge%3Fref%3Dmaster&style=flat)](https://actions-badge.atrox.dev/arrow-kt/arrow-meta/goto?ref=master)

## Documentation

### Getting started

Arrow Meta is a meta-programming library that cooperates with the Kotlin compiler in all it's phases bringing its full power to the community.
Writing compiler plugins, source transformations, IDEA plugins, linters, type search engines, automatic code refactoring,... are just a few of the [use cases](#use-cases) of the things that can be accomplished with Meta.

#### Creating your first compiler plugin

#### Project Setup

#### Hello World Compiler Plugin

The following example shows a Hello World Compiler Plugin. 
The Hello World plugin auto implements the `helloWorld` function by rewriting the Kotlin AST before the compiler proceeds.

```kotlin
val Meta.helloWorld: Plugin
  get() =
    "Hello World" {
      meta(
        func({ name == "helloWorld" }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """|fun helloWorld(): Unit = 
               |  println("Hello ΛRROW Meta!")
               |""".function.synthetic
          )
        }
      )
    }
```

For any user code whose function name is `helloWorld` our compiler plugin will replace the matching function for a
function that returns Unit and prints our message.

```kotlin:diff
-fun helloWorld(): Unit = TODO()
+fun helloWorld(): Unit = 
+  println("Hello ΛRROW Meta!")
```

#### Hello World Compiler + IDE Plugin
#### Arrow Meta 💚 Kotlin Compiler
#### Anatomy of a Meta Plugin

### Quote Templates DSL

The Kotlin DSL `func` is part of our **Quotes** system. The **Quotes** system acts as an intermediate 
layer between PSI Elements and AST Node parsing. More namely, A declaration quasi quote matches tree 
in the synthetic resolution and gives users the chance to transform them before they are processed 
by the Kotlin compiler.

The quote system may take the code that user writes, analyzes and detects code at the PSI Element level, and 
with the `Transform` system, the code can be changed at the level the user
sees the code.

##### quote
##### classOrObject
##### func

You may have noticed the following function previously mentioned in our `helloWorld` plugin.  

```kotlin
val Meta.helloWorld: Plugin
  get() =
    "Hello World" {
      meta(
        func({ name == "helloWorld" }) { c ->  // <-- func(...) {...}          Transform.replace(
            replacing = c,
            newDeclaration =
            """|fun helloWorld(): Unit = 
               |  println("Hello ΛRROW Meta!")
               |""".function.synthetic
          )
        }
      )
    }
```

A quote extension of Meta may look like this, as shown in the
`func` extension function:

```
fun Meta.func(
  match: KtNamedFunction.() -> Boolean, // some KtElement predicate for matching
  map: FuncScope.(KtNamedFunction) -> Transform<KtNamedFunction>  // applies the transformative function 
): ExtensionPhase =
  quote(match, map) { FuncScope(it) }
```


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
With a custom [TypeChecker] we can redefine what subtyping and type equality means.

##### enableIr

The [enableIr] function enables the Intermediate Representation Backend. 
The IR Backend is a part of the code generation phase and emits code in the IR format.
The IR Format is a tree structure with significant indentation that contains all the information needed to generate bytecode
for all platforms the Kotlin programming language targets.

When the IR backend is disabled which is the current default in the Kotlin Compiler, the [JVM ASM Backend] is used instead.

[IR Example]

#### Analysis

The Analysis phase determines if the parsed AST type checks and resolves properly.
As part of this phase we have access to events happening before and after resolution.
Before resolution we are giving the chance to modify the compiler trees in the form of `KtElement` via the [Quote Template System].

##### additionalSources [CLI]

[additionalSources] is a function that is invoked before resolution and allows us to provide an additional set of [KtFile] files.
These files will be considered as part of the compilation unit alongside the user sources.

##### analysis [CLI]

The [analysis] function allows us to intercept analysis before and after it happens altering the analysis input and outputs.
Altering the inputs on [doAnalysis] allows us to modify the compiler trees in the AST before they are considered for resolution.
This allows us to build the [Quote Template System] in this phase which is Arrow meta's higher level API.
Altering the output with [analysisCompleted] allows us to modify the binding trace and all elements resulting from analysis.

##### extraImports [CLI]

The [extraImports] function allows the user to provide an additional set of [org.jetbrains.kotlin.psi.KtImportInfo] imports for
each individual [KtFile] considered as sources. 
This additional set of imports are taken into account when resolving symbols in the resolution phase of a [KtFile].

##### suppressDiagnostic [CLI, IDE]

The [suppressDiagnostic] function allows to selectively determine whether a diagnostic emitted by the compiler affects compilation.
As the compiler performs resolution it will generate diagnostic of type [Diagnostic] with different [Severity] levels:
[Severity.INFO] [Severity.ERROR] and [Severity.WARNING].

When the [suppressDiagnostic] returns [true] the emitted diagnostic is suppressed and removed from the [BindingTrace].
This will cause the [Diagnostic] to not be considered in further compilation phases.

#### Resolve [CLI, IDE]

The Resolve phase is in charge of providing the meaning of the Kotlin Language to the structured trees discovered by the Kotlin parser.
Right up until [Analysis] we are just working with a tree structure, the AST. 
In resolution we proceed to type-check the AST and all its expressions associating each of them to a [DeclarationDescriptor].
A [DeclarationDescriptor] is a model that contains the type and kotlin structure as it understands our sources in the AST.

[Psi and Descriptor comparison gif]

##### packageFragmentProvider [CLI, IDE]

The [packageFragmentProvider] function allows us to provide synthetic descriptors for declarations of a [PackageFragmentDescriptor].
A [PackageFragmentDescriptor] holds all the information about declared members in a package fragment such as top level [typealiases], [functions], [properties] and [class] like constructs like [object] and [interface].

##### syntheticScopes [IDE]

The [syntheticScopes] function encapsulates a powerful interface that lets you peak and modify the resolution scope of 
constructors, extension functions, properties and static functions. 
Altering the synthetic scope we can provide our own descriptors to IntelliJ. 
These descriptors are required for IntelliJ IDEA to enable synthetic generated code that is required by IDE features such 
as autocompletion and code refactoring.

##### syntheticResolver [CLI, IDE]

The [syntheticResolver] extension allows the user to change the top level class and nested class descriptors requested by IntelliJ and some parts of the CLI compiler.
This interface will be incomplete if your plugin is producing top level declarations that are [typealiases], [functions] or [properties].
For the above cases we would need to combine it or entirely replace it with a [packageFragmentProvider] which can provide descriptors for those top level declarations.

#### Codegen

The codegen phase is where the compiler emits bytecode and metadata for the different platforms the Kotlin language targets.
In this phase by default the compiler would go into ASM codegen for the JVM or into IR codegen if IR is enabled.
[IR] is the Intermediate Representation format the new Kotlin compiler backend targets.

##### ASM

When the compiler goes to codegen and IR is not enabled by default it goes into the codegen phase for the JVM where it
uses the ASM libs to generate bytecode using the AST and associated descriptors coming from the resolution phase.

###### codegen
 
The [codegen] function allows us to interact with [applyFunction], [applyProperty] and [generateClassSyntheticParts].
Each one of these functions are invoked as the compiled and type checked tree of [KtElement] and [DeclarationDescriptor] is processed for codegen.
Here we can alter the bytecode emitted using the [Meta ASM DSL]. 
This DSL mirrors the [IR DSL] offering a match + transform function that allows us to alter the codegen tree.

##### IR

IR, The intermediate representation format is a structured text format with significant indentation that contains all the information the compiler knows about a program.
At this point the compiler knows how a program is declared in sources, what the typed expressions are and how each of the generic type
arguments are applied.
The compiler emits in this phase this information which can then be further processed by interpreters and compilers targeting any platform.

[IR Example]

###### IR DSL

The IR DSL provides a match + transform function for each one of the elements that can be intercepted in the IR phase.

[Link to API docs for IR functions]

### IDE DSL

The IDE DSL empowers library and compiler plugin authors to bring their features closer to the development experience.
Arrow Meta allows sharing the compiler plugin code with the IDE code so developers can reuse their compiler plugin functions in their IDE plugin.

The Arrow Meta IDE DSL models the entire set of interesting features the Kotlin IDE plugin offers and the IDEA plugin system exposes to interface with the editor.

The table below showcases the currently available functions and visual examples of what each one of them may produce.
The coding style remains cohesive around Meta always offering a match + transform function.
This is intentional so the API is all about intercepting desired elements and transforming them into desired results.

[Table of IDE DSL functions]

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
