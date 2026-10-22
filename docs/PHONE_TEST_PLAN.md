# Samsung A23 verification
Run on the actual phone before treating the submission as complete. The development
emulator checks do not prove Samsung-specific behaviour.

1. Install the provided APK. Open Smart Pantry and confirm the empty pantry message.
2. Try saving a blank name, zero quantity and an impossible date. Each must show an error.
3. Add eggs: 2 item; cheddar: 30 g; butter: 5 g. Check the list and Suggested Recipes.
4. Open Soft cheddar scramble. Confirm the exact ingredient quantities and method.
5. Edit eggs to 1 item. The scramble must disappear from suggestions.
6. Edit eggs back to 2. It must reappear.
7. Delete butter, confirm the prompt, and check the recipe disappears again.
8. Add butter again, close the app completely, reopen and confirm all three entries remain.
9. Add potato 0.25 kg and another batch 250 g. Compare with a 500 g requirement in unit tests.
10. Give the eggs yesterday's expiry date; the scramble must disappear.
11. Toggle expiry reminders, reopen Settings and confirm the setting persists.
12. Rotate while editing and check typed values remain. Check the keyboard does not cover Save.
13. Increase the phone's font size and check text, scrolling and bottom navigation.
14. Browse Recipe Collection: its non-matching recipes must be clearly labelled as ideas.
15. Record Android version, results and any issues in the report. Capture genuine phone screenshots.

Use a test pantry rather than private information. Do not uninstall between persistence checks:
uninstalling intentionally removes local data.
