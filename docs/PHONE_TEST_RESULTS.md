# Samsung A23 manual test results — 27 October 2026

**Device:** Samsung A23, Android 14, One UI 6.1. This is the working phone selected for the demonstration.

**Evidence source:** results reported by the student in conversation. The assistant did not directly witness these manual tests. No screenshots or recording accompanied this test report.

| Check | Reported result |
| --- | --- |
| Install and open the supplied app | Pass; no errors |
| Save an empty ingredient form | Rejected as expected |
| Save a zero quantity | Rejected as expected |
| Save eggs 2 item, cheddar 30 g and butter 5 g | Pass |
| Soft cheddar scramble appears with sufficient ingredients | Pass |
| Reduce eggs to 1 item | Scramble disappears from strict suggestions |
| Restore eggs to 2 item | Scramble returns |
| Delete butter | Scramble disappears |
| Close and reopen the app | Ingredients remain |
| Layout and interaction during these checks | No overlapping text, crashes or confusing controls reported |

The student was instructed to close the app from recent apps before reopening. This is not described as a device reboot or a Settings force-stop test.

## Next steps

Keep this working installation for the demonstration. Capture phone screenshots and record the required 5–7-minute video in the student's own voice, showing the live app and explaining the relevant code.

These results cover the core pantry and matching flows. Expiry boundaries, settings persistence, rotation, enlarged text and other unreported cases in PHONE_TEST_PLAN.md remain unconfirmed on this physical device. Do not mark those checks complete without testing them.

The report should distinguish these student-reported physical-device results from automated emulator evidence. The completed cover/declaration, video and final submission package remain separate deliverables.
