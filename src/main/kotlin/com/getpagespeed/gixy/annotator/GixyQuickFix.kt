package com.getpagespeed.gixy.annotator

import com.getpagespeed.gixy.model.GixyFix
import com.intellij.codeInspection.IntentionAndQuickFixAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class GixyQuickFix(private val fix: GixyFix) : IntentionAndQuickFixAction() {

    override fun getName(): String = "Gixy: ${fix.title}"

    override fun getFamilyName(): String = "Gixy"

    override fun applyFix(project: Project, file: PsiFile, editor: Editor?) {
        val document = file.viewProvider.document ?: return
        val text = document.text
        val index = text.indexOf(fix.search)
        if (index < 0) return

        WriteCommandAction.runWriteCommandAction(project, "Gixy: ${fix.title}", "Gixy", {
            document.replaceString(index, index + fix.search.length, fix.replace)
        })
    }
}
