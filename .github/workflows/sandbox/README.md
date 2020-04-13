# How to manage upcoming Kotlin Compiler versions

## Goal

Testing Arrow Meta Compiler Plugin with the latest Kotlin Compiler EAP version and the latest Kotlin Compiler DEV version.

## Context

Some latest Kotlin Compiler versions can imply changes into Arrow Meta Compiler Plugin.

However, not always those changes can be done in the current version of the repository.

## Steps

When solving problems for an upcoming version that are incompatible with the current version:

1. Create a patch file with all the changes:
```
$> git diff > <latest-version>.diff
```
2. Add that patch file into `.github/workflows/sandbox/` directory. For instance:
```
.github/workflows/sandbox/1.4.0-dev.diff
```

### What if a patch file for a related version already exists?

For instance, there are additional changes for `1.4.0-dev-325` and `1.4.0-dev-180.diff` already exists (both of them are related to `1.4.0` version).

Update `.github/workflows/sandbox/1.4.0-dev.diff` with the new required changes.

### What if there are failures when applying a patch for a development branch?

Follow these steps:

1. Create a stash entry for that patch:
```
git checkout master
git apply .github/workflows/sandbox/<version>.diff
git stash
```
2. Apply the stash entry on development branch:
```
git checkout <dev-branch>
git stash apply
```
3. Fix the conflicts
4. Update the patch:
```
git diff > .github/workflows/sandbox/<version>.diff
```
5. Commit the change

## What will it be done with those patch files automatically?

### Build check with every pull request

It will create a workspace for every patch file:

- Extract version from filename (e.g. `1.4.0-dev-180` for `.github/workflows/sandbox/1.4.0-dev-180.diff`).
- Replace `KOTLIN_VERSION` by that version in `gradle.properties`.
- Add Kotlin DEV repository (`https://dl.bintray.com/kotlin/kotlin-dev/`) in `build.gradle`.
- Apply changes from that patch file: `git apply <latest-version>.diff`.
- Check Arrow Meta Compiler Plugin build: `./gradlew clean :compiler-plugin:build`

You can follow that guideline in your workspace for doing tests with upcoming versions.

### Sync bot

#### When testing latest Kotlin EAP version

- Check if there are patch files for latest Kotlin EAP version (`major.minor.patch` version that appears before `-eap-`).
- If they exist, apply them before the build.

#### When testing latest Kotlin DEV version

- Check if there are patch files for latest Kotlin DEV version (`major.minor.patch` version that appears before `-dev-`).
- If they exist, apply them before the build.

#### When testing latest Kotlin release version + Kotlin IDEA plugin + Intellij IDEA

- Check if there are patch files for Kotlin release version (`major.minor.patch` version that appears before `-dev-` or `-eap-` in patch files).
- If they exist:
    - Apply them before the build.
    - Remove them.
    - Create a pull request with the changes from patch files, updated `gradle.properties` and removing the applied patch files (they are no longer necessary).
