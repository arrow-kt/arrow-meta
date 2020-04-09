package arrow.meta.ide.testing.dsl

import arrow.meta.ide.testing.dsl.icon.IconProviderTestSyntax
import arrow.meta.ide.testing.env.resolution.ResolutionSyntax
import arrow.meta.ide.testing.dsl.inspection.InspectionTestSyntax
import arrow.meta.ide.testing.dsl.lineMarker.LineMarkerTestSyntax
import arrow.meta.ide.testing.env.IdeTestTypeSyntax

interface IdeTestSyntax : IdeTestTypeSyntax, LineMarkerTestSyntax, IconProviderTestSyntax, InspectionTestSyntax,
  ResolutionSyntax