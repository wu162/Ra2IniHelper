package com.github.wu162.ra2inihelper.lang.psi

import com.github.wu162.ra2inihelper.lang.IniLanguage
import com.intellij.psi.tree.IElementType


class IniTokenType(debugName: String) : IElementType(debugName, IniLanguage) {
    override fun toString(): String {
        return "IniTokenType." + super.toString()
    }
}