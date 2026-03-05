package com.getpagespeed.gixy.model

data class GixyFix(
    val title: String,
    val search: String,
    val replace: String,
    val description: String? = null,
)

data class GixyIssue(
    val plugin: String,
    val summary: String,
    val severity: String,
    val description: String,
    val reason: String? = null,
    val line: Int? = null,
    val file: String? = null,
    val path: String? = null,
    val config: String? = null,
    val reference: String? = null,
    val fixes: List<GixyFix> = emptyList(),
)
