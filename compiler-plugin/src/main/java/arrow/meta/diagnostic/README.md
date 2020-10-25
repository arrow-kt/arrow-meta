Notes on Diagnostics: 

To successfully create a Diagnostics it needs an `_initializer` which can be seen in `MetaError's`.
The latter is needed as the private Member `name`, can not be instrumented directly to not cause the NPE.