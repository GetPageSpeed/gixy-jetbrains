# Gixy JetBrains Plugin

## Project Overview

JetBrains IDE plugin that integrates gixy (nginx security analyzer) as an ExternalAnnotator. Shows security findings as inline annotations with quick-fix suggestions.

## Build & Run

- **JDK**: 17+ (use `JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home`)
- **Build**: `./gradlew build`
- **Run IDE**: `./gradlew runIde` (launches a sandbox IntelliJ with the plugin)
- **Tests**: `./gradlew test`
- **Package**: `./gradlew buildPlugin` (produces ZIP in `build/distributions/`)

## Architecture

- **ExternalAnnotator pattern**: Runs `gixy --format json <file>` externally, parses JSON output, maps to IDE annotations
- **Binary management**: Auto-downloads platform-specific frozen gixy binary on first launch; falls back to system `gixy` on PATH
- **Plugin.xml**: `src/main/resources/META-INF/plugin.xml` — extension point registrations
- **No custom language**: Works with any file type; filters by nginx config filename patterns in the annotator

## Key Packages

- `com.getpagespeed.gixy.model` — Data classes for gixy JSON output
- `com.getpagespeed.gixy.util` — GixyRunner (process exec), GixyBinaryManager (download/cache)
- `com.getpagespeed.gixy.annotator` — ExternalAnnotator + QuickFix implementations
- `com.getpagespeed.gixy.settings` — Persistent plugin settings + UI panel

## Conventions

- Language: Kotlin
- Target: IntelliJ Platform 2024.1+ (build 241+)
- Use IntelliJ's bundled Gson for JSON parsing (no extra deps)
- Register extensions in plugin.xml, not programmatically
