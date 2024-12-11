package com.github.wu162.ra2inihelper.lang.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode

abstract class IniPropertyNamedElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), IniPropertyNamedElement {
}