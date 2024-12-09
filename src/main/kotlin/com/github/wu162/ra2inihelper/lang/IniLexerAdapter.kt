package com.github.wu162.ra2inihelper.lang

import com.intellij.lexer.FlexAdapter

class IniLexerAdapter : FlexAdapter(IniLexer(null))