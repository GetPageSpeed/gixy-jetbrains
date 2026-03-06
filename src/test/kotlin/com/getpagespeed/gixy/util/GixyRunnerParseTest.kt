package com.getpagespeed.gixy.util

import org.junit.Assert.*
import org.junit.Test

class GixyRunnerParseTest {

    @Test
    fun `parse empty string returns empty list`() {
        assertEquals(emptyList<Any>(), GixyRunner.parseOutput(""))
    }

    @Test
    fun `parse blank string returns empty list`() {
        assertEquals(emptyList<Any>(), GixyRunner.parseOutput("   "))
    }

    @Test
    fun `parse invalid json returns empty list`() {
        assertEquals(emptyList<Any>(), GixyRunner.parseOutput("not json"))
    }

    @Test
    fun `parse empty array returns empty list`() {
        assertEquals(emptyList<Any>(), GixyRunner.parseOutput("[]"))
    }

    @Test
    fun `parse single issue with all fields`() {
        val json = """
        [{
          "plugin": "version_disclosure",
          "summary": "Do not enable server_tokens on",
          "severity": "HIGH",
          "description": "Allows attacker to learn NGINX version",
          "reason": "server_tokens is on",
          "line": 9,
          "file": "/etc/nginx/nginx.conf",
          "path": "/etc/nginx/nginx.conf",
          "config": "server_tokens on;",
          "reference": "https://gixy.getpagespeed.com/checks/version-disclosure/",
          "fixes": [{
            "title": "Set server_tokens off",
            "search": "server_tokens on",
            "replace": "server_tokens off",
            "description": "Disable version disclosure"
          }]
        }]
        """.trimIndent()

        val issues = GixyRunner.parseOutput(json)
        assertEquals(1, issues.size)

        val issue = issues[0]
        assertEquals("version_disclosure", issue.plugin)
        assertEquals("Do not enable server_tokens on", issue.summary)
        assertEquals("HIGH", issue.severity)
        assertEquals("Allows attacker to learn NGINX version", issue.description)
        assertEquals("server_tokens is on", issue.reason)
        assertEquals(9, issue.line)
        assertEquals("/etc/nginx/nginx.conf", issue.file)
        assertEquals("https://gixy.getpagespeed.com/checks/version-disclosure/", issue.reference)

        assertEquals(1, issue.fixes.size)
        val fix = issue.fixes[0]
        assertEquals("Set server_tokens off", fix.title)
        assertEquals("server_tokens on", fix.search)
        assertEquals("server_tokens off", fix.replace)
        assertEquals("Disable version disclosure", fix.description)
    }

    @Test
    fun `parse issue without optional fields`() {
        val json = """
        [{
          "plugin": "ssrf",
          "summary": "Possible SSRF",
          "severity": "HIGH",
          "description": "May allow arbitrary requests"
        }]
        """.trimIndent()

        val issues = GixyRunner.parseOutput(json)
        assertEquals(1, issues.size)

        val issue = issues[0]
        assertEquals("ssrf", issue.plugin)
        assertNull(issue.reason)
        assertNull(issue.line)
        assertNull(issue.file)
        assertNull(issue.reference)
        assertTrue(issue.fixes.isEmpty())
    }

    @Test
    fun `parse multiple issues`() {
        val json = """
        [
          {"plugin": "a", "summary": "A", "severity": "HIGH", "description": "desc A"},
          {"plugin": "b", "summary": "B", "severity": "LOW", "description": "desc B"}
        ]
        """.trimIndent()

        val issues = GixyRunner.parseOutput(json)
        assertEquals(2, issues.size)
        assertEquals("a", issues[0].plugin)
        assertEquals("b", issues[1].plugin)
    }

    @Test
    fun `parse issue with empty fixes array`() {
        val json = """
        [{"plugin": "test", "summary": "Test", "severity": "MEDIUM", "description": "desc", "fixes": []}]
        """.trimIndent()

        val issues = GixyRunner.parseOutput(json)
        assertEquals(1, issues.size)
        assertTrue(issues[0].fixes.isEmpty())
    }

    @Test
    fun `parse issue with multiple fixes`() {
        val json = """
        [{
          "plugin": "test",
          "summary": "Test",
          "severity": "HIGH",
          "description": "desc",
          "fixes": [
            {"title": "Fix 1", "search": "a", "replace": "b"},
            {"title": "Fix 2", "search": "c", "replace": "d", "description": "second fix"}
          ]
        }]
        """.trimIndent()

        val issues = GixyRunner.parseOutput(json)
        assertEquals(2, issues[0].fixes.size)
        assertEquals("Fix 1", issues[0].fixes[0].title)
        assertNull(issues[0].fixes[0].description)
        assertEquals("second fix", issues[0].fixes[1].description)
    }

    @Test
    fun `missing severity defaults to MEDIUM`() {
        val json = """[{"plugin": "x", "summary": "X", "description": "d"}]"""
        val issues = GixyRunner.parseOutput(json)
        assertEquals("MEDIUM", issues[0].severity)
    }
}
