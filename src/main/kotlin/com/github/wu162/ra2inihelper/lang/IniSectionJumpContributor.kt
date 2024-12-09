package com.github.wu162.ra2inihelper.lang

import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.containers.ContainerUtil
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter

class IniSectionJumpContributor : ChooseByNameContributorEx {
    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {

    }

    override fun processElementsWithName(name: String, processor: Processor<in NavigationItem>, parameters: FindSymbolParameters) {

    }
}