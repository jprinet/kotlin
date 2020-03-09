/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.jetbrains.konan

import com.intellij.execution.ExecutionTarget
import com.intellij.execution.ExecutionTargetListener
import com.intellij.execution.ExecutionTargetManager
import com.intellij.execution.RunManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import org.jdom.Element
import org.jetbrains.kotlin.konan.CompilerVersion
import org.jetbrains.kotlin.konan.CompilerVersionImpl
import org.jetbrains.kotlin.konan.MetaVersion
import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.konan.util.DependencyDirectories
import java.io.File
import java.util.*
import kotlin.collections.HashSet

open class TargetListener(private val workspace: WorkspaceBase) : ExecutionTargetListener {
    protected fun configuration() = RunManager.getInstance(workspace.project).selectedConfiguration?.configuration

    override fun activeTargetChanged(target: ExecutionTarget) {
        (configuration() as? BinaryRunConfigurationBase)?.let {
            it.selectedTarget = target as? BinaryExecutionTarget
        }
    }
}

private val DEBUG_POSSIBLE = CompilerVersionImpl(MetaVersion.DEV, 1, 3, 60, -1)

private fun compare(lhs: CompilerVersion, rhs: CompilerVersion): Int {
    if (lhs.major != rhs.major) return lhs.major - rhs.major
    if (lhs.minor != rhs.minor) return lhs.minor - rhs.minor
    if (lhs.maintenance != rhs.maintenance) return lhs.maintenance - rhs.maintenance
    return 0
}

open class WorkspaceBase(val project: Project) : PersistentStateComponent<Element>, ProjectComponent {
    val executables = HashSet<KonanExecutable>()
    private val basePath = File(project.basePath!!)
    var konanHome: String? = null

    val lldbHome: File?
        get() {
            if (konanHome == null || !File(konanHome).exists()) {
                return null
            }

            val propertiesPath = "$konanHome/konan/konan.properties"
            val propertiesFile = File(propertiesPath)
            val hostDependenciesKey = "dependencies.${HostManager.host}"

            if (!propertiesFile.exists()) {
                KonanLog.LOG.error("Kotlin/Native properties file is absent at $propertiesPath")
                return null
            }

            val hostDependencies = Properties().apply { propertiesFile.inputStream().use(::load) }.getProperty(hostDependenciesKey)

            if (hostDependencies == null) {
                KonanLog.LOG.error("No property $hostDependenciesKey at $propertiesPath")
                return null
            }

            val lldbRelative = hostDependencies.split(" ").firstOrNull { it.startsWith("lldb-") }

            if (lldbRelative == null) {
                KonanLog.LOG.error("Property $hostDependenciesKey at $propertiesPath does not specify lldb")
                return null
            }

            return DependencyDirectories.defaultDependenciesRoot.resolve(lldbRelative)
        }

    var konanVersion: CompilerVersion? = null
        set(value) {
            value?.let {
                if (compare(it, DEBUG_POSSIBLE) < 0) {
                    KonanLog.MESSAGES.createNotification(
                        KonanBundle.message("warning.versionPrior1_3_60", it),
                        NotificationType.WARNING
                    ).notify(project)

                }
            }
            field = value
        }

    val isDebugPossible: Boolean
        get() {
            konanVersion?.let {
                return compare(it, DEBUG_POSSIBLE) >= 0
            }

            return false
        }

    override fun getState(): Element {
        val stateElement = Element("state")
        val executablesElement = Element(XmlKonanWorkspace.nodeAllExecutables)
        executables.toSortedSet().forEach {
            val element = Element(XmlKonanWorkspace.nodeExecutable)
            it.writeToXml(element, basePath)
            executablesElement.addContent(element)
        }
        stateElement.addContent(executablesElement)
        konanHome?.let { stateElement.setAttribute(XmlKonanWorkspace.attributeKonanHome, it) }
        return stateElement
    }

    override fun loadState(stateElement: Element) {
        stateElement.getChild(XmlKonanWorkspace.nodeAllExecutables)?.getChildren(XmlKonanWorkspace.nodeExecutable)?.forEach {
            val executable = KonanExecutable.readFromXml(it, basePath) ?: return@forEach
            executables.add(executable)
        }

        konanHome = stateElement.getAttributeValue(XmlKonanWorkspace.attributeKonanHome)?.also {
            konanVersion = getKotlinNativeVersion(it)
        }
    }

    override fun projectOpened() {
        super.projectOpened()

        val runManager = RunManager.getInstance(project)
        val activeTarget = ExecutionTargetManager.getActiveTarget(project)

        runManager.selectedConfiguration?.apply {
            (configuration as? BinaryRunConfigurationBase)?.let {
                it.selectedTarget = activeTarget as? BinaryExecutionTarget ?: it.executable?.executionTargets?.firstOrNull()
            }
        }

        konanHome?.let { konanVersion = getKotlinNativeVersion(it) }
    }
}