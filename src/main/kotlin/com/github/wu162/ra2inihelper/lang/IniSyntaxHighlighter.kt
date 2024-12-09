package com.github.wu162.ra2inihelper.lang

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

object IniSyntaxHighlighter : SyntaxHighlighterBase() {

    val SECTION_HEADER: TextAttributesKey = createTextAttributesKey("INI_SECTION", DefaultLanguageHighlighterColors.KEYWORD)
    val KEY: TextAttributesKey = createTextAttributesKey("INI_KEY", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE)
    val VALUE: TextAttributesKey = createTextAttributesKey("INI_VALUE", DefaultLanguageHighlighterColors.STRING)
    val COMMENT: TextAttributesKey = createTextAttributesKey("INI_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val BAD_CHARACTER: TextAttributesKey = createTextAttributesKey("INI_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)


    private val BAD_CHAR_KEYS = arrayOf(BAD_CHARACTER)
    private val SECTION_HEADER_KEYS = arrayOf(SECTION_HEADER)
    private val KEY_KEYS = arrayOf(KEY)
    private val VALUE_KEYS = arrayOf(VALUE)
    private val COMMENT_KEYS = arrayOf(COMMENT)
    private val EMPTY_KEYS = arrayOf<TextAttributesKey>()

    override fun getHighlightingLexer(): Lexer {
        return IniLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        if (tokenType.equals(IniTypes.SECTIONNAME)) {
            return SECTION_HEADER_KEYS;
        }
        if (tokenType.equals(IniTypes.KEY)) {
            return KEY_KEYS;
        }
        if (tokenType.equals(IniTypes.VALUE)) {
            return VALUE_KEYS;
        }
        if (tokenType.equals(IniTypes.COMMENT)) {
            return COMMENT_KEYS;
        }
        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }
        return EMPTY_KEYS;
    }


}