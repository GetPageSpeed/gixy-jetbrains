package com.getpagespeed.gixy.annotator

import com.getpagespeed.gixy.model.GixyIssue
import com.getpagespeed.gixy.settings.GixySettings
import com.getpagespeed.gixy.util.GixyRunner
import com.intellij.ide.BrowserUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

data class GixyAnnotationInfo(
    val filePath: String,
    val fileContent: String,
)

class GixyExternalAnnotator : ExternalAnnotator<GixyAnnotationInfo, List<GixyIssue>>() {
    private val LOG = Logger.getInstance(GixyExternalAnnotator::class.java)

    companion object {
        private val NGINX_FILE_PATTERNS = listOf(
            Regex(""".*nginx.*\.conf$""", RegexOption.IGNORE_CASE),
            Regex(""".*\.conf$"""),
            Regex(""".*sites-(available|enabled)/.*"""),
            Regex(""".*conf\.d/.*"""),
            Regex(""".*nginx/.*"""),
        )

        private val SEVERITY_ORDER = mapOf(
            "UNSPECIFIED" to 0,
            "LOW" to 1,
            "MEDIUM" to 2,
            "HIGH" to 3,
        )

    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): GixyAnnotationInfo? {
        val settings = GixySettings.getInstance()
        if (!settings.enabled) return null

        val virtualFile = file.virtualFile ?: return null
        val path = virtualFile.path

        if (!isNginxConfig(path)) return null

        return GixyAnnotationInfo(
            filePath = path,
            fileContent = file.text,
        )
    }

    override fun doAnnotate(info: GixyAnnotationInfo): List<GixyIssue> {
        val issues = GixyRunner.run(info.filePath)

        val settings = GixySettings.getInstance()
        val minSeverity = SEVERITY_ORDER[settings.minimumSeverity] ?: 0

        return issues.filter { issue ->
            val issueSeverity = SEVERITY_ORDER[issue.severity] ?: 0
            issueSeverity >= minSeverity
        }
    }

    override fun apply(file: PsiFile, issues: List<GixyIssue>, holder: AnnotationHolder) {
        val document = file.viewProvider.document ?: return

        for (issue in issues) {
            val severity = mapSeverity(issue.severity)
            val lineNumber = (issue.line ?: 1) - 1
            val safeLineNumber = lineNumber.coerceIn(0, document.lineCount - 1)

            val lineStartOffset = document.getLineStartOffset(safeLineNumber)
            val lineEndOffset = document.getLineEndOffset(safeLineNumber)
            val range = TextRange(lineStartOffset, lineEndOffset)

            val tooltip = buildTooltip(issue)

            val builder = holder.newAnnotation(severity, issue.summary)
                .range(range)
                .tooltip(tooltip)

            for (fix in issue.fixes) {
                builder.withFix(GixyQuickFix(fix))
            }

            if (issue.reference != null) {
                builder.withFix(GixyOpenDocsFix(issue.reference, issue.plugin))
            }

            builder.create()
        }
    }

    internal fun isNginxConfig(path: String): Boolean {
        return NGINX_FILE_PATTERNS.any { it.matches(path) }
    }

    internal fun mapSeverity(severity: String): HighlightSeverity {
        return when (severity) {
            "HIGH" -> HighlightSeverity.ERROR
            "MEDIUM" -> HighlightSeverity.WARNING
            "LOW" -> HighlightSeverity.WEAK_WARNING
            else -> HighlightSeverity.WEAK_WARNING
        }
    }

    private fun buildTooltip(issue: GixyIssue): String {
        val sb = StringBuilder()
        sb.append("<html><body>")
        sb.append("<b>[${issue.plugin}]</b> ${issue.summary}<br/>")
        if (issue.reason != null) {
            sb.append("<p>${issue.reason}</p>")
        }
        sb.append("<p>${issue.description}</p>")
        if (issue.reference != null) {
            sb.append("<p><a href='${issue.reference}'>Documentation</a></p>")
        }
        sb.append("</body></html>")
        return sb.toString()
    }

}

class GixyOpenDocsFix(
    private val url: String,
    private val pluginName: String,
) : com.intellij.codeInspection.IntentionAndQuickFixAction() {

    override fun getName(): String = "View documentation for $pluginName"

    override fun getFamilyName(): String = "Gixy"

    override fun applyFix(
        project: com.intellij.openapi.project.Project,
        file: PsiFile,
        editor: Editor?,
    ) {
        BrowserUtil.browse(url)
    }
}
