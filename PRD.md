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

### 1.2 PyInstaller Frozen Binaries (TODO)
**Why**: Lets users install the plugin without needing Python. Binary auto-downloads on first launch.

- [ ] Add `pyinstaller.yml` workflow to gixy repo (`.github/workflows/`)
  - **How**: Copy from `~/Projects/gixy/.github/workflows/pyinstaller.yml` (created by this session)
  - Trigger: on release published
  - Matrix: macOS arm64, macOS x86_64, Linux x86_64, Windows x64
  - Uploads binaries + checksums to GitHub release
- [ ] Test locally first:
  ```bash
  cd ~/Projects/gixy
  pip install pyinstaller
  pyinstaller --onefile --name gixy gixy/__main__.py
  # Test: ./dist/gixy --format json ~/Projects/gixy-jetbrains-plugin/test-configs/nginx.conf
  ```
- [ ] Tag a new gixy release to trigger the workflow
- [ ] Verify all 4 platform binaries appear on the GitHub release
- [ ] Update `GIXY_VERSION` in `GixyBinaryManager.kt` to match

### 1.3 Plugin Icon (DONE)
- [x] Add `gixy.svg` icon (40x40, shield + "G" design)
  - File at `src/main/resources/icons/gixy.svg`, registered in `plugin.xml`
- [x] Verify icon shows in `./gradlew runIde`

### 1.4 Manual Testing (TODO)
- [ ] Run `./gradlew runIde` to launch sandbox IntelliJ:
  ```bash
  export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
  cd ~/Projects/gixy-jetbrains-plugin
  ./gradlew runIde
  ```
- [ ] Open `test-configs/nginx.conf` in the sandbox IDE
- [ ] Verify annotations appear on lines 9 (server_tokens) and 20 (ssrf proxy_pass)
- [ ] Verify quick-fix works: click the fix for server_tokens → should change to `server_tokens off`
- [ ] Verify "View documentation" opens browser to gixy.getpagespeed.com
- [ ] Verify Settings > Tools > Gixy panel appears and works
- [ ] Test with gixy not available: set a bad path in settings, verify graceful handling

### 1.5 Create GitHub Repo (TODO)
- [ ] Create repo on GitHub:
  ```bash
  cd ~/Projects/gixy-jetbrains-plugin
  gh repo create getpagespeed/gixy-jetbrains --public --source=. --push
  ```
  Or if you prefer a different org/name, adjust accordingly.
- [ ] Add a LICENSE file (Apache 2.0 to match gixy)
- [ ] Push initial commit

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

### 1.7 README.md (TODO)
- [ ] Create `README.md` with:
  - Plugin description and screenshot
  - Installation (from Marketplace + manual ZIP)
  - Configuration (settings panel)
  - How it works (ExternalAnnotator + gixy)
  - Link to gixy docs
  - License

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
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
cd ~/Projects/gixy-jetbrains-plugin

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
- PyInstaller workflow: `~/Projects/gixy/.github/workflows/pyinstaller.yml`
- Test config: `~/Projects/gixy-jetbrains-plugin/test-configs/nginx.conf`
- Built plugin: `~/Projects/gixy-jetbrains-plugin/build/distributions/gixy-jetbrains-plugin-0.1.0.zip`
