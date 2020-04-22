# CONTINUOUS INTEGRATION CHECKS

## 'Build Artifacts' check

### 'Check next version' step

Arrow Meta is a functional companion to Kotlin's Compiler & IDE so it's necessary to keep it updated with the following versions:

* Latest Kotlin release version
* Latest Kotlin DEV version
* Latest Kotlin EAP version 
* Latest Intellij IDEA version
* Latest Kotlin IDEA Plugin version

It's only possible to check `compiler-plugin` for **Kotlin DEV and EAP versions**  because correspondent **Kotlin IDEA Plugin version** is just available for Kotlin release versions.

What if you get an error with **Check next version** step on **Build Artifacts** check for your pull request?

Follow these steps:

1. Look for the following line in the log to discover the patch that is failing:
```
Checking .github/workflows/sandbox/<version>.diff ...
```
2. Create a stash entry for that patch:
```
git checkout master
git apply .github/workflows/sandbox/<version>.diff
git stash
```
3. Apply the stash entry on your development branch:
```
git checkout <dev-branch>
git stash apply
```
4. Fix the conflicts
5. Update the patch:
```
git add -u
git diff --staged > .github/workflows/sandbox/<version>.diff
```
6. Commit the change on the patch file:
```
git reset HEAD -- .
git add .github/workflows/sandbox/<version>.diff
git commit -m "Fix patch"
```
7. Discard your local changes
```
git checkout .
```

These files will be used by the Bot which will create pull requests when having new release versions available or issues when raising errors with latest versions (DEV, EAP or release). Find more details at [How to manage upcoming Kotlin Compiler versions](.github/workflows/sandbox/README.md).
