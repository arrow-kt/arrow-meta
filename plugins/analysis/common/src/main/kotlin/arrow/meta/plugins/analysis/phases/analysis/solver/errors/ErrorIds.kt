package arrow.meta.plugins.analysis.phases.analysis.solver.errors

enum class SeverityLevel {
  Error,
  Warning,
  Info
}

sealed interface ErrorIds {

  val name: String
  val id: String
    get() = name
  val shortDescription: String
  val level: SeverityLevel
    get() = SeverityLevel.Error

  enum class Parsing : ErrorIds {
    ErrorParsingPredicate,
    UnexpectedReference,
    UnexpectedFieldInitBlock,
    LawMustCallFunction,
    LawMustHaveParametersInOrder,
    SubjectWithoutName,
    CouldNotResolveSubject;

    override val shortDescription: String
      get() =
        """
          These errors arise from `pre`, `post`, or `invariant` blocks which cannot be translated into
          SMT formulae.
         
          For example, we cannot translate method calls to SMT:
         
          ```kotlin
          fun f(xs: List[String]): String {
            pre({ !xs.get(0).isEmpty() }) { ... }
            ...
            }
          ```
         
          The compiler won't catch these errors in its own analysis phase (like it would do with a
          type error), since this is perfectly good code. However, it seems desirable for the
          programmer to know that a particular language feature cannot be used in these blocks.
        """.trimIndent()
  }

  enum class Unsupported : ErrorIds {
    UnsupportedImplicitPrimaryConstructor,
    UnsupportedExpression;

    override val shortDescription: String
      get() =
        """
        These are warning which are attached to those elements which are not supported by the analysis (yet).
      """.trimIndent()

    override val level: SeverityLevel
      get() = SeverityLevel.Warning
  }

  enum class Unsatisfiability : ErrorIds {
    UnsatCallPre {
      override val shortDescription: String
        get() =
          """
          `UnsatCallPre` (attached to the argument): The required pre-conditions for a (method,
          property, function) call are not satisfied.
          
          For example:
          
          ```kotlin
            val wrong = 1 / 0  // does not satisfy '0 != 0' in Int.div law
          ```
        """.trimIndent()
    },
    UnsatBodyPost {
      override val shortDescription: String
        get() =
          """
           (attached to the return value)
          
           the post-condition declared in a function body is not true.
          
           For example:
          
           ```kotlin
           fun f(x: Int): Int {
             pre(x >= 0) { "non-negative" }
             val r = x + x
             return r.post({ it > 1 }) { "greater than 1" }
             // does not satisfy 'x + x > 1'
           }
           ```
        """.trimIndent()
    },
    UnsatInvariants {
      override val shortDescription: String
        get() =
          """
          (attached to the new value): the invariant declared for a mutable variable is not satisfied
          by the new value.
          
          For example:
          
          ```kotlin
           fun g(): Int {
             var r = 1.invariant({ it > 0 }) { "it > 0" }
             r = 0 // does not satisfy '0 > 0'
             ...
           }
          ```
        """.trimIndent()
    };

    override val shortDescription: String
      get() =
        """
          These errors embody the idea that "something should have been true, but it is not." 
      """.trimIndent()
  }

  enum class Inconsistency : ErrorIds {
    InconsistentBodyPre {
      override val shortDescription: String
        get() =
          """
           The set of pre-conditions given to the function leaves no possible way to call the function.
           
           For example:
           
           ```kotlin
            fun h(x: Int): Int {
              pre({ x > 0 }) { "greater than 0" }
              pre({ x < 0 }) { "smaller than 0" }
              // no value can be both < 0 and > 0
              ...
            }
           ```
        """.trimIndent()
    },
    InconsistentDefaultValues {
      override val shortDescription: String
        get() =
          """
           The default values do not satisfy the pre-conditions.
          
           For example:
          
           ```kotlin
            fun h(x: Int = 0): Int {
              pre({ x > 0 }) { "greater than 0" }
              ...
            }
           ```
        """.trimIndent()
    },
    InconsistentConditions {
      override val shortDescription: String
        get() =
          """
           (attached to a particular condition): the body of a branch is never executed, because the
           condition it hangs upon conflicts with the rest of the information about the function.
           
           For example, if a condition goes against a pre-condition:
           
           ```kotlin
             fun i(x: Int): Int {
               pre({ x > 0 }) { "greater than 0" }
               if (x == 0) {
                 // 'x > 0' and 'x == 0' are incompatible
                 // so this branch is unreachable
               } else {
                 ...
               }
             }
           ```
        """.trimIndent()
    },
    InconsistentCallPost {
      override val shortDescription: String
        get() =
          """
          (attached to the function call): the post-conditions gathered after calling a function imply
          that this function could not be called at all. _This is really uncommon in practice_.
        """.trimIndent()
    },
    InconsistentInvariants {
      override val shortDescription: String
        get() =
          """
           (attached to a local declaration): there is no way in which the invariant attached to a
           declaration may be satisfied.
          
           For example:
          
           ```kotlin
            fun j(x: Int): Int {
              pre({ x > 0 }) { "greater than 0" }
              var v = 3.invariant ({ v > x && v < 0 }) { "v > x && v < 0" }
            }
          
           ```  
        """.trimIndent()
    };

    override val shortDescription: String
      get() =
        """
        These errors embody the idea that "there's no possible way in which we may end up in this
        situation." Usually this means that the code is somehow unreachable. There are four cases in
        which this may arise.
      """.trimIndent()
  }

  enum class Liskov : ErrorIds {
    NotWeakerPrecondition {
      override val shortDescription: String
        get() = """
          
        """.trimIndent()
    },
    NotStrongerPostcondition {
      override val shortDescription: String
        get() = """
          
        """.trimIndent()
    }
  }

  enum class Exception : ErrorIds {
    IllegalState {
      override val shortDescription: String
        get() = """
          
        """.trimIndent()
    },
    OtherException {
      override val shortDescription: String
        get() = """
          
        """.trimIndent()
    }
  }
}
