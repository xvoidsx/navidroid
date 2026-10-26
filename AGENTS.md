# AGENTS.md — navidroid

How to work in this repo. Short and accurate beats long and stale — update
this file the same night a lesson lands.

## What this is

navidroid is xvoidsx's minimal, distractionless Android launcher — the phone
companion to navi. A hard fork of s4nj1th/Slauncher (itself a fork of
Olauncher). **Upstream is cherry-picks now, not merges** — navidroid goes its
own way. GPL-3.0; the fork attribution and LICENSE stay.

## Build and release — CI only

- **Gradle does NOT build locally in this sandbox.** The daemon protocol
  fails the same way every time (client dispatch dies with
  `MessageIOException` after the daemon idles). Never trust a sandbox
  native-build failure as a code failure; never claim a build works without
  the CI run.
- `.github/workflows/build-apk.yml` — push-triggered on `app/**`, uploads the
  `navidroid-debug-apk` artifact. This is the compile check.
- `.github/workflows/release.yml` — `workflow_dispatch` with a `version`
  input: builds the debug APK and cuts a prerelease with
  `navidroid-<version>.apk`. The release body lives hardcoded in that file;
  update it before dispatching, not after.
- Version bumps touch `app/build.gradle` only: `versionCode` +1 and
  `versionName` per release (v0.2.1 = 106).
- **Signing:** `app/debug.keystore` is a committed shared debug keystore with
  a `signingConfigs.debug` block, so preview builds update over each other.
  A real release signing key is still open for 1.0.
- **Dispatch quirk:** `gh-api` POSTs to the dispatch endpoint error with
  “provider response was not valid JSON” even on success — GitHub answers
  dispatches with 204 No Content, which the helper cannot parse. Check the
  runs list to confirm, and **never double-dispatch** on that error.
- **Workflow files need the Contents API.** The Git Data API (gh-push-mika)
  returns 422 on `.github/workflows/` even with workflow scope. Contents API
  PUT with the current blob sha works.

## Pushing

- Push with `gh-push-mika`, explicit every time: `--repo xvoidsx/navidroid
  --branch main --root <tree> --message "<msg>" <repo-relative paths...>`.
  It defaults `--repo` to xvoidsx/navi and `--branch` to main — omitting
  either has shipped work to the wrong repo before.
- Before every push, fetch the remote file(s) and diff against local. Local
  trees go stale in either direction; a file-level diff is not enough after
  days of drift — do a fresh full-tree diff when in doubt.
- After every push, re-fetch remote and grep the change to verify live.
- Deleting remote files: Contents API DELETE with the blob sha from GET,
  one commit per file.

## Where things live

- `data/Agents.kt` — the curated agent table (`Agent` + `CURATED_AGENTS` +
  `agentForPackage()`). **An empty package means hidden from the picker** —
  that is how a feature can never offer a dead button (HuggingChat: no
  current Play listing, so it stays hidden until it has a store route).
- `ui/AgentPicker.kt` — one shared single-choice dialog for home and
  Settings; “None” clears the default.
- `ui/AgentGlimmer.kt` — the agent line's neon wave: an animated
  `LinearGradient` shader (pink → green → cyan → pink, `TileMode.REPEAT`,
  one band per 3.2 s cycle, seamless loop). Started in `populateAgentLine`,
  stopped in `onPause`/`onDestroyView` — a foreground flourish only, never
  animate a line nobody is looking at. Colour-emoji glyphs ignore the
  shader, so the ✨ keeps its own colours while the words glimmer.
- `ui/HomeFragment.kt` — `populateAgentLine()` (always visible; “Ask
  <name>” or “Choose your agent”), tap = launch-or-Play, long-press =
  quick-switch. `tvAgentLine` lives in **both** `res/layout` and
  `res/layout-land` — view binding requires every new id in both, or the
  reference is null on rotation.
- `ui/SettingsFragment.kt` — the Default AI Agent row after the calendar
  row, in both settings layouts.
- `data/Prefs.kt` — `defaultAgentPackage` (SharedPreferences, package
  stored). Settings row stays plain — the glimmer is the home line's
  signature.

## Product rules (standing)

- **Portrait-first.** Allow-landscape defaults OFF; rotation is opt-in.
- **Calendar line:** the earliest not-yet-ended event wins; countdown uses
  the app's idiom (“40m”, “2h 15m”): future → “in …”, ongoing → “ends in …”.
- **Agent line:** always visible, never plain white. Installed → launch via
  the existing `launchApp` path; not installed → Play listing
  (`market://details?id=` with an https fallback for devices without Play).
- **Launch-only nuance, verified:** the current Copilot listing is
  `com.microsoft.office.officehubrow`; the old standalone
  `com.microsoft.copilot` was removed from Play, so a device with the old
  app gets the Play listing instead of a launch.
- **Media player at the bottom of the screen** — a now-playing line above
  it is redundant; don't add one.
- **Verify permissions in the BUILT APK**, never just the source manifest.
- **Result pickers use a trampoline**, never a singleTask activity.
- **Screenshots uploaded via chat are JPEG bytes even when named .png.**
  Name image files by their actual format.

## Current state (26 Oct 2026)

- v0.2.1 (106) is the latest public preview: v0.2.0's Default AI Agent
  (19 live curated agents, launch-or-Play, long-press quick-switch) +
  calendar countdown + the agent-line glimmer.
- Sequenced next: weather line, then the Lain bar — which stays **gated on
  the agent-story decision** (engine, tap action, local-first). The agent
  button does not lift that gate. Later: quick capture, voice,
  swipe-to-Lain, F-Droid.
- The navi-site page lives at `navidroid.html` in xvoidsx/enter-the-wired
  (not a navi-site repo — none exists); its screenshots are copies of the
  ones in `screenshots/` here.
