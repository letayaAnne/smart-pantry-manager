package za.co.letaya.smartpantry;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Immutable pantry batch or recipe requirement. Null expiry means not recorded. */
public final class Ingredient {
    public final long id;
    public final String name, unit;
    public final BigDecimal quantity;
    public final LocalDate expiry;
    public Ingredient(long id, String name, BigDecimal quantity, String unit, LocalDate expiry) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Enter an ingredient name.");
        if (name.trim().length() > 60) throw new IllegalArgumentException("Use a name of 60 characters or fewer.");
        if (quantity == null || quantity.signum() <= 0 || quantity.compareTo(new BigDecimal("1000000")) > 0)
            throw new IllegalArgumentException("Quantity must be greater than 0 and no more than 1,000,000.");
        if (quantity.stripTrailingZeros().scale() > 3) throw new IllegalArgumentException("Use at most 3 decimal places.");
        if (!Matcher.UNITS.containsKey(unit)) throw new IllegalArgumentException("Select a supported unit.");
        this.id=id; this.name=name.trim(); this.quantity=quantity; this.unit=unit; this.expiry=expiry;
    }
    public boolean expired(LocalDate today) { return expiry != null && expiry.isBefore(today); }
    public String amount() { return quantity.stripTrailingZeros().toPlainString()+" "+unit; }
}
