package com.github.wu162.ra2inihelper.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon


object IniFileType : LanguageFileType(IniLanguage) {
    override fun getName(): String {
        return "INI File"
    }

    override fun getDescription(): String {
        return "INI configuration file"
    }

    override fun getDefaultExtension(): String {
        return "ini"
    }

    override fun getIcon(): Icon? {
        return IniIcons.FILE
    }

}