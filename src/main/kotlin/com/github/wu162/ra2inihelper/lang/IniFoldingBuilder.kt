package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.lang.psi.IniElementType
import com.github.wu162.ra2inihelper.lang.psi.IniSections
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class IniFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        if (root is IniFile) {
            for (child in root.children) {
                if (child is IniSections) {
                    descriptors.add(
                        FoldingDescriptor(
                            child,
                            child.textRange
                        )
                    )
                }
            }
        }

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        return (node.psi as IniSections).sectionheader.text
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }
}