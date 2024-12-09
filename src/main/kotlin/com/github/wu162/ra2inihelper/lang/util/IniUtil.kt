package com.github.wu162.ra2inihelper.lang.util

import com.github.wu162.ra2inihelper.lang.IniFile
import com.github.wu162.ra2inihelper.lang.IniFileType
import com.github.wu162.ra2inihelper.lang.psi.IniSectionheader
import com.github.wu162.ra2inihelper.lang.psi.IniSections
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getSectionName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore.DirectoryScope
import com.intellij.psi.util.PsiTreeUtil

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

    val registerSectionNames = listOf(
        "InfantryTypes",
        "VehicleTypes",
        "AircraftTypes",
        "BuildingTypes",
    )

//    //找到所有注册的物体
//    fun findAllObjects(project: Project): List<String> {
//        val result = mutableListOf<String>()
//        val virtualFiles: Collection<VirtualFile> =
//            FileTypeIndex.getFiles(IniFileType, GlobalSearchScope.allScope(project))
//        for (virtualFile in virtualFiles) {
//            val iniFile: IniFile? = PsiManager.getInstance(project).findFile(virtualFile) as? IniFile
//            if (iniFile != null) {
//                val sections =
//                    PsiTreeUtil.getChildrenOfType(iniFile, IniSections::class.java)
//                if (sections != null) {
//                    for (section in sections) {
//                        val sectionName = section.getSectionName()
//                        if (sectionName ) {
//                            return section
//                        }
//                    }
//                }
//            }
//        }
//
//        return result
//    }

}