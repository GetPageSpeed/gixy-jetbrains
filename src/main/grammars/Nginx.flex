package com.getpagespeed.gixy.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.getpagespeed.gixy.lexer.NginxTokenTypes.*;
import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%class _NginxLexer
%implements FlexLexer
%public
%unicode
%function advance
%type IElementType

%state DIRECTIVE_VALUE

WHITE_SPACE_CHAR=[ \t]
NEWLINE=\r?\n|\r
COMMENT=#[^\r\n]*
CONTEXT_NAME=(http|server|location|upstream|events|stream|mail|map|geo|types|if|limit_except|split_clients)
IDENTIFIER=[a-zA-Z_][a-zA-Z0-9_]*
VARIABLE=(\$[a-zA-Z_][a-zA-Z0-9_]*|\$\{[a-zA-Z_][a-zA-Z0-9_]*\})
NUMBER=[0-9]+[kKmMgGsShHdDwW]?

%%

<YYINITIAL> {
  {WHITE_SPACE_CHAR}+    { return WHITE_SPACE; }
  {NEWLINE}              { return WHITE_SPACE; }
  {COMMENT}              { return COMMENT; }
  "{"                    { return LBRACE; }
  "}"                    { return RBRACE; }
  ";"                    { return SEMICOLON; }
  {CONTEXT_NAME}         { yybegin(DIRECTIVE_VALUE); return CONTEXT_NAME; }
  {IDENTIFIER}           { yybegin(DIRECTIVE_VALUE); return DIRECTIVE_NAME; }
}

<DIRECTIVE_VALUE> {
  {WHITE_SPACE_CHAR}+    { return WHITE_SPACE; }
  {NEWLINE}              { return WHITE_SPACE; }
  {COMMENT}              { return COMMENT; }
  ";"                    { yybegin(YYINITIAL); return SEMICOLON; }
  "{"                    { yybegin(YYINITIAL); return LBRACE; }
  "}"                    { yybegin(YYINITIAL); return RBRACE; }
  "^~"                   { return LOCATION_OP; }
  "="                    { return LOCATION_OP; }
  "~*"                   { return REGEX_PREFIX; }
  "~"                    { return REGEX_PREFIX; }
  {VARIABLE}             { return VARIABLE; }
  \"([^\"\\\r\n]|\\.)*\" { return DOUBLE_QUOTED_STRING; }
  \'([^\'\\\r\n]|\\.)*\' { return SINGLE_QUOTED_STRING; }
  {NUMBER}               { return NUMBER; }
  [^ \t\r\n;{}\#\"\']+  { return VALUE; }
}

[^]                      { return BAD_CHARACTER; }
