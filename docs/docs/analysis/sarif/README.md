---
layout: docs-analysis
title: Analysis - GitHub Actions / SARIF
permalink: /analysis/sarif:output_ext
---

# GitHub Actions / SARIF

⚠️ **This is still an alpha feature.**

Λrrow Analysis generates a so-called SARIF report as part of its output. [SARIF](https://sarifweb.azurewebsites.net/) is a standard interchange format for static analysis, which can be later consumed by many different tools. When using the Gradle plug-in, the file can be found in the `build/generated` folder.

One very useful integration is with [GitHub's Code Scanning](https://docs.github.com/en/code-security/code-scanning/integrating-with-code-scanning/about-integration-with-code-scanning). In that case the results of the analysis appear as part of the workflow output, annotating the source code itself. You can see [an example](https://github.com/arrow-kt/backend-arrow-example/pull/6/checks?check_run_id=4497725364) for an "unsatisfiable pre-condition" error.

> ℹ️ Unfortunately, you need to pay for Code Scanning in private repos. For public ones you can enable it in the _Security & analysis_ tab in the repo settings.

The following snippet shows how to configure your GitHub Action workflow to make it aware of Code Scanning. We assume that you've [configured your Gradle project to run Λrrow Analysis](({{ '/analysis' | relative_url }}) as part of the `build` task. The next step is to take all the SARIF files and put them in a single folder, what we call "bundle analysis report" here. Finally you use the [`upload-sarif` action](https://docs.github.com/en/code-security/code-scanning/integrating-with-code-scanning/uploading-a-sarif-file-to-github), passing the name of the folder where you've gathered the SARIF files. We use `if: always()` because otherwise the results would only be uploaded on a succesful run, which is the least interesting scenario for an analysis tool.

```yaml
jobs:
  
  build_artifacts:
    
    steps:
      - first_steps
      
      - name: Build and test with Gradle
        run: ./gradlew build

      - more_steps

      - name: Bundle analysis report
        if: always()
        run: mkdir sarif && find . -name '*.sarif' | xargs -I{} cp "{}" ./sarif/

      - name: Upload analysis report
        if: always()
        uses: github/codeql-action/upload-sarif@v1
        with:
          sarif_file: sarif # path relative to root
```