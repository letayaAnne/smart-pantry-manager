package za.co.letaya.smartpantry;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Runs only on an isolated test installation. Captures real rendered Android screens. */
public final class EvidenceInstrumentation extends Instrumentation {
  private PantryDatabase db;
  private int checks = 0;

  @Override
  public void onCreate(Bundle args) {
    super.onCreate(args);
    start();
  }

  private void check(boolean condition, String message) {
    if (!condition) throw new AssertionError(message);
    checks++;
  }

  private Activity open(Class<?> type, long id) {
    Intent i = new Intent(getTargetContext(), type).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    if (type == EditIngredientActivity.class) i.putExtra("ingredient_id", id);
    if (type == RecipeDetailActivity.class) i.putExtra("recipe_id", id);
    Activity a = startActivitySync(i);
    waitForIdleSync();
    pause(650);
    return a;
  }

  private void pause(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  private View find(View v, String text) {
    if (v instanceof TextView && ((TextView) v).getText().toString().equals(text)) return v;
    if (v instanceof ViewGroup) {
      ViewGroup g = (ViewGroup) v;
      for (int i = 0; i < g.getChildCount(); i++) {
        View found = find(g.getChildAt(i), text);
        if (found != null) return found;
      }
    }
    return null;
  }

  private void click(Activity a, String text) {
    runOnMainSync(
        () -> {
          View v = find(a.getWindow().getDecorView(), text);
          if (v == null) throw new AssertionError("Button missing: " + text);
          v.performClick();
        });
    waitForIdleSync();
    pause(600);
  }

  private void fill(Activity a, String name, String amount, String unit) {
    runOnMainSync(
        () -> {
          ((EditText) a.findViewById(R.id.ingredient_name)).setText(name);
          ((EditText) a.findViewById(R.id.ingredient_quantity)).setText(amount);
          ((Spinner) a.findViewById(R.id.ingredient_unit))
              .setSelection(new ArrayList<>(Matcher.UNITS.keySet()).indexOf(unit));
        });
  }

  private void screenshot(String name) {
    waitForIdleSync();
    pause(500);
    Bitmap bitmap = getUiAutomation().takeScreenshot();
    if (bitmap == null) throw new AssertionError("Screenshot unavailable");
    File dir = new File(getTargetContext().getFilesDir(), "evidence");
    dir.mkdirs();
    try (FileOutputStream out = new FileOutputStream(new File(dir, name + ".png"))) {
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    bitmap.recycle();
  }

  private void waitCount(int count) {
    for (int n = 0; n < 50; n++) {
      if (db.pantry().size() == count) return;
      pause(100);
    }
    throw new AssertionError("Pantry count did not become " + count);
  }

  private long id(String name) {
    for (Ingredient i : db.pantry()) if (i.name.equals(name)) return i.id;
    throw new AssertionError("Missing " + name);
  }

  private Recipe scramble() {
    for (Recipe r : db.recipes()) if (r.name.equals("Soft cheddar scramble")) return r;
    throw new AssertionError("Missing seed");
  }

  private boolean ready() {
    return Matcher.canMake(scramble(), db.pantry(), LocalDate.now());
  }

  @Override
  public void onStart() {
    Bundle result = new Bundle();
    try {
      db = PantryDatabase.get(getTargetContext());
      for (Ingredient i : db.pantry()) db.delete(i.id);
      check(db.recipes().size() == 20, "Twenty recipes seeded");
      for (Recipe r : db.recipes())
        check(!r.ingredients.isEmpty() && !r.steps.isEmpty(), "Complete seeded recipe");
      open(PantryActivity.class, 0);
      screenshot("01_empty_pantry");
      Activity edit = open(EditIngredientActivity.class, 0);
      click(edit, "Save ingredient");
      screenshot("02_validation_name");
      fill(edit, "eggs", "0", "item");
      click(edit, "Save ingredient");
      screenshot("03_validation_quantity");
      fill(edit, "eggs", "2", "item");
      screenshot("04_add_ingredient");
      click(edit, "Save ingredient");
      waitCount(1);
      check(db.pantry().get(0).quantity.compareTo(new BigDecimal("2")) == 0, "Create through form");
      edit = open(EditIngredientActivity.class, 0);
      fill(edit, "cheddar", "30", "g");
      click(edit, "Save ingredient");
      waitCount(2);
      edit = open(EditIngredientActivity.class, 0);
      fill(edit, "butter", "5", "g");
      click(edit, "Save ingredient");
      waitCount(3);
      check(ready(), "Complete pantry matches");
      open(PantryActivity.class, 0);
      screenshot("05_pantry_list");
      open(RecipesActivity.class, 0);
      screenshot("06_strict_suggestions");
      open(RecipeDetailActivity.class, scramble().id);
      screenshot("07_recipe_detail");
      edit = open(EditIngredientActivity.class, id("eggs"));
      fill(edit, "eggs", "1", "item");
      screenshot("08_edit_ingredient");
      click(edit, "Save ingredient");
      pause(400);
      check(!ready(), "Insufficient quantity excluded");
      open(RecipesActivity.class, 0);
      screenshot("09_no_match");
      edit = open(EditIngredientActivity.class, id("eggs"));
      fill(edit, "eggs", "2", "item");
      click(edit, "Save ingredient");
      pause(400);
      check(ready(), "Update restores match");
      Activity settings = open(SettingsActivity.class, 0);
      screenshot("10_settings");
      runOnMainSync(
          () -> {
            View view =
                find(settings.getWindow().getDecorView(), "Highlight ingredients expiring soon");
            ((Switch) view).setChecked(false);
          });
      check(
          !getTargetContext().getSharedPreferences("settings", 0).getBoolean("expiry_alerts", true),
          "Setting persisted");
      edit = open(EditIngredientActivity.class, id("butter"));
      click(edit, "Delete ingredient");
      screenshot("11_delete_confirmation");
      android.view.accessibility.AccessibilityNodeInfo window = getUiAutomation().getRootInActiveWindow();
      boolean clicked = false;
      for (android.view.accessibility.AccessibilityNodeInfo node : window.findAccessibilityNodeInfosByText("Delete")) {
        if ("Delete".contentEquals(node.getText()) && node.isClickable()) {
          clicked = node.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK);
          break;
        }
      }
      check(clicked, "Delete confirmation clicked through UI");
      waitCount(2);
      check(!ready(), "Deleting required ingredient excludes recipe");
      check(db.pantry().size() == 2, "Delete retains other rows");
      open(PantryActivity.class, 0);
      screenshot("12_after_delete");
      db.save(new Ingredient(0, "butter", new BigDecimal("5"), "g", null));
      long eggId = id("eggs");
      db.save(
          new Ingredient(eggId, "eggs", new BigDecimal("2"), "item", LocalDate.now().minusDays(1)));
      check(!ready(), "Expired batch excluded end-to-end");
      open(PantryActivity.class, 0);
      screenshot("13_expired_batch");
      db.save(new Ingredient(eggId, "eggs", new BigDecimal("2"), "item", null));
      check(ready(), "Fresh batch restores match");
      Activity collection = open(RecipesActivity.class, 0);
      click(collection, "Browse recipe collection");
      screenshot("14_recipe_collection");
      open(PantryActivity.class, 0);
      result.putString(
          "stream",
          "PASS: "
              + checks
              + " integration checks. Screenshots in target files/evidence. Pantry ends with eggs,"
              + " cheddar and butter.\n");
      finish(Activity.RESULT_OK, result);
    } catch (Throwable t) {
      result.putString("stream", "FAIL after " + checks + " checks: " + t + "\n");
      android.util.Log.e("PantryEvidence", "Test failed", t);
      finish(Activity.RESULT_CANCELED, result);
    }
  }
}
