package com.github.wu162.ra2inihelper.action

import com.github.wu162.ra2inihelper.PluginSettingComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory

class SetRa2RootPathAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        FileChooser.chooseFile(
            FileChooserDescriptorFactory.createSingleFolderDescriptor(), e.project, null,
        ) {
            PluginSettingComponent.instance.state.ra2Root = it.path
        }
    }
}