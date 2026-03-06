package com.getpagespeed.gixy.settings

import com.getpagespeed.gixy.util.GixyRunner
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JComboBox

class GixyConfigurable : Configurable {
    private var enabledCheckBox = JBCheckBox("Enable Gixy analysis")
    private var pathField = TextFieldWithBrowseButton()
    private var severityComboBox = JComboBox(arrayOf("UNSPECIFIED", "LOW", "MEDIUM", "HIGH"))
    private var onSaveOnlyCheckBox = JBCheckBox("Analyze on save only (improves performance)")
    private var statusLabel = JBLabel("")

    override fun getDisplayName(): String = "Gixy"

    override fun createComponent(): JComponent {
        pathField.addBrowseFolderListener(
            "Select Gixy Executable",
            "Path to the gixy executable. Leave empty for auto-detection.",
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
        )

        updateStatus()

        return panel {
            row {
                cell(enabledCheckBox)
            }
            row("Gixy executable:") {
                cell(pathField).resizableColumn()
                    .comment("Leave empty to use bundled binary or auto-detect from PATH")
            }
            row("Minimum severity:") {
                cell(severityComboBox)
            }
            row {
                cell(onSaveOnlyCheckBox)
            }
            separator()
            row("Status:") {
                cell(statusLabel)
            }
        }
    }

    private fun updateStatus() {
        val resolved = GixyRunner.resolveExecutableWithSource()
        if (resolved == null) {
            statusLabel.text = "Not found — install gixy or configure path above"
            return
        }

        val sourceLabel = when (resolved.source) {
            GixyRunner.ExecutableSource.SETTINGS -> "configured"
            GixyRunner.ExecutableSource.BUNDLED -> "bundled"
            GixyRunner.ExecutableSource.PATH -> "system PATH"
            GixyRunner.ExecutableSource.NONE -> "none"
        }

        val version = GixyRunner.getVersion(resolved.path)
        val versionText = if (version != null) " — $version" else ""
        statusLabel.text = "<html><b>$sourceLabel</b>$versionText<br><small>${resolved.path}</small></html>"
    }

    override fun isModified(): Boolean {
        val settings = GixySettings.getInstance()
        return enabledCheckBox.isSelected != settings.enabled
                || pathField.text != settings.gixyPath
                || severityComboBox.selectedItem as String != settings.minimumSeverity
                || onSaveOnlyCheckBox.isSelected != settings.analyzeOnSaveOnly
    }

    override fun apply() {
        val settings = GixySettings.getInstance()
        settings.enabled = enabledCheckBox.isSelected
        settings.gixyPath = pathField.text
        settings.minimumSeverity = severityComboBox.selectedItem as String
        settings.analyzeOnSaveOnly = onSaveOnlyCheckBox.isSelected
        updateStatus()
    }

    override fun reset() {
        val settings = GixySettings.getInstance()
        enabledCheckBox.isSelected = settings.enabled
        pathField.text = settings.gixyPath
        severityComboBox.selectedItem = settings.minimumSeverity
        onSaveOnlyCheckBox.isSelected = settings.analyzeOnSaveOnly
        updateStatus()
    }
}
