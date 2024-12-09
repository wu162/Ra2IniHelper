package com.github.wu162.ra2inihelper.lang.psi

import com.intellij.navigation.ItemPresentation

interface NavigatableElement {

    fun getPresentation(): ItemPresentation?
}