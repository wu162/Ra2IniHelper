package com.github.wu162.ra2inihelper.lang.format

import com.github.wu162.ra2inihelper.lang.IniTypes
import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock

class IniBlock (
    node: ASTNode, wrap: Wrap?, alignment: Alignment?,
    private val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): List<Block> {
        val blocks = mutableListOf<Block>()
        var child: ASTNode = myNode.firstChildNode
        while (child != null) {
            if (child.elementType == IniTypes.PROPERTY) {
                val block: Block = IniBlock(
                    child, Wrap.createWrap(WrapType.NONE, false),
                    Alignment.createAlignment(),
                    spacingBuilder
                )
                blocks.add(block)
            }
            child = child.treeNext
        }
        return blocks
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        return spacingBuilder.getSpacing(this, child1, child2)
    }

    override fun isLeaf(): Boolean {
        return myNode.firstChildNode == null
    }

    override fun getIndent(): Indent? {
        return Indent.getNoneIndent()
    }
}