package com.getpagespeed.gixy.settings

import com.getpagespeed.gixy.util.GixyBinaryManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JComboBox

class GixyConfigurable : Configurable {
    private var enabledCheckBox = JBCheckBox("Enable Gixy analysis")
    private var severityComboBox = JComboBox(arrayOf("UNSPECIFIED", "LOW", "MEDIUM", "HIGH"))
    private var onSaveOnlyCheckBox = JBCheckBox("Analyze on save only (improves performance)")
    private var statusLabel = JBLabel()

    override fun getDisplayName(): String = "Gixy"

    override fun createComponent(): JComponent {
        updateStatus()

        return panel {
            row {
                cell(enabledCheckBox)
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
        val binaryPath = GixyBinaryManager.getBinaryPath()
        statusLabel.text = if (binaryPath != null) {
            "<html><b>Bundled</b> v${GixyBinaryManager.GIXY_VERSION}<br><small>$binaryPath</small></html>"
        } else {
            "Binary not available for this platform"
        }
    }

    override fun isModified(): Boolean {
        val settings = GixySettings.getInstance()
        return enabledCheckBox.isSelected != settings.enabled
                || severityComboBox.selectedItem as String != settings.minimumSeverity
                || onSaveOnlyCheckBox.isSelected != settings.analyzeOnSaveOnly
    }

    override fun apply() {
        val settings = GixySettings.getInstance()
        settings.enabled = enabledCheckBox.isSelected
        settings.minimumSeverity = severityComboBox.selectedItem as String
        settings.analyzeOnSaveOnly = onSaveOnlyCheckBox.isSelected
    }

    override fun reset() {
        val settings = GixySettings.getInstance()
        enabledCheckBox.isSelected = settings.enabled
        severityComboBox.selectedItem = settings.minimumSeverity
        onSaveOnlyCheckBox.isSelected = settings.analyzeOnSaveOnly
    }
}
