# Six-and-a-half-minute demonstration guide

Use your own voice and wording. Rehearse first; do not claim you wrote unaided code or carried out tests you did not perform. Explain the actual local development/import history. The final video must be 5–7 minutes and inside the submission ZIP, not just a link.

## Before recording
- Open the public GitHub repository and its commit history in Chrome.
- Install the APK and check it opens on the Samsung A23.
- Start with an empty test pantry (delete previous test batches individually).
- Have these amounts ready: eggs 2 item; cheddar 30 g; butter 5 g.
- Keep Java source pages open in Chrome: Matcher.java, PantryDatabase.java and PantryActivity.java / PantryAdapter.java.
- Enable microphone recording. A silent recording is not acceptable. Face camera is optional.

## 0:00–0:55 — introduction and GitHub
Introduce yourself as Le’taya, a final-year BSc IT student at Richfield. Explain that the app is inspired by cooking for family, valuing ingredients and reducing avoidable waste. Show the repository and actual commits. Explain that work began locally with AI assistance, then the existing development steps were imported; show DEVELOPMENT_HISTORY.md. Do not portray upload times as days of development.

## 0:55–3:10 — live application
Show the empty pantry and a validation error from trying to save an empty form. Add eggs, cheddar and butter with the amounts above. Show the pantry list, then Suggested Recipes and Soft cheddar scramble. Open its full ingredient list and preparation method.

Edit eggs to 1 item. Return to suggestions: the scramble must disappear. Restore 2 item and show it returns. Delete butter using the confirmation dialog and show that the recipe disappears again. Add butter again. Close the app from recent apps and reopen; show the stored pantry entries. Briefly show the Settings switch and the clearly separate recipe collection.

## 3:10–5:40 — explain three concepts with source visible
1. Strict matching (about 55 seconds): point to Matcher.canMake. Explain canonical names, compatible-unit conversion, sums for pantry batches and requirements, and the final comparison. Give the example of 0.5 kg equalling 500 g; explain why one potato cannot automatically equal 200 g. Missing or insufficient ingredients return false. Expired batches are ignored.
2. Database (about 50 seconds): show PantryDatabase.onCreate, save and delete. Explain three tables, the recipe_id foreign key, one-time recipe seeding and parameterised ID selection. Explain why data survives closing the app but not uninstalling it. Note that the background worker avoids database work on the UI thread.
3. Lists and lifecycle (about 45 seconds): show PantryActivity.onResume and PantryAdapter.getView. Explain how returning to the screen reloads database rows, replace calls notifyDataSetChanged, and recycled rows display the current values. Mention that each edit Intent passes the stable database ID.

## 5:40–6:20 — database justification
Explain that SQLite keeps the app usable offline without account setup or a server. It follows the study guide’s persistent-data approach. A relational model fits recipes and their ingredients. SharedPreferences stores the small reminder preference. A future household-sharing feature would need a separate synchronisation design.

## 6:20–6:35 — reflection
Describe one thing you personally learned while reviewing/testing the project and one improvement you would make. Possible improvement: an atomic “I cooked this” operation to deduct ingredients. Only describe phone tests you have actually completed.

## Export and handoff
Export MP4/H.264 with readable text and clear narration. Aim for about 6:30. For the 50 MB total ZIP limit, a 650 kbps video stream plus 64 kbps audio uses approximately 35 MB for 6:30, before container overhead. Check actual quality and final ZIP size; do not assume this bitrate will make every screen readable. Send the recorded video here so it can be included with the report and source. Do not submit this guide as a substitute for the video.
