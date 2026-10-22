package za.co.letaya.smartpantry;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.*;

public final class RecipeAdapter extends BaseAdapter {
  private final Context context;
  private final List<Recipe> recipes = new ArrayList<>();

  public RecipeAdapter(Context c) {
    context = c;
  }

  public void replace(List<Recipe> data) {
    recipes.clear();
    recipes.addAll(data);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return recipes.size();
  }

  @Override
  public Recipe getItem(int p) {
    return recipes.get(p);
  }

  @Override
  public long getItemId(int p) {
    return getItem(p).id;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public View getView(int p, View view, ViewGroup parent) {
    LinearLayout row;
    if (view == null) {
      row = Ui.column(context);
      row.setPadding(
          Ui.dp(context, 18), Ui.dp(context, 16), Ui.dp(context, 18), Ui.dp(context, 16));
      row.setBackground(Ui.rounded(context, 0xFFF6E1E5, 24));
      row.addView(Ui.text(context, "", 20, Ui.INK));
      row.addView(Ui.text(context, "", 14, Ui.MUTED));
    } else row = (LinearLayout) view;
    Recipe r = getItem(p);
    ((TextView) row.getChildAt(0)).setText(r.name);
    ((TextView) row.getChildAt(1))
        .setText(
            r.description
                + "\n"
                + r.minutes
                + " min · "
                + r.ingredients.size()
                + " ingredients · 1 serving");
    return row;
  }
}
