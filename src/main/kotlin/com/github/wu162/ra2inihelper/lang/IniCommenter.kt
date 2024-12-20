package com.github.wu162.ra2inihelper.lang

import com.intellij.lang.Commenter

class IniCommenter : Commenter {
    override fun getLineCommentPrefix(): String {
        return ";"
    }

    override fun getBlockCommentPrefix(): String? {
        return "";
    }

    override fun getBlockCommentSuffix(): String? {
        return null;
    }

    override fun getCommentedBlockCommentPrefix(): String? {
        return null;
    }

    override fun getCommentedBlockCommentSuffix(): String? {
        return null;
    }
}