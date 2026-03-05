package com.getpagespeed.gixy.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object GixyBinaryManager {
    private val LOG = Logger.getInstance(GixyBinaryManager::class.java)

    private const val GIXY_VERSION = "0.2.34"
    private const val GITHUB_RELEASE_BASE =
        "https://github.com/dvershinin/gixy/releases/download"

    private val binaryDir: Path
        get() {
            val home = System.getProperty("user.home")
            return Path.of(home, ".gixy-jetbrains", "bin")
        }

    fun getBinaryPath(): String? {
        val binary = binaryDir.resolve(binaryName()).toFile()
        if (binary.exists() && binary.canExecute()) {
            return binary.absolutePath
        }
        return null
    }

    fun needsDownload(): Boolean = getBinaryPath() == null

    fun download(): String? {
        val url = downloadUrl() ?: return null
        val targetDir = binaryDir.toFile()

        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        val targetFile = binaryDir.resolve(binaryName()).toFile()

        return try {
            LOG.info("Downloading gixy binary from: $url")
            val uri = URI.create(url)
            uri.toURL().openStream().use { input ->
                Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            targetFile.setExecutable(true)
            LOG.info("Gixy binary downloaded to: ${targetFile.absolutePath}")
            targetFile.absolutePath
        } catch (e: Exception) {
            LOG.warn("Failed to download gixy binary: ${e.message}")
            null
        }
    }

    private fun binaryName(): String {
        return if (SystemInfo.isWindows) "gixy.exe" else "gixy"
    }

    private fun platformSuffix(): String? {
        return when {
            SystemInfo.isMac -> "darwin-arm64"
            SystemInfo.isLinux && isArm64() -> "linux-aarch64"
            SystemInfo.isLinux -> "linux-x86_64"
            SystemInfo.isWindows -> "windows-x64"
            else -> null
        }
    }

    private fun isArm64(): Boolean {
        val arch = System.getProperty("os.arch", "").lowercase()
        return arch == "aarch64" || arch == "arm64"
    }

    private fun downloadUrl(): String? {
        val suffix = platformSuffix() ?: return null
        val ext = if (SystemInfo.isWindows) ".exe" else ""
        return "$GITHUB_RELEASE_BASE/v$GIXY_VERSION/gixy-$suffix$ext"
    }
}
