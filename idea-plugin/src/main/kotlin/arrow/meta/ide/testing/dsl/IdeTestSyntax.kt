package arrow.meta.ide.testing.dsl

import arrow.meta.ide.testing.dsl.folding.FoldingTestSyntax
import arrow.meta.ide.testing.dsl.icon.IconProviderTestSyntax
import arrow.meta.ide.testing.dsl.inspection.InspectionTestSyntax
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerTestSyntax
import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import arrow.meta.ide.testing.env.git.GitSyntax
import arrow.meta.ide.testing.env.gradle.GradleSyntax
import arrow.meta.ide.testing.env.resolution.ResolutionSyntax

interface IdeTestSyntax : IdeTestTypeSyntax, LineMarkerTestSyntax, IconProviderTestSyntax,
  InspectionTestSyntax, ResolutionSyntax, FoldingTestSyntax, GitSyntax, GradleSyntax