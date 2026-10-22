# Smart Pantry Manager
A Java Android application for Mobile App Development 700, personalised for Le'taya.
Cream, blush and gold styling; offline pantry management and strict recipe matching.

## Development status and evidence
Public repository: https://github.com/letayaAnne/smart-pantry-manager

Work began locally on 22 October 2026 with AI assistance. The original nine local
steps were imported into GitHub with their source hashes in the messages. Import
timestamps are upload times, not evidence of work over several days. See
DEVELOPMENT_HISTORY.md and docs/ORIGINAL_LOCAL_COMMITS.txt. The original Git objects are preserved in
`docs/original-development.bundle` (`git bundle verify` can inspect it). No history is backdated.
The assignment's GitHub-from-the-start requirement was not met.

The current project compiles and its 30 unit tests pass. Android Lint has passed
its error gate; warnings are not claimed to be absent. The Actions workflow also
runs a fresh emulator, checks CRUD and matching, captures actual screenshots,
and checks persistence after force-stop/relaunch. Consult the latest completed
Actions run for its result. Samsung A23 testing and the student's narrated video
are separate remaining tasks.

## Database choice
SQLiteOpenHelper: local relational storage, no account or internet required, and a direct
connection to the module's SQLite teaching. SharedPreferences stores one UI preference.

## Build and run
Use JDK 17 and Android SDK platform 35 / build-tools 35.0.0. The Gradle wrapper
uses Gradle 8.13 and the Android Gradle plugin is pinned to 8.9.2.

1. Clone this repository and open the project folder in Android Studio.
2. Let Gradle sync and install the required SDK packages if requested.
3. Select an Android 8.0+ emulator or USB-debugging device and choose Run.
4. Or run `./gradlew testDebugUnitTest lintDebug assembleDebug` (`gradlew.bat` on Windows).
5. The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

Without a computer, open the repository's Actions tab, select a successful
Android build, and download `SmartPantry-APK-and-test-results`. Extract the ZIP
on the phone, open the APK under `outputs/apk/debug`, and permit installation
from the app used to open that APK if Android asks. This is a debug build for
assessment/testing, not a Play Store release. Keep the same build when testing
persistence; uninstalling clears its database.

## Validation and evidence
- `MatcherTest`: 30 focused unit tests of matching and validation.
- `EvidenceInstrumentation`: Android UI/database checks on a fresh test install.
- `scripts/run-evidence.sh`: installs the app and test APK, collects screenshots,
  force-stops/reopens the app, and verifies stored rows using SQLite.
- `docs/PHONE_TEST_PLAN.md`: physical Samsung A23 acceptance checklist.
- `docs/VIDEO_GUIDE.md`: a timed 6:35 demonstration outline for the student's voice.
- `docs/SCREENSHOT_CHECKLIST.md`: the required evidence views.

Do not run the evidence test against a valued real pantry: it resets test data.
There are no third-party runtime dependencies. JUnit is used only in tests.

## Scope
No location, maps, payments, accounts or network permissions. The app does not
assume staples: even water, oil and salt must be present if a recipe requires them.

## Simulated planning exercise
[Simulated one-month development schedule](docs/SIMULATED_DEVELOPMENT_SCHEDULE.md)
models 22 September–22 October 2026 for the fictional student scenario. It is a
planning exercise, separate from the actual commit history and test evidence.
