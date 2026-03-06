package com.getpagespeed.gixy.lexer

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class NginxSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val CONTEXT_NAME_KEY = createTextAttributesKey("NGINX_CONTEXT_NAME", DefaultLanguageHighlighterColors.KEYWORD)
        val DIRECTIVE_NAME_KEY = createTextAttributesKey("NGINX_DIRECTIVE_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val VARIABLE_KEY = createTextAttributesKey("NGINX_VARIABLE", DefaultLanguageHighlighterColors.INSTANCE_FIELD)
        val STRING_KEY = createTextAttributesKey("NGINX_STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER_KEY = createTextAttributesKey("NGINX_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
        val COMMENT_KEY = createTextAttributesKey("NGINX_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val SEMICOLON_KEY = createTextAttributesKey("NGINX_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val BRACES_KEY = createTextAttributesKey("NGINX_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val LOCATION_OP_KEY = createTextAttributesKey("NGINX_LOCATION_OP", DefaultLanguageHighlighterColors.KEYWORD)
        val REGEX_PREFIX_KEY = createTextAttributesKey("NGINX_REGEX_PREFIX", DefaultLanguageHighlighterColors.METADATA)
    }

    override fun getHighlightingLexer(): Lexer = NginxLexerAdapter()

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            NginxTokenTypes.CONTEXT_NAME -> arrayOf(CONTEXT_NAME_KEY)
            NginxTokenTypes.DIRECTIVE_NAME -> arrayOf(DIRECTIVE_NAME_KEY)
            NginxTokenTypes.VARIABLE -> arrayOf(VARIABLE_KEY)
            NginxTokenTypes.DOUBLE_QUOTED_STRING -> arrayOf(STRING_KEY)
            NginxTokenTypes.SINGLE_QUOTED_STRING -> arrayOf(STRING_KEY)
            NginxTokenTypes.NUMBER -> arrayOf(NUMBER_KEY)
            NginxTokenTypes.COMMENT -> arrayOf(COMMENT_KEY)
            NginxTokenTypes.SEMICOLON -> arrayOf(SEMICOLON_KEY)
            NginxTokenTypes.LBRACE -> arrayOf(BRACES_KEY)
            NginxTokenTypes.RBRACE -> arrayOf(BRACES_KEY)
            NginxTokenTypes.LOCATION_OP -> arrayOf(LOCATION_OP_KEY)
            NginxTokenTypes.REGEX_PREFIX -> arrayOf(REGEX_PREFIX_KEY)
            else -> emptyArray()
        }
    }
}
