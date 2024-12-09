package com.github.wu162.ra2inihelper.lang.util

import com.github.wu162.ra2inihelper.lang.IniTypes
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.psi.IniSectionNamedElement
import com.github.wu162.ra2inihelper.lang.psi.IniSections
import com.intellij.icons.AllIcons
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.util.findParentOfType
import com.intellij.ui.IconManager
import com.intellij.ui.PlatformIcons
import javax.swing.Icon

object IniPsiImplUtil {

    fun IniProperty.getKey(): String? {
        val keyNode = node.findChildByType(IniTypes.KEY)
        return keyNode?.text
    }

    fun IniProperty.getParentSectionName(): String? {
        val section = findParentOfType<IniSections>()
        return section?.getSectionName()
    }

    fun IniProperty.getValue(): String? {
        val keyNode = node.findChildByType(IniTypes.VALUE)
        return keyNode?.text
    }

    fun IniSections.getSectionName(): String? {
        return sectionheader.node.findChildByType(IniTypes.SECTIONNAME)?.text
    }

    @JvmStatic
    fun getName(section: IniSections): String? {
       return section.sectionheader.text
    }

    @JvmStatic
    fun setName(section: IniSections, newName: String): PsiElement {
        return section
    }

    @JvmStatic
    fun getNameIdentifier(section: IniSections): PsiElement? {
        return section
    }

    @JvmStatic
    fun getName(property: IniProperty): String? {
        return property.getKey()
    }

    @JvmStatic
    fun setName(property: IniProperty, newName: String): PsiElement {
        return property
    }

    @JvmStatic
    fun getNameIdentifier(property: IniProperty): PsiElement? {
        return property
    }

//    fun IniSections.setName(newName: String) {
//        val sectionHeader = IniElementFactory.createProperty()
//    }

    fun IniSections.isNamed() = getName(this) != null

    fun IniProperty.isNamed() = getName(this) != null

    @JvmStatic
    fun getPresentation(property: IniProperty): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return if (property.isValid) property.node.findChildByType(IniTypes.KEY)?.text else null
            }

            override fun getIcon(unused: Boolean): Icon {
                return AllIcons.Nodes.Property
            }

        }
    }

    @JvmStatic
    fun getPresentation(section: IniSections): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return if (section.isValid) section.getName() else null
            }

            override fun getIcon(unused: Boolean): Icon {
                return AllIcons.Nodes.Folder
            }

        }
    }



}