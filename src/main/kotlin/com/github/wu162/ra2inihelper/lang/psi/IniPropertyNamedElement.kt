package com.github.wu162.ra2inihelper.lang.psi

import com.intellij.psi.ContributedReferenceHost
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.tree.IElementType

interface IniPropertyNamedElement : PsiNameIdentifierOwner, NavigatableElement {
    fun getTokenType(): IElementType
}