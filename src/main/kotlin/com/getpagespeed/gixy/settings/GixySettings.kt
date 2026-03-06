package com.getpagespeed.gixy.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "GixySettings",
    storages = [Storage("gixy.xml")]
)
class GixySettings : PersistentStateComponent<GixySettings.State> {
    data class State(
        var enabled: Boolean = true,
        var minimumSeverity: String = "LOW",
        var analyzeOnSaveOnly: Boolean = false,
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    var enabled: Boolean
        get() = state.enabled
        set(value) { state.enabled = value }

    var minimumSeverity: String
        get() = state.minimumSeverity
        set(value) { state.minimumSeverity = value }

    var analyzeOnSaveOnly: Boolean
        get() = state.analyzeOnSaveOnly
        set(value) { state.analyzeOnSaveOnly = value }

    companion object {
        fun getInstance(): GixySettings {
            return ApplicationManager.getApplication().getService(GixySettings::class.java)
        }
    }
}
