package com.getpagespeed.gixy.annotator

import com.intellij.lang.annotation.HighlightSeverity
import org.junit.Assert.*
import org.junit.Test

class SeverityMappingTest {
    private val annotator = GixyExternalAnnotator()

    @Test
    fun `HIGH maps to ERROR`() {
        assertEquals(HighlightSeverity.ERROR, annotator.mapSeverity("HIGH"))
    }

    @Test
    fun `MEDIUM maps to WARNING`() {
        assertEquals(HighlightSeverity.WARNING, annotator.mapSeverity("MEDIUM"))
    }

    @Test
    fun `LOW maps to WEAK_WARNING`() {
        assertEquals(HighlightSeverity.WEAK_WARNING, annotator.mapSeverity("LOW"))
    }

    @Test
    fun `UNSPECIFIED maps to WEAK_WARNING`() {
        assertEquals(HighlightSeverity.WEAK_WARNING, annotator.mapSeverity("UNSPECIFIED"))
    }

    @Test
    fun `unknown severity maps to WEAK_WARNING`() {
        assertEquals(HighlightSeverity.WEAK_WARNING, annotator.mapSeverity("BOGUS"))
    }
}
