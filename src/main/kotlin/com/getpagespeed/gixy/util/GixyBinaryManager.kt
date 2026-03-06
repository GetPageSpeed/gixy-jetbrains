package com.getpagespeed.gixy.util

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object GixyBinaryManager {
    private val LOG = Logger.getInstance(GixyBinaryManager::class.java)

    private val binaryDir: Path
        get() {
            val home = System.getProperty("user.home")
            return Path.of(home, ".gixy-jetbrains", "bin")
        }

    fun getBinaryPath(): String? {
        val binary = binaryDir.resolve(localBinaryName()).toFile()
        if (binary.exists() && binary.canExecute()) {
            return binary.absolutePath
        }
        return extractBundled()
    }

    private fun extractBundled(): String? {
        val resourceName = bundledResourceName() ?: return null
        val resourcePath = "/binaries/$resourceName"
        val inputStream = GixyBinaryManager::class.java.getResourceAsStream(resourcePath)
        if (inputStream == null) {
            LOG.warn("Bundled binary not found: $resourcePath")
            return null
        }

        val targetDir = binaryDir.toFile()
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        val targetFile = binaryDir.resolve(localBinaryName()).toFile()
        return try {
            inputStream.use { input ->
                Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }
            targetFile.setExecutable(true)
            LOG.info("Extracted bundled gixy binary to: ${targetFile.absolutePath}")
            targetFile.absolutePath
        } catch (e: Exception) {
            LOG.warn("Failed to extract bundled gixy binary: ${e.message}")
            null
        }
    }

    private fun localBinaryName(): String {
        return if (SystemInfo.isWindows) "gixy.exe" else "gixy"
    }

    private fun bundledResourceName(): String? {
        return when {
            SystemInfo.isMac -> "gixy-darwin-arm64"
            SystemInfo.isLinux && isArm64() -> "gixy-linux-aarch64"
            SystemInfo.isLinux -> "gixy-linux-x86_64"
            SystemInfo.isWindows -> "gixy-windows-x64.exe"
            else -> null
        }
    }

    private fun isArm64(): Boolean {
        val arch = System.getProperty("os.arch", "").lowercase()
        return arch == "aarch64" || arch == "arm64"
    }
}
