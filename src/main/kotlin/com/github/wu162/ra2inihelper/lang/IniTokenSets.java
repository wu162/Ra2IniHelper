package com.github.wu162.ra2inihelper.lang;

import com.intellij.psi.tree.TokenSet;

public interface IniTokenSets {

//    TokenSet IDENTIFIERS = TokenSet.create(IniTypes.KEY);

    TokenSet COMMENTS = TokenSet.create(IniTypes.COMMENT);

    TokenSet SECTION = TokenSet.create(IniTypes.SECTIONS);

    TokenSet SECTION_HEADER = TokenSet.create(IniTypes.SECTIONHEADER);

    TokenSet PROPERTY = TokenSet.create(IniTypes.PROPERTY);

    TokenSet ITEM_ = TokenSet.create(IniTypes.ITEM_);

}
