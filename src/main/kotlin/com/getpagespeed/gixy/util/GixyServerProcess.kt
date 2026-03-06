package com.getpagespeed.gixy.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

@Service(Service.Level.APP)
class GixyServerProcess : Disposable {
    private val log = Logger.getInstance(GixyServerProcess::class.java)
    private val gson = Gson()
    private val lock = ReentrantLock()
    private val requestIdCounter = AtomicInteger(1)

    private var process: Process? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null

    fun analyze(filePath: String, content: String): String? {
        lock.lock()
        try {
            if (!ensureRunning()) return null

            val id = requestIdCounter.getAndIncrement()
            val request = JsonObject().apply {
                addProperty("id", id)
                addProperty("method", "analyze")
                addProperty("filename", filePath)
                addProperty("content", content)
            }

            writer?.write(gson.toJson(request))
            writer?.newLine()
            writer?.flush()

            val response = reader?.readLine() ?: run {
                stopServer()
                return null
            }

            val responseObj = gson.fromJson(response, JsonObject::class.java)
            if (responseObj.has("error") && !responseObj.get("error").isJsonNull) {
                log.warn("Gixy server error: ${responseObj.get("error").asString}")
            }

            return if (responseObj.has("issues")) {
                responseObj.getAsJsonArray("issues").toString()
            } else {
                null
            }
        } catch (e: IOException) {
            log.warn("Gixy server communication failed: ${e.message}")
            stopServer()
            return null
        } finally {
            lock.unlock()
        }
    }

    private fun ensureRunning(): Boolean {
        if (process?.isAlive == true) return true
        return startServer()
    }

    private fun startServer(): Boolean {
        val executable = GixyBinaryManager.getBinaryPath() ?: return false

        return try {
            val pb = ProcessBuilder(executable, "--server", "--disable-includes")
                .redirectErrorStream(false)
            process = pb.start()
            writer = process!!.outputStream.bufferedWriter()
            reader = process!!.inputStream.bufferedReader()

            val readyLine = reader?.readLine()
            if (readyLine != null) {
                val readyObj = gson.fromJson(readyLine, JsonObject::class.java)
                if (readyObj.has("ready") && readyObj.get("ready").asBoolean) {
                    log.info("Gixy server started, version: ${readyObj.get("version")?.asString}")
                    return true
                }
            }

            log.warn("Gixy server did not send ready message")
            stopServer()
            false
        } catch (e: IOException) {
            log.warn("Failed to start gixy server: ${e.message}")
            false
        }
    }

    private fun stopServer() {
        try { writer?.close() } catch (_: Exception) {}

        process?.let { p ->
            if (p.isAlive) {
                p.destroy()
                try {
                    p.waitFor(3, TimeUnit.SECONDS)
                } catch (_: Exception) {}
                if (p.isAlive) {
                    p.destroyForcibly()
                }
            }
        }

        process = null
        writer = null
        reader = null
    }

    override fun dispose() {
        stopServer()
    }
}
