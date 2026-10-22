package za.co.letaya.smartpantry;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public final class EditIngredientActivity extends BaseActivity {
  private EditText name, quantity, expiry;
  private Spinner unit;
  private long itemId;
  private Button save, delete;
  private final List<String> units = new ArrayList<>(Matcher.UNITS.keySet());

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    itemId = getIntent().getLongExtra("ingredient_id", 0);
    screen(
        itemId == 0 ? "Add a little goodness" : "Edit your ingredient",
        "Record each batch as you find it in your kitchen.",
        true);
    name = Ui.field(body, "Ingredient name", "e.g. eggs, potato or cheddar", 1001);
    name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    quantity = Ui.field(body, "Quantity", "e.g. 2 or 0.5", 1002);
    quantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    body.addView(Ui.text(this, "Unit", 14, Ui.INK));
    unit = new Spinner(this);
    unit.setId(1003);
    unit.setContentDescription("Ingredient unit");
    ArrayAdapter<String> choices =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
    choices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    unit.setAdapter(choices);
    unit.setMinimumHeight(Ui.dp(this, 52));
    body.addView(unit);
    Ui.space(body, 12);
    expiry = Ui.field(body, "Expiry date (optional)", "YYYY-MM-DD", 1004);
    expiry.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
    body.addView(
        Ui.text(
            this,
            "Use grams for vegetables and cheese; items for eggs and garlic cloves; slices for"
                + " bread. 1 tsp = 5 ml; 1 tbsp = 15 ml. Expired batches stay visible but never"
                + " qualify for recipes.",
            13,
            Ui.MUTED));
    Ui.space(body, 16);
    save = Ui.button(this, "Save ingredient", true);
    save.setOnClickListener(v -> save());
    body.addView(save);
    Ui.space(body, 10);
    if (itemId != 0) {
      delete = Ui.button(this, "Delete ingredient", false);
      delete.setOnClickListener(v -> confirmDelete());
      body.addView(delete);
    }
    Button cancel = Ui.button(this, "Cancel", false);
    cancel.setOnClickListener(v -> finish());
    Ui.space(body, 10);
    body.addView(cancel);
    if (itemId != 0 && state == null) {
      save.setEnabled(false);
      work(
          () -> db.ingredient(itemId),
          item -> {
            if (item == null) {
              toast("This ingredient no longer exists.");
              finish();
              return;
            }
            name.setText(item.name);
            quantity.setText(item.quantity.stripTrailingZeros().toPlainString());
            unit.setSelection(units.indexOf(item.unit));
            expiry.setText(item.expiry == null ? "" : item.expiry.toString());
            save.setEnabled(true);
          },
          () -> save.setEnabled(true));
    }
  }

  private void busy(boolean value) {
    save.setEnabled(!value);
    if (delete != null) delete.setEnabled(!value);
  }

  private void save() {
    name.setError(null);
    quantity.setError(null);
    expiry.setError(null);
    String n = name.getText().toString().trim(),
        q = quantity.getText().toString().trim(),
        e = expiry.getText().toString().trim();
    if (n.isEmpty() || n.length() > 60) {
      name.setError("Enter an ingredient name, up to 60 characters.");
      name.requestFocus();
      return;
    }
    BigDecimal amount;
    try {
      if (!q.matches("[0-9]+(?:[.,][0-9]{1,3})?")) throw new NumberFormatException();
      amount = new BigDecimal(q.replace(',', '.'));
    } catch (NumberFormatException ex) {
      quantity.setError("Enter a positive number with up to 3 decimal places.");
      quantity.requestFocus();
      return;
    }
    LocalDate date = null;
    try {
      if (!e.isEmpty()) {
        if (!e.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"))
          throw new DateTimeParseException("format", e, 0);
        date = LocalDate.parse(e);
      }
    } catch (DateTimeParseException ex) {
      expiry.setError("Enter a real date as YYYY-MM-DD.");
      expiry.requestFocus();
      return;
    }
    Ingredient item;
    try {
      item = new Ingredient(itemId, n, amount, (String) unit.getSelectedItem(), date);
    } catch (IllegalArgumentException ex) {
      quantity.setError(ex.getMessage());
      quantity.requestFocus();
      return;
    }
    busy(true);
    work(
        () -> db.save(item),
        id -> {
          toast(itemId == 0 ? "Ingredient added" : "Ingredient updated");
          finish();
        },
        () -> busy(false));
  }

  private void confirmDelete() {
    new AlertDialog.Builder(this)
        .setTitle("Remove this ingredient?")
        .setMessage("This removes only this pantry batch. Other batches are kept.")
        .setNegativeButton("Keep it", null)
        .setPositiveButton(
            "Delete",
            (dialog, which) -> {
              busy(true);
              work(
                  () -> {
                    db.delete(itemId);
                    return true;
                  },
                  ok -> {
                    toast("Ingredient deleted");
                    finish();
                  },
                  () -> busy(false));
            })
        .show();
  }
}
