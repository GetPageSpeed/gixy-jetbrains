import de.undercouch.gradle.tasks.download.Download
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.11.0"
    id("de.undercouch.download") version "5.6.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

val gixyVersion = "0.2.35"
val gixyBinaries = listOf(
    "gixy-darwin-arm64",
    "gixy-linux-x86_64",
    "gixy-linux-aarch64",
    "gixy-windows-x64.exe",
)

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(providers.gradleProperty("platformVersion").get())
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("junit:junit:4.13.2")
}

val downloadGixyBinaries by tasks.registering(Download::class) {
    val baseUrl = "https://github.com/dvershinin/gixy/releases/download/v$gixyVersion"
    src(gixyBinaries.map { "$baseUrl/$it" })
    dest(layout.buildDirectory.dir("gixy-binaries"))
    overwrite(false)
}

tasks.named<Copy>("processResources") {
    dependsOn(downloadGixyBinaries)
    from(layout.buildDirectory.dir("gixy-binaries")) {
        into("binaries")
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
        untilBuild.set(provider { "" })
    }

    publishPlugin {
        token.set(providers.environmentVariable("PUBLISH_TOKEN"))
    }

    signPlugin {
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }
}
