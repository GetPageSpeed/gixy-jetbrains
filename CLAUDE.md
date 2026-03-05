# Gixy JetBrains Plugin

## Project Overview

JetBrains IDE plugin that integrates [gixy](https://github.com/dvershinin/gixy) (nginx security analyzer) as an ExternalAnnotator. Shows security findings as inline annotations with quick-fix suggestions. Freemium model planned: free annotations + paid Pro features (project-wide scan, reports, suppression).

See `PRD.md` for full task tracker with status and how-to for every step.

## Build & Run

**Prerequisite**: JDK 17+ via Homebrew.

```bash
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
```

| Command | What it does |
|---------|-------------|
| `./gradlew build` | Compile + checks |
| `./gradlew buildPlugin` | Produce installable ZIP in `build/distributions/` |
| `./gradlew runIde` | Launch sandbox IntelliJ with plugin loaded |
| `./gradlew publishPlugin` | Publish to Marketplace (needs `PUBLISH_TOKEN` env var) |
| `./gradlew verifyPlugin` | Verify compatibility with target platform |

**Testing gixy directly** (useful for debugging output format):
```bash
~/.virtualenvs/gixy/bin/python -m gixy --format json test-configs/nginx.conf
```

## Architecture

```
JetBrains IDE
  └── GixyExternalAnnotator (registered for language="", filters by filename)
        ├── collectInformation() — checks if file is nginx config by path pattern
        ├── doAnnotate() — runs gixy --format json <file>, parses JSON
        └── apply() — creates annotations with severity + quick-fixes
              ├── GixyQuickFix — applies search/replace from gixy fix suggestions
              └── GixyOpenDocsFix — opens reference URL in browser
```

- **Binary resolution order**: Settings path → `~/.gixy-jetbrains/bin/gixy` (downloaded) → system PATH
- **Nginx file detection**: Regex patterns matching `*.conf`, `sites-available/*`, `conf.d/*`, `*nginx*`
- **No custom language/file type**: Works alongside any nginx plugin the user has installed

## Key Files

| File | Purpose |
|------|---------|
| `src/main/resources/META-INF/plugin.xml` | Extension registrations, plugin metadata |
| `src/main/resources/META-INF/pluginIcon.svg` | Plugin icon (40x40 SVG, must be in META-INF/) |
| `src/main/kotlin/.../model/GixyIssue.kt` | Data classes for gixy JSON output |
| `src/main/kotlin/.../util/GixyRunner.kt` | Process execution, JSON parsing |
| `src/main/kotlin/.../util/GixyBinaryManager.kt` | Download/cache frozen binary |
| `src/main/kotlin/.../annotator/GixyExternalAnnotator.kt` | Core annotator + file detection |
| `src/main/kotlin/.../annotator/GixyQuickFix.kt` | Quick-fix actions |
| `src/main/kotlin/.../settings/GixySettings.kt` | Persistent state |
| `src/main/kotlin/.../settings/GixyConfigurable.kt` | Settings UI panel |
| `test-configs/nginx.conf` | Sample nginx config with known issues for testing |

## Conventions

- **Language**: Kotlin (JVM toolchain 17)
- **Target**: IntelliJ Platform 2024.1+ (build 241+), no upper bound
- **JSON parsing**: IntelliJ's bundled Gson — no extra dependencies
- **Extension registration**: Always in `plugin.xml`, not programmatically
- **Gradle**: IntelliJ Platform Gradle Plugin 2.x (`org.jetbrains.intellij.platform` v2.11.0)
- **Package**: `com.getpagespeed.gixy`

## Related Projects

- **Gixy** (the engine): `~/Projects/gixy/` — Python nginx security analyzer
- **PyInstaller workflow**: `~/Projects/gixy/.github/workflows/pyinstaller.yml` — builds frozen binaries
- **Gixy docs**: https://gixy.getpagespeed.com

## Gixy JSON Output Format

The plugin parses this structure (array of issues):
```json
[{
  "plugin": "version_disclosure",
  "summary": "Do not enable server_tokens on",
  "severity": "HIGH",
  "description": "...",
  "reason": "...",
  "line": 9,
  "file": "/path/to/nginx.conf",
  "path": "/path/to/nginx.conf",
  "reference": "https://gixy.getpagespeed.com/checks/version-disclosure/",
  "fixes": [{"title": "...", "search": "server_tokens on", "replace": "server_tokens off", "description": "..."}]
}]
```

Severity mapping: HIGH → Error, MEDIUM → Warning, LOW/UNSPECIFIED → Weak Warning.
