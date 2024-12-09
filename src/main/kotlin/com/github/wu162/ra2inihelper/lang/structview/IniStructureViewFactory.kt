package com.github.wu162.ra2inihelper.lang.structview

import com.github.wu162.ra2inihelper.lang.IniFile
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.psi.IniSections
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.isNamed
import com.intellij.ide.structureView.*
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiFile

class IniStructureViewFactory : PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        if (psiFile !is IniFile) {
            return null
        }
        return object : TreeBasedStructureViewBuilder() {
            override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                return Model(psiFile, editor)
            }

            override fun isRootNodeShown(): Boolean {
                return false
            }
        }
    }
}

//class IniPropertyElement(
//    val element: NavigatablePsiElement
//) : StructureViewTreeElement {
//
//    override fun getPresentation(): ItemPresentation {
//        return element.presentation!!
//    }
//
//    override fun getValue(): Any {
//        return element
//    }
//
//    override fun navigate(requestFocus: Boolean) {
//        element.navigate(requestFocus)
//    }
//
//    override fun canNavigate(): Boolean {
//        return element != null
//    }
//
//    override fun getChildren(): Array<TreeElement> {
//        val result = mutableListOf<TreeElement>()
//        return result.toTypedArray()
//    }
//
//}

class SectionElement(
    val element: NavigatablePsiElement
) : StructureViewTreeElement {

    override fun getPresentation(): ItemPresentation {
        return element.presentation!!
    }

    override fun getValue(): Any {
        return element
    }

    override fun navigate(requestFocus: Boolean) {
        element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element != null
    }

    override fun getChildren(): Array<TreeElement> {
        val result = mutableListOf<TreeElement>()
        return result.toTypedArray()
    }

}

class FileElement(
    val element: NavigatablePsiElement
) : StructureViewTreeElement {
    override fun getPresentation(): ItemPresentation {
        return element.presentation!!
    }

    override fun getValue(): Any {
        return element
    }

    override fun navigate(requestFocus: Boolean) {
        element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element != null
    }

    override fun getChildren(): Array<TreeElement> {
        val result = mutableListOf<TreeElement>()
        for (child in element.children) {
            if (child is IniSections) {
                if (child.isNamed()) {
                    result.add(SectionElement(child as NavigatablePsiElement))
                }
            }
        }
        return result.toTypedArray()
    }

}

class Model(
    iniFile: IniFile,
    editor: Editor?
) : StructureViewModelBase(iniFile, editor, FileElement(iniFile)), StructureViewModel.ElementInfoProvider {

    init {
        withSuitableClasses(IniSections::class.java, IniProperty::class.java, IniFile::class.java)
    }
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?): Boolean {
        return false
    }

    override fun isAlwaysLeaf(element: StructureViewTreeElement?): Boolean {
        return element?.value is IniProperty
    }

}