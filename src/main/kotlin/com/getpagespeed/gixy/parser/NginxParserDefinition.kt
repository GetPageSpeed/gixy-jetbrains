package com.getpagespeed.gixy.parser

import com.getpagespeed.gixy.NginxConfFileType
import com.getpagespeed.gixy.NginxLanguage
import com.getpagespeed.gixy.lexer.NginxLexerAdapter
import com.getpagespeed.gixy.lexer.NginxTokenTypes
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class NginxParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(NginxLanguage)
        val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
        val COMMENTS = TokenSet.create(NginxTokenTypes.COMMENT)
        val STRINGS = TokenSet.create(NginxTokenTypes.DOUBLE_QUOTED_STRING, NginxTokenTypes.SINGLE_QUOTED_STRING)
    }

    override fun createLexer(project: Project): Lexer = NginxLexerAdapter()

    override fun createParser(project: Project): PsiParser {
        return PsiParser { root, builder ->
            val marker = builder.mark()
            while (!builder.eof()) {
                builder.advanceLexer()
            }
            marker.done(root)
            builder.treeBuilt
        }
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getWhitespaceTokens(): TokenSet = WHITE_SPACES

    override fun getCommentTokens(): TokenSet = COMMENTS

    override fun getStringLiteralElements(): TokenSet = STRINGS

    override fun createElement(node: ASTNode): PsiElement = ASTWrapperPsiElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return object : PsiFileBase(viewProvider, NginxLanguage) {
            override fun getFileType(): FileType = NginxConfFileType
        }
    }
}
