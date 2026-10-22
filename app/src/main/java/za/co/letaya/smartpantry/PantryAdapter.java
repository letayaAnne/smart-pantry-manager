package za.co.letaya.smartpantry;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.time.LocalDate;
import java.util.*;

/** Custom adapter reuses row views; stable IDs refer to SQLite primary keys. */
public final class PantryAdapter extends BaseAdapter {
  private final Context context;
  private final List<Ingredient> items = new ArrayList<>();
  private boolean alerts;

  public PantryAdapter(Context context) {
    this.context = context;
  }

  public void replace(List<Ingredient> data, boolean alerts) {
    items.clear();
    items.addAll(data);
    this.alerts = alerts;
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public Ingredient getItem(int position) {
    return items.get(position);
  }

  @Override
  public long getItemId(int position) {
    return getItem(position).id;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public View getView(int position, View recycled, ViewGroup parent) {
    LinearLayout row;
    TextView name, details;
    if (recycled == null) {
      row = Ui.column(context);
      row.setPadding(
          Ui.dp(context, 18), Ui.dp(context, 15), Ui.dp(context, 18), Ui.dp(context, 15));
      row.setBackground(Ui.rounded(context, 0xFFFFFDF9, 22));
      name = Ui.text(context, "", 19, Ui.INK);
      details = Ui.text(context, "", 14, Ui.MUTED);
      row.addView(name);
      row.addView(details);
    } else {
      row = (LinearLayout) recycled;
      name = (TextView) row.getChildAt(0);
      details = (TextView) row.getChildAt(1);
    }
    Ingredient item = getItem(position);
    name.setText(item.name);
    String info = item.amount();
    if (item.expiry != null) {
      info += "  •  " + item.expiry;
      if (item.expired(LocalDate.now())) info += "\nExpired · excluded from recipes";
      else if (alerts && !item.expiry.isAfter(LocalDate.now().plusDays(3)))
        info += "\nUse soon · within 3 days";
    }
    details.setText(info + "\nTap to edit or delete");
    return row;
  }
}
