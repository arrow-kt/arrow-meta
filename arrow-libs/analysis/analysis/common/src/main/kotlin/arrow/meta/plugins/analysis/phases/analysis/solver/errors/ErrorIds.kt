package arrow.meta.plugins.analysis.phases.analysis.solver.errors

enum class SeverityLevel {
  Error,
  Warning,
  Info,
  Unsupported
}

sealed interface ErrorIds {

  val name: String
  val id: String
    get() = name
  val shortDescription: String
  val fullDescription: String?
    get() = null
  val level: SeverityLevel
    get() = SeverityLevel.Error

  enum class Parsing : ErrorIds {
    ErrorParsingPredicate,
    WarningParsingPredicate,
    UnexpectedReference,
    UnexpectedFieldInitBlock;

    override val shortDescription: String
      get() = "The predicate could not be translated into a SMT formula"

    override val fullDescription: String
      get() =
        """
          The corresponding predicate -- in `pre`, `post`, or `invariant` blocks -- could not be
          translated to a SMT formula, that is, it is not supported by the solver used
          internally by the analysis. For example, we cannot translate method calls to SMT:
         
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

  enum class Laws : ErrorIds {
    LawMustCallFunction,
    LawMustHaveParametersInOrder,
    SubjectWithoutName,
    CouldNotResolveSubject;

    override val shortDescription: String
      get() = "Incorrect definition of a law"

    override val fullDescription: String
      get() =
        """
          The definition of a law by means of a `@Law` annotation does not satisfy the requirements:
          
          - It must call **exactly one** function at the end,
          - The call must use the parameters **in order**.
        """.trimIndent()
  }

  enum class Unsupported : ErrorIds {
    UnsupportedExpression;

    override val shortDescription: String
      get() = "This element is not (yet) supported"

    override val level: SeverityLevel
      get() = SeverityLevel.Unsupported
  }

  enum class Unsatisfiability : ErrorIds {
    UnsatCallPre {
      override val shortDescription: String
        get() = "A pre-condition for a (method, property, function) is are not satisfied"

      override val fullDescription: String
        get() =
          """
          The required pre-conditions for a (method, property, function) call are not satisfied.
          For example:
          
          ```kotlin
            val wrong = 1 / 0  // does not satisfy '0 != 0' in Int.div law
          ```
        """.trimIndent()
    },
    UnsatBodyPost {
      override val shortDescription: String
        get() = "A post-condition declared in the function body does not hold"

      override val fullDescription: String
        get() =
          """
           The post-condition declared in a function body cannot be proven true.
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
        get() = "The invariant declared for a mutable variable is not satisfied by a value"

      override val fullDescription: String
        get() =
          """
          The invariant declared for a mutable variable is not satisfied a value,
          either the first one or in a new assignment. For example:
          
          ```kotlin
           fun g(): Int {
             var r = 1.invariant({ it > 0 }) { "it > 0" }
             r = 0 // does not satisfy '0 > 0'
             ...
           }
          ```
        """.trimIndent()
    }
  }

  enum class Inconsistency : ErrorIds {
    InconsistentBodyPre {
      override val fullDescription: String
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
      override val fullDescription: String
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
      override val fullDescription: String
        get() =
          """
           The body of a branch is never executed, because the condition it hangs upon conflicts
           with the rest of the information about the function.
           This may arise, for example, if a condition goes against a pre-condition:
           
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

      override val level: SeverityLevel
        get() = SeverityLevel.Warning
    },
    InconsistentCallPost {
      override val fullDescription: String
        get() =
          """
          The post-conditions gathered after calling a function imply
          that this function could not be called at all. 
          _This is really uncommon in practice_.
        """.trimIndent()

      override val level: SeverityLevel
        get() = SeverityLevel.Warning
    },
    InconsistentInvariants {
      override val fullDescription: String
        get() =
          """
           There is no way in which the invariant attached to a declaration may be satisfied.
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
      get() = "Inconsistent set of conditions (usually means unreachable code)"
  }

  enum class Liskov : ErrorIds {
    NotWeakerPrecondition {
      override val shortDescription: String
        get() = "The pre-condition is not weaker than its parent's"

      override val fullDescription: String
        get() =
          """
            The pre-condition of an overriden method must be **weaker** than
            the one declared in its parent. This guarantees that we can
            always replace a call to the parent with a call to the child
            (Liskov Substitution Principle).
          """.trimIndent()
    },
    NotStrongerPostcondition {
      override val shortDescription: String
        get() = "The post-condition is not stronger than its parent's"

      override val fullDescription: String
        get() =
          """
            The post-condition of an overriden method must be **stronger** than
            the one declared in its parent. This guarantees that we can
            always replace a call to the parent with a call to the child
            (Liskov Substitution Principle).
          """.trimIndent()
    }
  }

  enum class Exception : ErrorIds {
    IllegalState,
    OtherException;

    override val shortDescription: String
      get() = "Internal error during analysis"
  }
}
