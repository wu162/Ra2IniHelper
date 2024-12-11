package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getKey
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getParentSectionName
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getValue
import com.github.wu162.ra2inihelper.lang.util.IniUtil
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.openhtmltopdf.css.constants.SVGProperty.properties


class IniLineMarkerProvider: RelatedItemLineMarkerProvider() {

    companion object {
        val objectPropNamesJump = mapOf(
            "Primary" to "跳转到武器定义",
            "Secondary" to "跳转到武器定义",
            "DeathWeapon" to "跳转到武器定义",
            "ElitePrimary" to "跳转到武器定义",
            "EliteSecondary" to "跳转到武器定义",
            "OccupyWeapon" to "跳转到武器定义",
            "EliteOccupyWeapon" to "跳转到武器定义"
        )
    }

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element !is IniProperty) {
            return
        }

        if (processIncludeFile(element, result)) return

        if (IniUtil.isWeaponSection(element)) {
            val keyName = element.getKey()
            val valueName = element.getValue()
            if (valueName.isNullOrEmpty()) {
                return
            }

            if (keyName == "Projectile") {
                val section = IniUtil.findSection(element.project, valueName) ?: return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(AllIcons.Nodes.Property)
                        .setTargets(listOf(section.sectionheader))
                        .setTooltipText("跳转到抛射体定义")
                result.add(builder.createLineMarkerInfo(element))
            } else if (keyName == "Warhead") {
                val section = IniUtil.findSection(element.project, valueName) ?: return
                val builder: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(AllIcons.Nodes.Property)
                        .setTargets(listOf(section.sectionheader))
                        .setTooltipText("跳转到弹头定义")
                result.add(builder.createLineMarkerInfo(element))
            }

        } else if (IniUtil.isObjectSection(element)) {
            val keyName = element.getKey()
            val valueName = element.getValue()
            if (valueName.isNullOrEmpty()) {
                return
            }

            matchObjectPlainProps(keyName, element, valueName, result)


            if (keyName == "Prerequisite") {
                val sections = IniUtil.findSections(element.project, valueName.split(",")) ?: return
                val builder2: NavigationGutterIconBuilder<PsiElement> =
                    NavigationGutterIconBuilder.create(AllIcons.Nodes.Property)
                        .setTargets(sections.map { it.sectionheader })
                        .setTooltipText("跳转到单位定义")
                result.add(builder2.createLineMarkerInfo(element))
            }
        }


        //单位名字注册跳转
    }

    private fun processIncludeFile(
        element: IniProperty,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ): Boolean {
        val sectionName = element.getParentSectionName()
        if (sectionName == "#include") {
            //处理include文件跳转
            val filePath = element.getValue()
            // get absolutePath by project basePath and filePath
            val absolutePath = element.project.basePath + "/" + filePath
            val virtualFiles: Collection<VirtualFile> =
                FileTypeIndex.getFiles(IniFileType, GlobalSearchScope.allScope(element.project))
            for (virtualFile in virtualFiles) {
                if (virtualFile.path == absolutePath) {
                    //get psi element in virtualFile

                    val psi = virtualFile.findPsiFile(element.project)?.node?.psi
                    if (psi != null) {
                        val builder: NavigationGutterIconBuilder<PsiElement> =
                            NavigationGutterIconBuilder.create(AllIcons.Ide.ConfigFile)
                                .setTargets(psi)
                                .setTooltipText("跳转到文件")
                        result.add(builder.createLineMarkerInfo(element))
                    }
                    return true
                }
            }
            return true
        }
        return false
    }

    private fun matchObjectPlainProps(
        keyName: String?,
        element: IniProperty,
        valueName: String?,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (!objectPropNamesJump.containsKey(keyName)) {
            return
        }
        //TODO 似乎可以在多个文件重复定义同一个物体
        val section = IniUtil.findSection(element.project, valueName) ?: return
        val builder: NavigationGutterIconBuilder<PsiElement> =
            NavigationGutterIconBuilder.create(AllIcons.Nodes.Property)
                .setTargets(listOf(section.sectionheader))
                .setTooltipText(objectPropNamesJump[keyName]!!)
        result.add(builder.createLineMarkerInfo(element))
        return
    }


}