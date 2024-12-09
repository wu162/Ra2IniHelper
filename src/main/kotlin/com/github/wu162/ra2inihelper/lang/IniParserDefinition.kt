package com.github.wu162.ra2inihelper.lang

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class IniParserDefinition : ParserDefinition {

    val FILE: IFileElementType = IFileElementType(IniLanguage)

    override fun createLexer(project: Project?): Lexer {
        return IniLexerAdapter()
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.create(IniTypes.COMMENT)
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createParser(project: Project?): PsiParser {
        return IniParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return IniFile(viewProvider)
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return IniTypes.Factory.createElement(node)
    }

}