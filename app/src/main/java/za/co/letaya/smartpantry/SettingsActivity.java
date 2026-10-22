package za.co.letaya.smartpantry;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

public final class SettingsActivity extends BaseActivity {
  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    screen("Small habits, less waste", "A pantry that works gently around your day.", true);
    body.addView(Ui.heading(this, "Your preferences"));
    Ui.space(body, 16);
    SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
    Switch alerts = new Switch(this);
    alerts.setText("Highlight ingredients expiring soon");
    alerts.setTextColor(Ui.INK);
    alerts.setTextSize(16);
    alerts.setMinHeight(Ui.dp(this, 56));
    alerts.setChecked(prefs.getBoolean("expiry_alerts", true));
    alerts.setOnCheckedChangeListener(
        (button, checked) -> prefs.edit().putBoolean("expiry_alerts", checked).apply());
    body.addView(alerts);
    body.addView(
        Ui.text(
            this,
            "Shows an in-app reminder for dates within the next 3 days. It does not send"
                + " notifications. Expired ingredients are always excluded from matching.",
            14,
            Ui.MUTED));
    Ui.space(body, 24);
    body.addView(Ui.heading(this, "Made with intention"));
    Ui.space(body, 12);
    body.addView(
        Ui.text(
            this,
            "Inspired by Le’taya’s love of cooking for family, exploring different cuisines and"
                + " making the most of what is already in the kitchen. Every ingredient has value.",
            17,
            Ui.INK));
    Ui.space(body, 20);
    body.addView(Ui.heading(this, "Simple, honest matching"));
    Ui.space(body, 12);
    body.addView(
        Ui.text(
            this,
            "No missing ingredients. No assumed staples. Compatible metric units are converted;"
                + " counts and weights stay separate. Cheddar is not treated as feta.\n\n"
                + "Your pantry stays on this device, including after closing the app. Clearing app"
                + " data or uninstalling removes it. No account, location or internet access is"
                + " required.",
            16,
            Ui.MUTED));
    Ui.space(body, 20);
    body.addView(
        Ui.text(
            this, "Smart Pantry Manager · version 1.0\nMobile App Development 700", 12, Ui.GOLD));
  }
}
