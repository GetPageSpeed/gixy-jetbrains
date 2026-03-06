package com.getpagespeed.gixy.lexer

import com.getpagespeed.gixy.NginxLanguage
import com.intellij.psi.tree.IElementType

object NginxTokenTypes {
    @JvmField val COMMENT = IElementType("COMMENT", NginxLanguage)
    @JvmField val CONTEXT_NAME = IElementType("CONTEXT_NAME", NginxLanguage)
    @JvmField val DIRECTIVE_NAME = IElementType("DIRECTIVE_NAME", NginxLanguage)
    @JvmField val VALUE = IElementType("VALUE", NginxLanguage)
    @JvmField val DOUBLE_QUOTED_STRING = IElementType("DOUBLE_QUOTED_STRING", NginxLanguage)
    @JvmField val SINGLE_QUOTED_STRING = IElementType("SINGLE_QUOTED_STRING", NginxLanguage)
    @JvmField val VARIABLE = IElementType("VARIABLE", NginxLanguage)
    @JvmField val NUMBER = IElementType("NUMBER", NginxLanguage)
    @JvmField val SEMICOLON = IElementType("SEMICOLON", NginxLanguage)
    @JvmField val LBRACE = IElementType("LBRACE", NginxLanguage)
    @JvmField val RBRACE = IElementType("RBRACE", NginxLanguage)
    @JvmField val REGEX_PREFIX = IElementType("REGEX_PREFIX", NginxLanguage)
    @JvmField val LOCATION_OP = IElementType("LOCATION_OP", NginxLanguage)
}
