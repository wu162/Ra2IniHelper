package com.github.wu162.ra2inihelper.lang.util

import com.github.wu162.ra2inihelper.indexer.ObjectsIndexer
import com.github.wu162.ra2inihelper.lang.IniDocumentationProvider.Companion.objPropDesc
import com.github.wu162.ra2inihelper.lang.IniFile
import com.github.wu162.ra2inihelper.lang.IniFileType
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.psi.IniSectionheader
import com.github.wu162.ra2inihelper.lang.psi.IniSections
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getKey
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getParentSectionName
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getSectionName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore.DirectoryScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex

object IniUtil {

    fun findSection(project: Project, value: String?): IniSections? {
        if (value == null) return null
        val virtualFiles: Collection<VirtualFile> =
            FileTypeIndex.getFiles(IniFileType, GlobalSearchScope.allScope(project))
        // only search current Directory
        for (virtualFile in virtualFiles) {
            val iniFile: IniFile? = PsiManager.getInstance(project).findFile(virtualFile) as? IniFile
            if (iniFile != null) {
                val sections =
                    PsiTreeUtil.getChildrenOfType(iniFile, IniSections::class.java)
                if (sections != null) {
                    for (section in sections) {
                        if (value == section.getSectionName()) {
                            return section
                        }
                    }
                }
            }
        }

        return null
    }

    fun isObjectSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        val objectNames = FileBasedIndex.getInstance()
            .getValues(
                ObjectsIndexer.ObjectsIndexerName,
                ObjectsIndexer.KEY_OBJECT,
                GlobalSearchScope.allScope(element.project)
            )
        //check if objectNames contains sectionName
        objectNames.forEach {
            it.forEach { name ->
                if (name == sectionName) {
                    return true
                }
            }
        }
        return false
    }


}