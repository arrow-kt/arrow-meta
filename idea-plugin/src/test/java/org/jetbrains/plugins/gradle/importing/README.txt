Sources of this package:
https://github.com/JetBrains/intellij-community/tree/master/plugins/gradle/java/testSources/importing

Changes:
- GradleImportingTestCase
  - specified a single version for gradle,
    // patched for Arrow Meta
    public static final String BASE_GRADLE_VERSION = "6.0.1"
  - updated set of gradle versions, which are used for test execution:
    // patched for Arrow Meta
    return Collections.singletonList(new Object[]{BASE_GRADLE_VERSION});
