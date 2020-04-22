# Type Proofs
- A Visual chart that displays proofs connection between proofed types using nomnoml or diagram plugin 
- Dialog over proof icons that display the effect the proof has with examples
- Include implicit call sites in the usages of proofs when using Find Usages
## Type Classes
- Show an extension is connecting the behavior with the datatype with a type class icon.
- A visual clue for a call site where a type class gets injected
- An intention to make the implicit injection explicit
- An intention to make the explicit injection implicit
## Coercion functions
- A visual representation when a type coercion occurs by proof
- An intention to make the implicit coercion explicit
- An intention to make the explicit coercion implicit
## Union Types
- Simplify display of `Union2<A, B>` to `A | B` when displayed in the editor and back to edit mode when focused
## Union Types
- Simplify display of `Tuple2<A, B>` to `(A, B)` when displayed in the editor and back to edit mode when focused
## Refined Types
- A visual representation for refined classes + dialog.
- A visual representation for safe/unsafe usage of call sites that abide to the refinement.
- A visual representation for each one of the refined predicates that showcases they are compile time verified.