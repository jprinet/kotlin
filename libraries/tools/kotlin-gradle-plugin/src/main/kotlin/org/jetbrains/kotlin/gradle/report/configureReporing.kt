/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.report

import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logger
import org.jetbrains.kotlin.gradle.plugin.PropertiesProvider
import org.jetbrains.kotlin.gradle.report.data.BuildExecutionDataProcessor
import org.jetbrains.kotlin.gradle.tasks.AbstractKotlinCompile
import org.jetbrains.kotlin.gradle.utils.isConfigurationCacheAvailable
import java.text.SimpleDateFormat
import java.util.*

internal fun reportingSettings(rootProject: Project): ReportingSettings {
    val properties = PropertiesProvider(rootProject)
    val buildReportType =
        properties.buildReportOutputs.mapNotNull { BuildReportType.values().firstOrNull { brt -> brt.name == it.toUpperCase() } }
    val buildReportMode =
        when {
            buildReportType.isEmpty() -> BuildReportMode.NONE
            else -> BuildReportMode.VERBOSE
        }
    val fileReportSettings = if (buildReportType.contains(BuildReportType.FILE)) {
        val buildReportDir = properties.buildReportFileOutputDir ?: rootProject.buildDir.resolve("reports/kotlin-build")
        val includeMetricsInReport = properties.buildReportMetrics || buildReportMode == BuildReportMode.VERBOSE
        FileReportSettings(buildReportDir = buildReportDir, includeMetricsInReport = includeMetricsInReport)
    } else {
        null
    }

    val httpReportSettings = if (buildReportType.contains(BuildReportType.HTTP)) {
        val url = properties.buildReportHttpUrl ?: throw IllegalStateException("Can't configure http report without url property")
        val password = properties.buildReportHttpPassword
        val user = properties.buildReportHttpUser
        HttpReportSettings(url, password, user)
    } else {
        null
    }
    val metricsOutputFile = properties.singleBuildMetricsFile
    return ReportingSettings(
        metricsOutputFile = metricsOutputFile,
        buildReportMode = buildReportMode,
        fileReportSettings = fileReportSettings,
        httpReportSettings = httpReportSettings
    )
}


