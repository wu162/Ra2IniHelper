package com.github.wu162.ra2inihelper.lang.psi

import com.github.wu162.ra2inihelper.lang.util.IniUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getValue

class IniPropertyReference(
    val myelement: IniProperty,
    textRange: TextRange
) : PsiReferenceBase<PsiElement>(myelement, textRange) {
    override fun resolve(): PsiElement? {
        return IniUtil.findSection(myelement.project, myelement.getValue())
    }


}