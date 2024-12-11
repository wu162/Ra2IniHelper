package com.github.wu162.ra2inihelper

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

@State(
    name = "Ra2ModPluginSetting",
    storages = [Storage(value = "Ra2ModPluginSetting.xml")]
)
class PluginSettingComponent : PersistentStateComponent<PluginSetting> {

    private var pluginSetting = PluginSetting()

    override fun getState(): PluginSetting {
        return pluginSetting
    }

    override fun loadState(pluginSetting: PluginSetting) {
        this.pluginSetting = pluginSetting
    }

    companion object {
        val instance = ApplicationManager.getApplication().getService(PluginSettingComponent::class.java)
    }

}

data class PluginSetting(
    var ra2Root: String = ""
)

fun ra2Root(): String {
    return PluginSettingComponent.instance.state.ra2Root
}

fun Project.inRa2Root(): Boolean {
    return PluginSettingComponent.instance.state.ra2Root.isNotEmpty() && basePath?.startsWith(ra2Root()) == true
}

fun VirtualFile.inRa2Root(): Boolean {
    return PluginSettingComponent.instance.state.ra2Root.isNotEmpty() && path.startsWith(ra2Root())
}