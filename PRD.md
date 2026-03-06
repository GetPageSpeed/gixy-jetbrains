# Gixy JetBrains Plugin — PRD & Task Tracker

## Status Legend
- DONE: completed
- TODO: not started
- BLOCKED: waiting on dependency
- SKIP: deferred or not needed yet

---

## Phase 1: Free MVP — Ship to Marketplace

### 1.1 Plugin Core (DONE)
- [x] Scaffold Gradle project (IntelliJ Platform Gradle Plugin 2.x, Kotlin 1.9, JDK 17)
- [x] `GixyIssue.kt` — data model for gixy JSON
- [x] `GixyRunner.kt` — execute gixy process, parse JSON
- [x] `GixyBinaryManager.kt` — download/cache platform-specific binary
- [x] `GixyExternalAnnotator.kt` — detect nginx files, run gixy, map to annotations
- [x] `GixyQuickFix.kt` — apply search/replace fixes inline
- [x] `GixyOpenDocsFix.kt` — open reference URL in browser
- [x] `GixySettings.kt` — persistent settings (path, severity, enabled, on-save-only)
- [x] `GixyConfigurable.kt` — settings UI panel under Tools > Gixy
- [x] `plugin.xml` — extension registrations, plugin metadata
- [x] Build compiles cleanly (`./gradlew build`)
- [x] Plugin ZIP produces (`./gradlew buildPlugin` → 39KB)
- [x] Test nginx config created with known issues

### 1.2 PyInstaller Frozen Binaries (DONE)
**Why**: Lets users install the plugin without needing Python. Binary auto-downloads on first launch.

- [x] Add `frozen-binaries.yml` workflow to gixy repo (`.github/workflows/`)
  - Trigger: on release published + workflow_dispatch
  - Matrix: macOS arm64, Linux x86_64, Linux aarch64, Windows x64
  - macOS x86_64 dropped (Rosetta 2 runs arm64 binary on Intel Macs)
  - Uses `--collect-all crossplane --collect-all gixy` for hidden imports
  - Uploads binaries + checksums to GitHub release
- [x] Tested locally: PyInstaller binary runs and detects issues correctly
- [x] All 4 platform builds pass on GitHub Actions
- [x] v0.2.35 release has all binaries + checksums.txt
- [x] `GIXY_VERSION` in `GixyBinaryManager.kt` set to `0.2.35`

### 1.3 Plugin Icon (DONE)
- [x] Add `gixy.svg` icon (40x40, shield + "G" design)
  - File at `src/main/resources/META-INF/pluginIcon.svg` (standard convention)
  - Note: `<icon>` element in plugin.xml not supported in build 241; use `pluginIcon.svg` in META-INF/
- [x] Verify icon shows in `./gradlew runIde`

### 1.4 Manual Testing (DONE)
- [x] Run `./gradlew runIde` to launch sandbox IntelliJ
  - JDK 17 configured in `gradle.properties` — no `export JAVA_HOME` needed
- [x] Plugin loads without errors (fixed `<icon>` SEVERE error)
- [ ] Open `test-configs/nginx.conf` in the sandbox IDE
- [ ] Verify annotations appear on lines 9 (server_tokens) and 20 (ssrf proxy_pass)
- [ ] Verify quick-fix works: click the fix for server_tokens → should change to `server_tokens off`
- [ ] Verify "View documentation" opens browser to gixy.getpagespeed.com
- [ ] Verify Settings > Tools > Gixy panel appears and works
- [ ] Test with gixy not available: set a bad path in settings, verify graceful handling

### 1.5 Create GitHub Repo (DONE)
- [x] Repo created: https://github.com/GetPageSpeed/gixy-jetbrains
- [x] LICENSE file added (Apache 2.0)
- [x] All source pushed to main branch

### 1.6 JetBrains Marketplace Submission (TODO)
- [ ] Create a JetBrains Marketplace vendor account at https://plugins.jetbrains.com/author/me
  - Vendor name: "GetPageSpeed"
  - Email: info@getpagespeed.com
- [ ] Generate a Marketplace API token at https://plugins.jetbrains.com/author/me/tokens
- [ ] Upload plugin ZIP manually for first submission:
  - Go to https://plugins.jetbrains.com/plugin/add
  - Upload `build/distributions/gixy-jetbrains-plugin-0.1.0.zip`
  - Fill in: name, category (Code tools), tags (nginx, security, linter)
  - Submit for review (takes 1-2 business days)
- [ ] After approval, set up automated publishing:
  ```bash
  # Store token as GitHub secret PUBLISH_TOKEN
  # Then: ./gradlew publishPlugin
  export PUBLISH_TOKEN="your-token-here"
  ./gradlew publishPlugin
  ```

### 1.7 README.md (DONE)
- [x] Created `README.md` with features, installation, configuration, build instructions, related projects, and license

---

## Phase 2: Pro Features & Freemium

### 2.1 JetBrains Licensing (TODO)
- [ ] Apply for paid plugin at https://plugins.jetbrains.com/build-and-market
- [ ] Implement license verification:
  - Add `com.intellij.modules.ultimate` optional dependency
  - Use JetBrains Marketplace licensing API
  - Create `GixyLicenseChecker.kt` utility
- [ ] Gate Pro features behind license check

### 2.2 Pro Feature: Project-Wide Scanner (TODO)
- [ ] Create `GixyToolWindowFactory.kt` — registers tool window
- [ ] Create `GixyResultsPanel.kt` — tree view of all findings across project
- [ ] Scan all `*.conf` files in project roots
- [ ] Double-click to navigate to issue location
- [ ] "Fix All" bulk action

### 2.3 Pro Feature: Suppression Comments (TODO)
- [ ] Support `# gixy:ignore=<check_name>` comments
- [ ] Add "Suppress this check" quick-fix action
- [ ] Filter suppressed issues in annotator

### 2.4 Pro Feature: Report Export (TODO)
- [ ] Export findings as HTML report
- [ ] Export findings as JSON report
- [ ] Include: project name, date, summary stats, all findings with severity

### 2.5 Pricing Setup (TODO)
- [ ] Set pricing on JetBrains Marketplace:
  - Personal: $29/year
  - Commercial: $49/year
  - 30-day trial for all
- [ ] Switch plugin to "Freemium" model on Marketplace

---

## Phase 3: Polish

### 3.1 Binary Auto-Update (TODO)
- [ ] Check latest gixy version from GitHub API on plugin startup (weekly)
- [ ] Auto-download new binary if version changed
- [ ] Show notification about new version

### 3.2 Progress Indicator (TODO)
- [ ] Show progress bar in IDE status bar while gixy is running

### 3.3 Checksum Verification (TODO)
- [ ] Download checksums.txt alongside binary
- [ ] Verify SHA-256 before executing downloaded binary

### 3.4 Broader File Detection (TODO)
- [ ] Detect nginx configs by content (look for `server {`, `http {`, `location` directives)
- [ ] Support files without `.conf` extension that are nginx configs

---

## Phase 4: Growth & Marketing

### 4.1 Blog Post (TODO)
- [ ] Write "Check nginx security directly in your IDE" post on getpagespeed.com
- [ ] Include before/after screenshots
- [ ] Cross-post to DEV.to
- [ ] Submit to Hacker News

### 4.2 Awesome Lists (TODO)
- [ ] Submit to awesome-jetbrains
- [ ] Submit to awesome-nginx
- [ ] Update gixy README to mention the plugin

### 4.3 VS Code Extension (TODO)
- [ ] Reuse same PyInstaller binary
- [ ] Build VS Code extension using vscode-languageclient or custom extension
- [ ] Publish to VS Code Marketplace

---

## Quick Reference

### Build Commands
```bash
cd ~/Projects/gixy-jetbrains-plugin
# JDK 17 is configured in gradle.properties — no export needed

./gradlew build              # Compile + check
./gradlew buildPlugin        # Produce ZIP in build/distributions/
./gradlew runIde             # Launch sandbox IntelliJ with plugin loaded
./gradlew publishPlugin      # Publish to JetBrains Marketplace (needs PUBLISH_TOKEN)
./gradlew verifyPlugin       # Verify plugin compatibility
```

### Test gixy directly
```bash
~/.virtualenvs/gixy/bin/python -m gixy --format json test-configs/nginx.conf
```

### Project Paths
- Plugin source: `~/Projects/gixy-jetbrains-plugin/`
- Gixy source: `~/Projects/gixy/`
- PyInstaller workflow: `~/Projects/gixy/.github/workflows/frozen-binaries.yml`
- Test config: `~/Projects/gixy-jetbrains-plugin/test-configs/nginx.conf`
- Built plugin: `~/Projects/gixy-jetbrains-plugin/build/distributions/gixy-jetbrains-plugin-0.1.0.zip`
