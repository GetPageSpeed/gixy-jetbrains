# Gixy - Nginx Security Scanner for JetBrains IDEs

A JetBrains IDE plugin that integrates [gixy](https://github.com/dvershinin/gixy), the nginx configuration security analyzer. Get real-time security findings as inline annotations with one-click fixes, right in your editor.

## Features

- **30+ security checks** — SSRF, header injection, path traversal, version disclosure, and more
- **Inline annotations** — security issues highlighted directly in the editor with severity levels (Error, Warning, Weak Warning)
- **Quick-fix suggestions** — one-click fixes for common misconfigurations
- **Documentation links** — jump to detailed docs for each finding
- **Automatic binary download** — no Python required; a frozen gixy binary is downloaded on first use
- **Configurable** — adjust severity threshold, set custom binary path, toggle on-save-only mode

## Installation

### From JetBrains Marketplace

1. Open **Settings** > **Plugins** > **Marketplace**
2. Search for **"Gixy"**
3. Click **Install** and restart the IDE

### Manual Install

1. Download the latest ZIP from [Releases](https://github.com/getpagespeed/gixy-jetbrains/releases)
2. Open **Settings** > **Plugins** > gear icon > **Install Plugin from Disk...**
3. Select the downloaded ZIP

## How It Works

The plugin uses the [ExternalAnnotator](https://plugins.jetbrains.com/docs/intellij/external-annotator.html) API to run gixy in the background whenever you open or edit an nginx configuration file.

1. **File detection** — matches `*.conf`, `sites-available/*`, `conf.d/*`, and other common nginx config patterns
2. **Background analysis** — runs `gixy --format json <file>` and parses the JSON output
3. **Annotation** — maps each finding to an inline annotation with appropriate severity
4. **Quick-fixes** — offers search/replace fixes from gixy and links to reference documentation

## Configuration

Open **Settings** > **Tools** > **Gixy** to configure:

| Setting | Description | Default |
|---------|-------------|---------|
| Enable Gixy | Toggle the analyzer on/off | Enabled |
| Gixy path | Custom path to gixy binary | Auto-detected |
| Minimum severity | Only show findings at or above this level | LOW |
| On save only | Run analysis only when the file is saved | Disabled |

## Requirements

- **JetBrains IDE** 2024.1 or later (IntelliJ IDEA, WebStorm, PyCharm, etc.)
- **gixy binary** — automatically downloaded on first use, or install manually:
  ```bash
  pip install gixy-ng
  ```

## Building from Source

Requires JDK 17+.

```bash
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home

./gradlew build          # Compile + checks
./gradlew buildPlugin    # Produce installable ZIP in build/distributions/
./gradlew runIde         # Launch sandbox IDE with plugin loaded
./gradlew verifyPlugin   # Verify compatibility with target platform
```

## Related Projects

- [gixy](https://github.com/dvershinin/gixy) — the nginx security analyzer engine
- [Gixy documentation](https://gixy.getpagespeed.com) — detailed check descriptions and remediation advice

## License

Apache 2.0 — see [LICENSE](LICENSE) for details.
