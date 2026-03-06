package com.getpagespeed.gixy.annotator

import org.junit.Assert.*
import org.junit.Test

class NginxFileDetectionTest {
    private val annotator = GixyExternalAnnotator()

    @Test
    fun `detects nginx conf`() {
        assertTrue(annotator.isNginxConfig("/etc/nginx/nginx.conf"))
    }

    @Test
    fun `detects any conf file`() {
        assertTrue(annotator.isNginxConfig("/etc/nginx/proxy.conf"))
    }

    @Test
    fun `detects sites-available`() {
        assertTrue(annotator.isNginxConfig("/etc/nginx/sites-available/mysite"))
    }

    @Test
    fun `detects sites-enabled`() {
        assertTrue(annotator.isNginxConfig("/etc/nginx/sites-enabled/default"))
    }

    @Test
    fun `detects conf d directory`() {
        assertTrue(annotator.isNginxConfig("/etc/nginx/conf.d/upstream.conf"))
    }

    @Test
    fun `detects nginx in path`() {
        assertTrue(annotator.isNginxConfig("/home/user/nginx/server.conf"))
    }

    @Test
    fun `detects case insensitive nginx conf`() {
        assertTrue(annotator.isNginxConfig("/opt/NGINX.conf"))
    }

    @Test
    fun `rejects non-nginx files`() {
        assertFalse(annotator.isNginxConfig("/home/user/app.py"))
    }

    @Test
    fun `rejects random txt files`() {
        assertFalse(annotator.isNginxConfig("/tmp/notes.txt"))
    }

    @Test
    fun `rejects java files`() {
        assertFalse(annotator.isNginxConfig("/src/main/App.java"))
    }
}
