package za.co.letaya.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

public final class PantryActivity extends BaseActivity {
  private PantryAdapter adapter;
  private TextView summary, empty;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    screen("A little goes a long way", "Keep what you have. Cook something lovely.", false);
    Button add = Ui.button(this, "+ Add ingredient", true);
    add.setOnClickListener(v -> startActivity(new Intent(this, EditIngredientActivity.class)));
    body.addView(add);
    Ui.space(body, 10);
    summary = Ui.text(this, "Loading your pantry…", 13, Ui.MUTED);
    body.addView(summary);
    Ui.space(body, 10);
    empty =
        Ui.text(
            this,
            "Your pantry is ready for a fresh start.\n"
                + "Add the ingredients you already have.\n\n"
                + "Try eggs, cheddar and butter for a soft cheddar scramble.",
            17,
            Ui.MUTED);
    body.addView(empty);
    ListView list = new ListView(this);
    list.setDividerHeight(Ui.dp(this, 9));
    list.setDivider(new android.graphics.drawable.ColorDrawable(Ui.CREAM));
    adapter = new PantryAdapter(this);
    list.setAdapter(adapter);
    list.setEmptyView(empty);
    body.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
    list.setOnItemClickListener(
        (parent, view, position, id) ->
            startActivity(
                new Intent(this, EditIngredientActivity.class).putExtra("ingredient_id", id)));
  }

  @Override
  protected void onResume() {
    super.onResume();
    work(
        () -> db.pantry(),
        items -> {
          adapter.replace(
              items,
              getSharedPreferences("settings", MODE_PRIVATE).getBoolean("expiry_alerts", true));
          summary.setText(items.size() + " pantry batches · tap an item to edit");
        });
  }
}
