package com.getpagespeed.gixy

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.fileTypes.PlainTextLanguage
import javax.swing.Icon

object NginxConfFileType : LanguageFileType(PlainTextLanguage.INSTANCE) {
    override fun getName(): String = "Nginx Configuration"
    override fun getDescription(): String = "Nginx configuration file"
    override fun getDefaultExtension(): String = "conf"
    override fun getIcon(): Icon? = null
}
