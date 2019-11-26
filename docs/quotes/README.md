---
layout: docs
title: Quotes
permalink: /quotes
---

# Quotes

Quotes can ultimately be used for PSI element -> String transformations and vice-versa. For example: 

```kotlin
ifExpression({ true }) { e ->
     Transform.replace(
      replacing = e,
        newDeclaration = """ if $`(condition)` $then $`else` """.`if`
       )
}
```

Below you can find a comprehensive map between Kotlin's PSI element constructs and various quotes in arrow.meta.quotes. If something's missing here, please let us know [here in our issue tracker](https://github.com/arrow-kt/arrow-meta/issues).

## ClassOrObject (quotes.classorobject)

|  Quote                                                                                                                        |  Declaration 
| ------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [Class](/docs/apidocs/compiler-plugin/arrow.meta.quotes/classorobject/class.html)                                             | ```"""$`@annotations` $kind $name $`(typeParameters)` $`(params)` : $supertypes"} { $body""".`class` ```    |
| [ObjectDeclaration](/docs/apidocs/compiler-plugin/arrow.meta.quotes/classorobject/object-declaration.html)                    |                                                                                                             |

## Elements (quotes.element)

|  Quote                                                                                                                         |  Declaration 
| -------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [CatchClause](/docs/apidocs/compiler-plugin/arrow.meta.quotes/element/catch-clause.html)                                       | ```""" catch $`(parameter)` $`{ body }` """.catch```                                                        |
| [FinallySection](/docs/apidocs/compiler-plugin/arrow.meta.quotes/element/finally-section.html)                                 | ```""" $finally """.finally```                                                                              |
| [WhenCondition](/docs/apidocs/compiler-plugin/arrow.meta.quotes/element/when-condition.html)                                   |  ```""" $condition """.whenCondition```                                                                     |
| [WhenEntry](/docs/apidocs/compiler-plugin/arrow.meta.quotes/element/when-entry.html)                                           | ```""" $conditions -> $expression """.whenEntry```                                                          |

## Expressions (quotes.expression)

|  Quote                                                                                                                         |  Declaration 
| -------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [AnnotatedExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/annotated-expression.html)                    | ```""" $`@annotations` $expression """.annotatedExpression```                                               |
| [BinaryExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/binary-expression.html)                          |                                                                                                             |
| [BlockExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/block-expression.html)                            | ```""" { $statements } """.block```                                                                         |
| [ForExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/for-expression.html)                                | ```""" for $`(param)` { $body } """.`for` ```                                                               |
| [IfExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/if-expression.html)                                  | ```""" if $`(condition)` $then $`else` """.`if` ```                                                         |
| [IsExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/is-expression.html)                                  | ```""" $left $operation $type """.`is` ```                                                                  |
| [ThrowExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/throw-expression.html)                            | ```""" $`throw` """.`throw` ```                                                                             |
| [TryExpression](docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/try-expression.html)                                 | ```""" try $tryBlock $catch $finally """.`try` ```                                                          |
| [WhenExpression](docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/when-expression.html)                               | ```"""when $`(expression)` { $entries $`else`} """.`when` ```                                               |
| [WhileExpression](docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/while-expression.html)                             | ```""" while $`(condition)` { $body } """.`while` ```                                                       |

### Expressions with Labels (quotes.expression.expressionwithlabel)

|  Quote                                                                                                                         |  Declaration 
| -------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [PropertyAccessor](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/expressionwithlabel/property-accessor.html)      |                                                                                                             |             
| [ReturnExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/expression/expressionwithlabel/return-expression.html)      |                                                                                                             |

## ModifierList (quotes.modifierlist)

|  Quote                                                                                                                         |  Declaration 
| -------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [PropertyAccessor](/docs/apidocs/compiler-plugin/arrow.meta.quotes/modifierlist/modifier-list.html)                            |  ```""" $`@annotations` $modifier value """.`modifierList` ```                                              |             
| [ReturnExpression](/docs/apidocs/compiler-plugin/arrow.meta.quotes/modifierlist/return-expression.html)                        |                                                                                                             |

## NamedDeclaration (quotes.nameddeclaration)

|  Quote                                                                                                                         |  Declaration 
| -------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [Parameter](/docs/apidocs/compiler-plugin/arrow.meta.quotes/nameddeclaration/parameter.html)                                   |                                                                                                             |  

### NamedDeclaration TypeParameterListOwner (quotes.nameddeclaration.typeparameterlistowner)

|  Quote                                                                                                                         |  Declaration 
| -------------------------------------------------------------------------------------------------------------------------------| ----------------------------------------------------------------------------------------------------------- |
| [NamedFunction](/docs/apidocs/compiler-plugin/arrow.meta.quotes/nameddeclaration/typeparameterlistowner/modifier-list.html)    |  ```""" $`@annotations` $modifier value """.`modifierList` ```                                              |             
| [Property](/docs/apidocs/compiler-plugin/arrow.meta.quotes/nameddeclaration/typeparameterlistowner/property.html)              |                                                                                                             |
| [TypeAlias](/docs/apidocs/compiler-plugin/arrow.meta.quotes/nameddeclaration/typeparameterlistowner/type-alias.html)           |                                                                                                             |