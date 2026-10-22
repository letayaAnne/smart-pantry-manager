# Smart Pantry Manager
A Java Android application for Mobile App Development 700, personalised for Le'taya.
Cream, blush and gold styling; offline pantry management and strict recipe matching.

## Development status
Work started 22 October 2026 with AI assistance. Local commits record actual steps;
no dates or authorship are fabricated. GitHub publication is pending repository creation.
The brief's requirement to use GitHub from the start is not yet met.

## Database choice
SQLiteOpenHelper: local relational storage, no account or internet required, and a direct
connection to the module's SQLite teaching. SharedPreferences stores one UI preference.

## Build
Use JDK 17, Android SDK 35 and Gradle 8.13. Open this folder in Android Studio,
allow Gradle sync, select an Android 8.0+ device, and Run. Command-line instructions
and tested results will be added as implementation proceeds.

## Scope
No location, maps, payments, accounts or network permissions. The app does not
assume staples: even water, oil and salt must be present if a recipe requires them.
