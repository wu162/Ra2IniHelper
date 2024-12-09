package com.github.wu162.ra2inihelper.lang.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class IniPropertyReference(
    element: PsiElement,
    textRange: TextRange
) : PsiReferenceBase<PsiElement>(element, textRange) {
    override fun resolve(): PsiElement? {
        return null
//        return IniUtil.findSection(element.project, element.node.findChildByType(IniTypes.VALUE)?.text)
    }


}