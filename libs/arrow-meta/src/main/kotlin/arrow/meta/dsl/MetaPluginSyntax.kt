package arrow.meta.dsl

import arrow.meta.dsl.analysis.AnalysisSyntax
import arrow.meta.dsl.codegen.CodegenSyntax
import arrow.meta.dsl.config.ConfigSyntax
import arrow.meta.dsl.fir.FirSyntax
import arrow.meta.dsl.resolve.ResolveSyntax

interface MetaPluginSyntax : ConfigSyntax, AnalysisSyntax, ResolveSyntax, CodegenSyntax, FirSyntax
