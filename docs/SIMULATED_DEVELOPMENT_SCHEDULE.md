# Simulated development schedule — Smart Pantry Manager

**Status: fictional planning exercise, not a record of work performed on these dates.**

**Modelled period:** 22 September–22 October 2026 (one calendar month).  
**Scenario:** Le’taya, a 21-year-old final-year BSc IT student at Richfield, develops a Java Android pantry application.  
**Prepared:** 22 October 2026. Dates below are proposed allocations within the simulation; they are not commit dates, test execution dates or evidence of earlier development.

## Purpose and scope

This schedule shows how the existing project's development stages could reasonably be distributed across a month alongside study commitments. It is a retrospective planning model, not a reconstructed diary. It assumes short work sessions with time for review and fixes, rather than full-time work every day. No hours or progress percentages are claimed as measured.

The scenario connects Le’taya’s interest in family cooking and sustainable living with a practical goal: suggest meals only when the pantry contains every required ingredient in sufficient quantity. Familiar foods such as eggs, potatoes, noodles, cheddar, feta and bread guide the recipe selection. Cream, blush, gold accents and curved controls guide the interface.

The model covers five Android screens, local SQLite storage, a custom list adapter, explicit Intent navigation, input validation, 20 recipes and strict matching. The 23 October submission deadline falls outside the modelled development period.

## Proposed month-long sequence — all dates simulated

| Simulated dates (2026) | Proposed work | Intended output and review gate |
| --- | --- | --- |
| 22–24 September | Read the brief and study guide; map requirements to screens and evidence. Define the food-waste motivation and keep the scope manageable. | A requirements checklist covering five screens, CRUD, matching, persistence, report and video. Review every requirement before implementation. |
| 25–27 September | Design the screen flow and relational data model. Choose Java and SQLite for an offline app. Plan ingredient names, quantities, units and optional expiry dates. | A screen-flow sketch and three-table schema: pantry items, recipes and recipe ingredients. Check keys, relationships and validation rules. |
| 28–30 September | Create the Android project structure. Implement ingredient and recipe models and isolate matching rules from the Android interface. | Model classes and a matcher that checks all required quantities. Review missing ingredients, incompatible units and duplicate batches. |
| 1–3 October | Write focused unit tests. Refine canonical names, explicit aliases, decimal arithmetic and supported unit conversions. | A regression suite covering complete and incomplete matches, quantity boundaries, aliases, expiry and invalid input. Fix failures before integrating the interface. |
| 4–6 October | Curate 20 recipes with explicit quantities and methods. Implement SQLite creation, recipe seeding and pantry CRUD. | A seeded database and parameterised CRUD operations. Check that seeding happens once and that pantry entries persist. |
| 7–10 October | Build the cream, blush and gold interface, reusable controls and custom pantry adapter. Connect the five Activities with explicit Intents. | Navigable pantry, ingredient editor, recipe list, recipe detail and settings screens. Review readability, scrolling and consistent navigation. |
| 11–13 October | Connect the screens to stored data. Add form validation, edit and delete handling, recipe availability and the settings preference. | An integrated CRUD flow, deletion confirmation and clearly separated matching and recipe-browsing modes. Confirm that changes refresh the visible list. |
| 14–16 October | Configure repeatable builds and automated checks. Reserve time for SDK, resource, theme and test-runner compatibility fixes. | A build workflow with unit tests, lint and APK generation. Resolve failures using the recorded diagnostics; do not treat this simulated slot as the actual date of any fix. |
| 17–19 October | Exercise the app on an emulator, capture screen evidence and test force-stop/relaunch persistence. Prepare physical-phone acceptance checks. | Emulator screenshots and reproducible verification procedures. Physical Samsung A23 results would require a separate, real test session. |
| 20–21 October | Draft the report, diagrams, code explanations and references. Prepare a timed demonstration outline and review the package structure. | A report draft and 5–7-minute video plan. Check that claims match evidence and that explanations can be delivered in the student's own words. |
| 22 October | Reserve a final review session for phone testing, corrections, recording and packaging. Check the cover, declaration, file naming and total ZIP size. | A readiness checklist. These activities are planned within the simulation, not asserted to be complete; actual outstanding items are listed below. |

## Dependencies and review decisions

- Agree on quantities and units before writing the matcher or entering recipe data. A pantry measured in items must not be silently converted to mass.
- Establish strict matching tests before connecting suggestions to the UI. A missing staple must prevent a recipe from qualifying.
- Confirm database creation and CRUD before testing screen refresh and restart behaviour.
- Capture report screenshots after the UI and test data are stable. Keep the APK, source version and test evidence traceable to one verified build.
- Leave the final sessions available for fixes. Recording the demonstration depends on understanding the code and being able to run the app; writing a video guide is not completion of the recording.

If a review gate fails, the model assigns the next available session to the fix and reduces optional polish. Required functionality, evidence and a working demonstration take priority over extra features. Features such as household synchronisation or automatic ingredient deduction remain outside this scope.

## Actual evidence, separate from the simulation

The available local development records begin on **22 October 2026**. The original local snapshots were subsequently imported into GitHub with source hashes in their commit messages. This schedule does not extend that history into September or change any timestamp.

The existing source provides concrete examples for the simulated phases:

| Development area | Actual repository reference |
| --- | --- |
| Ingredient modelling and strict matching | `app/src/main/java/za/co/letaya/smartpantry/Ingredient.java` and `Matcher.java` |
| Unit tests | `app/src/test/java/za/co/letaya/smartpantry/MatcherTest.java` |
| Database and recipe data | `app/src/main/java/za/co/letaya/smartpantry/PantryDatabase.java` and `app/src/main/assets/` |
| Android screens and adapter | `app/src/main/java/za/co/letaya/smartpantry/` |
| Build and emulator verification | `.github/workflows/android.yml`, `scripts/run-evidence.sh` and `app/src/androidTest/` |
| Original local history | [Original commit listing](ORIGINAL_LOCAL_COMMITS.txt), `original-development.bundle` and [development history](../DEVELOPMENT_HISTORY.md) |

The previously verified [GitHub Actions run 39805812776](https://github.com/letayaAnne/smart-pantry-manager/actions/runs/39805812776), for source commit `619e744b77c59ef31b269a43be9276b8a26bc090`, completed on 22 October 2026. Its evidence records 30 passing unit tests, 31 passing emulator integration checks and a separate successful database-persistence check after force-stop/relaunch. Those results belong to that run, not to the simulated dates above.

At the time this schedule was prepared, physical Samsung A23 testing, the student's narrated video, the completed cover and declaration, and final submission assembly still required confirmation. They are not marked complete by this document.

## How to use this exercise

Use this as a **simulated project-planning appendix** or a discussion of how the work could be scheduled. Retain its title and status notice when copying or exporting it. Keep it separate from the factual report and actual development log; it is not evidence that the project was developed over a month. The project's assistance acknowledgement remains applicable.
