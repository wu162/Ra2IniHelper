package com.github.wu162.ra2inihelper.lang.reference

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.SmartList
import com.intellij.util.containers.ConcurrentFactoryMap
import com.intellij.util.containers.MultiMap

interface IniPsiReferenceProvider {
    fun getReferencesByElement(element: PsiElement): Array<PsiReference>
}

interface IniReferenceProviderContributor {
    fun registerReferenceProviders(registrar: IniPsiReferenceRegistrar)

    companion object {
        fun getInstance(project: Project): IniReferenceProviderContributor =
            project.getService(IniReferenceProviderContributor::class.java)
    }
}

class IniPsiReferenceRegistrar {
    val providers: MultiMap<Class<out PsiElement>, IniPsiReferenceProvider> = MultiMap(LinkedHashMap())

    inline fun <reified E : PsiElement> registerProvider(crossinline factory: (E) -> PsiReference?) {
        registerMultiProvider<E> { element ->
            factory(element)?.let { reference -> arrayOf(reference) } ?: PsiReference.EMPTY_ARRAY
        }
    }

    inline fun <reified E : PsiElement> registerMultiProvider(crossinline factory: (E) -> Array<PsiReference>) {
        val provider: IniPsiReferenceProvider = object : IniPsiReferenceProvider {
            override fun getReferencesByElement(element: PsiElement): Array<PsiReference> {
                return factory(element as E)
            }
        }

        registerMultiProvider(E::class.java, provider)
    }

    fun registerMultiProvider(klass: Class<out PsiElement>, provider: IniPsiReferenceProvider) {
        providers.putValue(klass, provider)
    }
}

open class IniReferenceProviderService() {

    open fun getReferences(psiElement: PsiElement): Array<PsiReference> = PsiReference.EMPTY_ARRAY

    companion object {

        private val NO_REFERENCES_SERVICE = IniReferenceProviderService()

        @JvmStatic
        fun getInstance(project: Project): IniReferenceProviderService {
            return project.getService(IniReferenceProviderService::class.java) ?: IniReferenceProviderService()
        }

        @JvmStatic
        fun getReferencesFromProviders(psiElement: PsiElement): Array<PsiReference> {
            return getInstance(psiElement.project).getReferences(psiElement)
        }
    }
}

class IniReferenceProviderServiceImpl(val project: Project): IniReferenceProviderService() {

    private val originalProvidersBinding: MultiMap<Class<out PsiElement>, IniPsiReferenceProvider>
    private val providersBindingCache: Map<Class<out PsiElement>, List<IniPsiReferenceProvider>>

    init {
        val registrar = IniPsiReferenceRegistrar()
        IniReferenceProviderContributor.getInstance(project).registerReferenceProviders(registrar)
        originalProvidersBinding = registrar.providers

        providersBindingCache = ConcurrentFactoryMap.createMap<Class<out PsiElement>, List<IniPsiReferenceProvider>> { klass ->
            val result = SmartList<IniPsiReferenceProvider>()
            for (bindingClass in originalProvidersBinding.keySet()) {
                if (bindingClass.isAssignableFrom(klass)) {
                    result.addAll(originalProvidersBinding.get(bindingClass))
                }
            }
            result
        }
    }

    private fun doGetIniReferencesFromProviders(context: PsiElement): Array<PsiReference> {
        val providers: List<IniPsiReferenceProvider>? = providersBindingCache[context.javaClass]
        if (providers.isNullOrEmpty()) return PsiReference.EMPTY_ARRAY

        val result = SmartList<PsiReference>()
        for (provider in providers) {
            result.addAll(provider.getReferencesByElement(context))
        }

        if (result.isEmpty()) {
            return PsiReference.EMPTY_ARRAY
        }

        return result.toTypedArray()
    }

    override fun getReferences(psiElement: PsiElement): Array<PsiReference> {
//        if (psiElement is ContributedReferenceHost) {
//            return ReferenceProvidersRegistry.getReferencesFromProviders(psiElement, PsiReferenceService.Hints.NO_HINTS)
//        }

        return CachedValuesManager.getCachedValue(psiElement) {
            CachedValueProvider.Result.create(
                doGetIniReferencesFromProviders(psiElement),
                PsiModificationTracker.MODIFICATION_COUNT
            )
        }
    }



//    val registrar = IniPsiReferenceRegistrar()
//
//    override fun getReferences(psiElement: PsiElement): Array<PsiReference> {
//        return super.getReferences(psiElement)
//    }
}