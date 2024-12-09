package com.github.wu162.ra2inihelper.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class IniFile(viewProvider: FileViewProvider) :
    PsiFileBase(viewProvider, IniLanguage) {

    override fun getFileType(): FileType {
        return IniFileType
    }

    override fun toString(): String {
        return "Ini File"
    }

}