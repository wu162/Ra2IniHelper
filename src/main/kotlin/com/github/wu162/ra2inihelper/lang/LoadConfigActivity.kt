package com.github.wu162.ra2inihelper.lang

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LoadConfigActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        IniDocumentationProvider.loadDoc()
    }
}