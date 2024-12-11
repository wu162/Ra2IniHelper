package com.github.wu162.ra2inihelper.lang.util

import com.github.wu162.ra2inihelper.indexer.AssetsIndexer
import com.github.wu162.ra2inihelper.lang.IniFile
import com.github.wu162.ra2inihelper.lang.IniFileType
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.psi.IniSections
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getParentSectionName
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getSectionName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
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

    fun findSections(project: Project, values: List<String>): List<IniSections> {
        val virtualFiles: Collection<VirtualFile> =
            FileTypeIndex.getFiles(IniFileType, GlobalSearchScope.allScope(project))

        val result = mutableListOf<IniSections>()
        for (virtualFile in virtualFiles) {
            val iniFile: IniFile? = PsiManager.getInstance(project).findFile(virtualFile) as? IniFile
            if (iniFile != null) {
                val sections =
                    PsiTreeUtil.getChildrenOfType(iniFile, IniSections::class.java)
                if (sections != null) {
                    for (section in sections) {
                        val sectionName = section.getSectionName()
                        if (values.contains(sectionName)) {
                            result.add(section)
                        }
                    }
                }
            }
        }

        return result
    }

    fun isObjectSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllAircraftTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        getAllBuildingTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        getAllInfantryTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        getAllVehicleTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun isAircraftTypesSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllAircraftTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun isBuildingTypesSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllBuildingTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun isInfantryTypesSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllInfantryTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun isVehicleTypesSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllVehicleTypes(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun getAllWeapons(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_WEAPON,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun getAllProjectiles(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_PROJECTILE,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun getAllWarhead(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_WARHEADS,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun getAllAircraftTypes(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_AIRCRAFTTYPES,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun getAllBuildingTypes(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_BUILDINGTYPES,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun getAllVehicleTypes(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_VEHICLETYPES,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun getAllInfantryTypes(project: Project): List<String> {
        return FileBasedIndex.getInstance()
            .getValues(
                AssetsIndexer.ObjectsIndexerName,
                AssetsIndexer.KEY_INFANTRYTYPES,
                GlobalSearchScope.allScope(project)
            ).flatten()
    }

    fun isWeaponSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllWeapons(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun isProjectileSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllProjectiles(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }

    fun isWarheadSection(element: IniProperty): Boolean {
        val sectionName = element.getParentSectionName()
        getAllWarhead(element.project).forEach {
            if (it == sectionName) {
                return true
            }
        }
        return false
    }


}