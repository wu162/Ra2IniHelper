package com.github.wu162.ra2inihelper.lang

import com.github.wu162.ra2inihelper.lang.psi.IniElementType
import com.github.wu162.ra2inihelper.lang.psi.IniProperty
import com.github.wu162.ra2inihelper.lang.psi.IniPropertyReference
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getKey
import com.github.wu162.ra2inihelper.lang.util.IniPsiImplUtil.getValue
import com.github.wu162.ra2inihelper.lang.util.IniUtil
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry
import com.intellij.psi.util.elementType
import com.intellij.util.ProcessingContext

class IniReferenceContributor : PsiReferenceContributor() {

    val objectPropNames = listOf(
        "Primary"
    )

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        // Register a reference provider for any element

        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(IniTypes.PROPERTY),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    if (element is IniProperty) {
                        val value = element.getValue()
                        if (value.isNullOrEmpty()) {
                            return PsiReference.EMPTY_ARRAY
                        }
//                        if (IniUtil.isObjectSection(element)) {
//                            val key = element.getKey()
//                            if (key != null && objectPropNames.contains(key)) {
//                                return arrayOf(IniPropertyReference(element, element.textRange))
//                            }
//                        }
                        val key = element.getKey()
                        if (key != null && objectPropNames.contains(key)) {
                            return arrayOf(IniPropertyReference(element, element.textRange))
                        }

                    }
                    return PsiReference.EMPTY_ARRAY
                }

            })
    }


}