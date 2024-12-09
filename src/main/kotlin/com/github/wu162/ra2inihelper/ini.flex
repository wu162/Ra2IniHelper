// Copyright 2000-2022 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.github.wu162.ra2inihelper.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.github.wu162.ra2inihelper.lang.IniTypes;
import com.intellij.psi.TokenType;

%%

%class IniLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

NEWLINE=\n
CRLF=\R
WHITE_SPACE=[\ \n\t\f]
LB="["
RB="]"
//section名字
SECTIONNAME_CHARACTER=[^\r\n\[\]\t\f\/\;]
FIRST_VALUE_CHARACTER=[^ \n\f\\\/]
//VAL=[^\;\[\]\n\f\\\/\/]
VAL=[^\/\;\[\]\n\f]+(\/[^\/\;\[\]\n\f]+)*
//同时有;开头的和//开头的注释，都需要支持下
END_OF_LINE_COMMENT=(";"|"//")[^\r\n]*
SEPARATOR=[=]
KEY_CHARACTER=[^\[\]\=\ \n\t\f\\\/\;]
COMMENT_START1=(";")
COMMENT_START2=("//")

%state WAITING_VALUE
%state WAITING_SECTIONNAME
%state WAITING_RB

%%

<YYINITIAL> {END_OF_LINE_COMMENT}                           { yybegin(YYINITIAL); return IniTypes.COMMENT; }

<YYINITIAL> {LB}                                     { yybegin(WAITING_SECTIONNAME); return IniTypes.LB; }

<WAITING_RB> {RB}                                     { yybegin(YYINITIAL); return IniTypes.RB; }

<WAITING_SECTIONNAME> {SECTIONNAME_CHARACTER}+                                { yybegin(WAITING_RB); return IniTypes.SECTIONNAME; }

<YYINITIAL> {KEY_CHARACTER}+                                { yybegin(YYINITIAL); return IniTypes.KEY; }

<YYINITIAL> {SEPARATOR}                                     { yybegin(WAITING_VALUE); return IniTypes.SEPARATOR; }

<WAITING_VALUE> {CRLF}({CRLF}|{WHITE_SPACE})+               { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {WHITE_SPACE}+                              { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {VAL}   { yybegin(YYINITIAL); return IniTypes.VALUE; }

<WAITING_VALUE> {COMMENT_START1}   { yybegin(YYINITIAL);  yypushback(1); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {COMMENT_START2}   { yybegin(YYINITIAL);  yypushback(2); return TokenType.WHITE_SPACE; }

({CRLF}|{WHITE_SPACE})+                                     { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }