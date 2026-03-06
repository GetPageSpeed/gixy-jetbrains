package com.getpagespeed.gixy.util

import com.getpagespeed.gixy.model.GixyFix
import com.getpagespeed.gixy.model.GixyIssue
import com.getpagespeed.gixy.settings.GixySettings
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object GixyRunner {
    private val LOG = Logger.getInstance(GixyRunner::class.java)
    private val gson = Gson()

    fun run(filePath: String): List<GixyIssue> {
        val executable = resolveExecutable() ?: return emptyList()

        val command = listOf(executable, "--format", "json", "--disable-includes", filePath)

        return try {
            val process = ProcessBuilder(command)
                .redirectErrorStream(false)
                .start()

            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()

            val finished = process.waitFor(30, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                LOG.warn("Gixy process timed out for: $filePath")
                return emptyList()
            }

            if (stderr.isNotBlank()) {
                LOG.debug("Gixy stderr: $stderr")
            }

            parseOutput(stdout)
        } catch (e: IOException) {
            LOG.warn("Failed to run gixy: ${e.message}")
            emptyList()
        }
    }

    enum class ExecutableSource { BUNDLED, CUSTOM }

    data class ResolvedExecutable(val path: String, val source: ExecutableSource)

    fun resolveExecutable(): String? = resolveExecutableWithSource()?.path

    fun resolveExecutableWithSource(): ResolvedExecutable? {
        val binaryPath = GixyBinaryManager.getBinaryPath()
        if (binaryPath != null) {
            return ResolvedExecutable(binaryPath, ExecutableSource.BUNDLED)
        }

        val settings = GixySettings.getInstance()
        if (settings.gixyPath.isNotBlank()) {
            val file = File(settings.gixyPath)
            if (file.exists() && file.canExecute()) {
                return ResolvedExecutable(settings.gixyPath, ExecutableSource.CUSTOM)
            }
        }

        return null
    }

    fun getVersion(executable: String): String? {
        return try {
            val process = ProcessBuilder(executable, "--version")
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().readText().trim()
            val finished = process.waitFor(5, TimeUnit.SECONDS)
            if (!finished) {
                process.destroyForcibly()
                return null
            }
            output.ifBlank { null }
        } catch (e: IOException) {
            null
        }
    }

    private fun parseOutput(json: String): List<GixyIssue> {
        if (json.isBlank()) return emptyList()

        return try {
            val array = gson.fromJson(json, JsonArray::class.java) ?: return emptyList()
            array.mapNotNull { element ->
                val obj = element as? JsonObject ?: return@mapNotNull null
                GixyIssue(
                    plugin = obj.get("plugin")?.asString ?: "",
                    summary = obj.get("summary")?.asString ?: "",
                    severity = obj.get("severity")?.asString ?: "MEDIUM",
                    description = obj.get("description")?.asString ?: "",
                    reason = obj.get("reason")?.asString,
                    line = obj.get("line")?.asInt,
                    file = obj.get("file")?.asString,
                    path = obj.get("path")?.asString,
                    config = obj.get("config")?.asString,
                    reference = obj.get("reference")?.asString,
                    fixes = parseFixes(obj),
                )
            }
        } catch (e: Exception) {
            LOG.warn("Failed to parse gixy output: ${e.message}")
            emptyList()
        }
    }

    private fun parseFixes(obj: JsonObject): List<GixyFix> {
        val fixesArray = obj.getAsJsonArray("fixes") ?: return emptyList()
        return fixesArray.mapNotNull { element ->
            val fixObj = element as? JsonObject ?: return@mapNotNull null
            GixyFix(
                title = fixObj.get("title")?.asString ?: "",
                search = fixObj.get("search")?.asString ?: "",
                replace = fixObj.get("replace")?.asString ?: "",
                description = fixObj.get("description")?.asString,
            )
        }
    }
}
