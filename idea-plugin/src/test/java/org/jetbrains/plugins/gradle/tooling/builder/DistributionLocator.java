/*
 * Copyright 2000-2014 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.gradle.tooling.builder;

import com.intellij.testFramework.UsefulTestCase;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

public class DistributionLocator {
    private static final String RELEASE_REPOSITORY_ENV = "GRADLE_RELEASE_REPOSITORY";
    private static final String SNAPSHOT_REPOSITORY_ENV = "GRADLE_SNAPSHOT_REPOSITORY";
    private static final String INTELLIJ_LABS_GRADLE_RELEASE_MIRROR =
            "https://services.gradle.org-mirror.labs.intellij.net/distributions";
    private static final String INTELLIJ_LABS_GRADLE_SNAPSHOT_MIRROR =
            "https://services.gradle.org-mirror.labs.intellij.net/distributions-snapshots";
    private static final String GRADLE_RELEASE_REPO = "https://services.gradle.org/distributions";
    private static final String GRADLE_SNAPSHOT_REPO = "https://services.gradle.org/distributions-snapshots";

    @NotNull
    private final String myReleaseRepoUrl;
    @NotNull
    private final String mySnapshotRepoUrl;

    public DistributionLocator() {
        this(DistributionLocator.getRepoUrl(false), DistributionLocator.getRepoUrl(true));
    }

    public DistributionLocator(@NotNull String releaseRepoUrl, @NotNull String snapshotRepoUrl) {
        myReleaseRepoUrl = releaseRepoUrl;
        mySnapshotRepoUrl = snapshotRepoUrl;
    }

    @NotNull
    public URI getDistributionFor(@NotNull GradleVersion version) throws URISyntaxException {
        return getDistribution(getDistributionRepository(version), version, "gradle", "bin");
    }

    @NotNull
    private String getDistributionRepository(@NotNull GradleVersion version) {
        return version.isSnapshot() ? mySnapshotRepoUrl : myReleaseRepoUrl;
    }

    private static URI getDistribution(@NotNull String repositoryUrl,
                                       @NotNull GradleVersion version,
                                       @NotNull String archiveName,
                                       @NotNull String archiveClassifier) throws URISyntaxException {
        return new URI(String.format("%s/%s-%s-%s.zip", repositoryUrl, archiveName, version.getVersion(), archiveClassifier));
    }

    @NotNull
    public static String getRepoUrl(boolean isSnapshotUrl) {
        final String envRepoUrl = System.getenv(isSnapshotUrl ? SNAPSHOT_REPOSITORY_ENV : RELEASE_REPOSITORY_ENV);
        if (envRepoUrl != null) return envRepoUrl;

        if (UsefulTestCase.IS_UNDER_TEAMCITY) {
            return isSnapshotUrl ? INTELLIJ_LABS_GRADLE_SNAPSHOT_MIRROR : INTELLIJ_LABS_GRADLE_RELEASE_MIRROR;
        }

        return isSnapshotUrl ? GRADLE_SNAPSHOT_REPO : GRADLE_RELEASE_REPO;
    }
}