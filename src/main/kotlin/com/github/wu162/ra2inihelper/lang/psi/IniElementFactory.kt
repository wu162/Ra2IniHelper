package com.github.wu162.ra2inihelper.lang.psi

import com.github.wu162.ra2inihelper.lang.IniFile
import com.github.wu162.ra2inihelper.lang.IniFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFileFactory

object IniElementFactory {

    fun createProperty(project: Project, name: String): IniProperty? {
        val file = createFile(project, name)
        var res : IniProperty? = null
        file.accept(object : IniVisitor() {
            override fun visitProperty(iniProperty: IniProperty) {
                res = iniProperty
                super.visitProperty(iniProperty)
            }

            override fun visitElement(element: PsiElement) {
                element.acceptChildren(this)
            }
        })
        return res
    }

    fun createFile(project: Project, text: String): IniFile {
        return PsiFileFactory.getInstance(project).createFileFromText("DUMMY__", IniFileType,  text) as IniFile
    }

}