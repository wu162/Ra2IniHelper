package com.github.wu162.ra2inihelper.lang.psi

import com.github.wu162.ra2inihelper.lang.IniTypes
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class IniReferenceContributor : PsiReferenceContributor() {

    val names = listOf(
        "Primary"
    )

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        thisLogger().warn("1111111111111111111111111111111111111111111111111111111")
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
                    println("IniReferenceContributor")
                    if (element is IniSections) {
//                        val key = element.node.findChildByType(IniTypes.KEY)?.text
//                        if (key != null && names.contains(key)) {
//                            val value = element.node.findChildByType(IniTypes.VALUE)
//                            if (value != null && value.psi is IniProperty) {
//                                return arrayOf(IniPropertyReference(value.psi as IniProperty, value.textRange))
//                            }
//
//                        }

                        return arrayOf(IniPropertyReference(element, element.textRange))

                    }
                    return PsiReference.EMPTY_ARRAY
                }

            })
    }
}