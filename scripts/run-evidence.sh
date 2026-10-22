#!/usr/bin/env bash
set -euo pipefail
mkdir -p evidence
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w za.co.letaya.smartpantry.test/za.co.letaya.smartpantry.EvidenceInstrumentation | tee evidence/integration.txt
adb exec-out run-as za.co.letaya.smartpantry tar -cf - files/evidence > evidence/screenshots.tar || true
grep -q 'PASS:' evidence/integration.txt
adb shell am force-stop za.co.letaya.smartpantry
adb shell am start -W -n za.co.letaya.smartpantry/.PantryActivity
sleep 2
adb exec-out screencap -p > evidence/15_reopened.png
adb exec-out run-as za.co.letaya.smartpantry cat databases/smart_pantry.db > evidence/reopened.db
python3 - <<'CHECK'
import sqlite3
with sqlite3.connect('evidence/reopened.db') as db:
    rows=db.execute('SELECT name,quantity,unit FROM pantry_item ORDER BY name').fetchall()
    assert rows == [('butter','5','g'),('cheddar','30','g'),('eggs','2','item')],rows
    assert db.execute('SELECT count(*) FROM recipe').fetchone()[0]==20
print('PASS: pantry and recipes survive force-stop and relaunch')
CHECK
