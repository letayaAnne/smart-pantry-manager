package za.co.letaya.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.time.LocalDate;
import java.util.*;

public final class RecipesActivity extends BaseActivity {
  private RecipeAdapter adapter;
  private TextView summary;
  private final List<Recipe> all = new ArrayList<>();
  private List<Ingredient> pantry = new ArrayList<>();
  private boolean collection;
  private TextView empty;
  private Button toggle;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    collection = state != null && state.getBoolean("collection");
    screen("Something lovely to cook", "Only complete matches appear in your suggestions.", false);
    summary = Ui.text(this, "Checking your pantry…", 15, Ui.ROSE);
    body.addView(summary);
    Ui.space(body, 10);
    toggle = Ui.button(this, "Browse recipe collection", false);
    toggle.setOnClickListener(
        v -> {
          collection = !collection;
          render();
        });
    body.addView(toggle);
    Ui.space(body, 10);
    empty = Ui.text(this, "", 17, Ui.MUTED);
    body.addView(empty);
    ListView list = new ListView(this);
    list.setDivider(new android.graphics.drawable.ColorDrawable(Ui.CREAM));
    list.setDividerHeight(Ui.dp(this, 10));
    adapter = new RecipeAdapter(this);
    list.setAdapter(adapter);
    list.setEmptyView(empty);
    body.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
    list.setOnItemClickListener(
        (parent, view, p, id) ->
            startActivity(new Intent(this, RecipeDetailActivity.class).putExtra("recipe_id", id)));
  }

  @Override
  protected void onResume() {
    super.onResume();
    work(
        () -> new Snapshot(db.recipes(), db.pantry()),
        result -> {
          all.clear();
          all.addAll(result.recipes);
          pantry = result.pantry;
          render();
        });
  }

  private void render() {
    List<Recipe> visible = collection ? all : Matcher.suggestions(all, pantry, LocalDate.now());
    adapter.replace(visible);
    summary.setText(
        collection
            ? "RECIPE COLLECTION · all "
                + all.size()
                + " recipes\nThese are ideas, not guaranteed pantry matches."
            : "SUGGESTED RECIPES · "
                + visible.size()
                + " ready to make\nEvery ingredient and quantity checked.");
    toggle.setText(collection ? "Return to strict suggestions" : "Browse recipe collection");
    empty.setText(
        collection
            ? "No recipes are available."
            : "No recipes match your pantry yet.\n"
                + "Add the ingredients and quantities you have.\n\n"
                + "For soft cheddar scramble: 2 eggs, 30 g cheddar and 5 g butter.");
  }

  @Override
  protected void onSaveInstanceState(Bundle state) {
    state.putBoolean("collection", collection);
    super.onSaveInstanceState(state);
  }

  private static final class Snapshot {
    final List<Recipe> recipes;
    final List<Ingredient> pantry;

    Snapshot(List<Recipe> r, List<Ingredient> p) {
      recipes = r;
      pantry = p;
    }
  }
}
