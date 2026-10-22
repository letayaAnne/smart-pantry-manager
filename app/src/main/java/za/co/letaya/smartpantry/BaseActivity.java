package za.co.letaya.smartpantry;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/** Serial DB worker and shared navigation. Late results never update destroyed Activities. */
public abstract class BaseActivity extends Activity {
  private static final ExecutorService IO = Executors.newSingleThreadExecutor();
  protected LinearLayout root, body;
  protected PantryDatabase db;

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);
    db = PantryDatabase.get(this);
  }

  protected void screen(String title, String subtitle, boolean scroll) {
    root = Ui.column(this);
    root.setBackgroundColor(Ui.CREAM);
    root.setOnApplyWindowInsetsListener(
        (v, insets) -> {
          v.setPadding(
              insets.getSystemWindowInsetLeft(),
              insets.getSystemWindowInsetTop(),
              insets.getSystemWindowInsetRight(),
              insets.getSystemWindowInsetBottom());
          return insets.consumeSystemWindowInsets();
        });
    setContentView(root);
    root.requestApplyInsets();
    LinearLayout header = Ui.column(this);
    header.setPadding(Ui.dp(this, 24), Ui.dp(this, 8), Ui.dp(this, 24), Ui.dp(this, 12));
    header.addView(new Ui.Flourish(this), new LinearLayout.LayoutParams(-1, Ui.dp(this, 42)));
    header.addView(Ui.text(this, "SMART PANTRY  /  by Le’taya", 11, Ui.GOLD));
    Ui.space(header, 6);
    header.addView(Ui.heading(this, title));
    Ui.space(header, 5);
    header.addView(Ui.text(this, subtitle, 14, Ui.MUTED));
    root.addView(header);
    body = Ui.column(this);
    body.setPadding(Ui.dp(this, 20), 0, Ui.dp(this, 20), Ui.dp(this, 10));
    if (scroll) {
      ScrollView view = new ScrollView(this);
      view.setFillViewport(true);
      view.addView(body);
      root.addView(view, new LinearLayout.LayoutParams(-1, 0, 1));
    } else root.addView(body, new LinearLayout.LayoutParams(-1, 0, 1));
    LinearLayout nav = Ui.column(this);
    nav.setPadding(Ui.dp(this, 12), Ui.dp(this, 8), Ui.dp(this, 12), 0);
    Button pantry = Ui.button(this, "Pantry", this instanceof PantryActivity),
        recipes = Ui.button(this, "Recipes", this instanceof RecipesActivity),
        settings = Ui.button(this, "Settings", this instanceof SettingsActivity);
    pantry.setOnClickListener(v -> navigate(PantryActivity.class));
    recipes.setOnClickListener(v -> navigate(RecipesActivity.class));
    settings.setOnClickListener(v -> navigate(SettingsActivity.class));
    Ui.buttonRow(nav, pantry, recipes, settings);
    root.addView(nav);
  }

  protected void navigate(Class<?> destination) {
    if (getClass() == destination) return;
    Intent intent = new Intent(this, destination);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    startActivity(intent);
  }

  protected <T> void work(Callable<T> task, Consumer<T> success) {
    work(task, success, () -> {});
  }

  protected <T> void work(Callable<T> task, Consumer<T> success, Runnable failure) {
    IO.execute(
        () -> {
          try {
            T result = task.call();
            runOnUiThread(
                () -> {
                  if (!isFinishing() && !isDestroyed()) success.accept(result);
                });
          } catch (Exception e) {
            android.util.Log.e("SmartPantry", "Operation failed", e);
            runOnUiThread(
                () -> {
                  if (!isFinishing() && !isDestroyed()) {
                    failure.run();
                    new AlertDialog.Builder(this)
                        .setTitle("Please try again")
                        .setMessage(
                            "That change could not be completed. Your saved pantry has not been"
                                + " cleared.")
                        .setPositiveButton("OK", null)
                        .show();
                  }
                });
          }
        });
  }

  protected void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
