Type Proofs - Extending the Kotlin Type System

TypeProofs is a new compiler plugin built on Arrow Meta that enables new features in the Kotlin type system.
Type Classes, Union Types, Type Refinements, and many other extensions to the Kotlin type system are now possible.

Type Proofs are propositions to the Kotlin type system expressed as extension functions that unlock new relationships
between types ad-hoc while remaining fully compatible with sub-type polymorphism and the existing inheritance type system.

Type Proofs brings to Kotlin first class idiomatic syntax for functional programming and replaces the need for Dependency Injection containers resolving injection of components and verifying applications dependency graphs at compile time.

At the root of this design is the Curry-Howard-Lambek Correspondence where we can establish a relationship between two types that acts as proposition, and a function that acts as morphism or proof of the relationship.

Attendants to this talk will learn about all these new type system features that the Arrow and 47 Degrees team is proposing as KEEPs in 2020.
