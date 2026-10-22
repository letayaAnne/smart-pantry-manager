package za.co.letaya.smartpantry;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.junit.Test;

public class MatcherTest {
  private final LocalDate today = LocalDate.of(2026, 10, 22);

  private Ingredient i(String name, String amount, String unit) {
    return new Ingredient(0, name, new BigDecimal(amount), unit, null);
  }

  private Recipe r(Ingredient... requirements) {
    return new Recipe(1, "Test", "", 1, "", Arrays.asList(requirements));
  }

  private boolean match(Recipe r, Ingredient... stock) {
    return Matcher.canMake(r, Arrays.asList(stock), today);
  }

  @Test
  public void emptyPantryFails() {
    assertFalse(match(r(i("egg", "2", "item"))));
  }

  @Test
  public void emptyRecipeFails() {
    assertFalse(match(r(), i("egg", "2", "item")));
  }

  @Test
  public void exactQuantityPasses() {
    assertTrue(match(r(i("egg", "2", "item")), i("egg", "2", "item")));
  }

  @Test
  public void extraQuantityPasses() {
    assertTrue(match(r(i("egg", "2", "item")), i("egg", "3", "item")));
  }

  @Test
  public void insufficientQuantityFails() {
    assertFalse(match(r(i("egg", "2", "item")), i("egg", "1.999", "item")));
  }

  @Test
  public void missingOneOfFiveFails() {
    assertFalse(
        match(
            r(
                i("egg", "1", "item"),
                i("milk", "50", "ml"),
                i("bread", "1", "slice"),
                i("butter", "5", "g"),
                i("salt", "1", "g")),
            i("egg", "1", "item"),
            i("milk", "50", "ml"),
            i("bread", "1", "slice"),
            i("butter", "5", "g")));
  }

  @Test
  public void aliasesCaseAndWhitespacePass() {
    assertTrue(match(r(i("tomato", "200", "g")), i("  TOMATOES  ", "200", "g")));
  }

  @Test
  public void internalWhitespacePasses() {
    assertTrue(match(r(i("cheddar", "30", "g")), i("Cheddar   Cheese", "30", "g")));
  }

  @Test
  public void kilogramsConvert() {
    assertTrue(match(r(i("potato", "500", "g")), i("potatoes", "0.5", "kg")));
  }

  @Test
  public void litresConvert() {
    assertTrue(match(r(i("milk", "250", "ml")), i("milk", "0.25", "l")));
  }

  @Test
  public void tablespoonsConvert() {
    assertTrue(match(r(i("oil", "15", "ml")), i("oil", "1", "tbsp")));
  }

  @Test
  public void teaspoonsConvert() {
    assertTrue(match(r(i("oil", "15", "ml")), i("oil", "3", "tsp")));
  }

  @Test
  public void massCannotBecomeVolume() {
    assertFalse(match(r(i("milk", "100", "ml")), i("milk", "100", "g")));
  }

  @Test
  public void countCannotBecomeMass() {
    assertFalse(match(r(i("potato", "200", "g")), i("potato", "2", "item")));
  }

  @Test
  public void slicesRemainSeparate() {
    assertFalse(match(r(i("bread", "2", "slice")), i("bread", "2", "item")));
  }

  @Test
  public void batchesAggregate() {
    assertTrue(
        match(r(i("potato", "500", "g")), i("potato", "250", "g"), i("potatoes", "0.25", "kg")));
  }

  @Test
  public void decimalAdditionIsExact() {
    assertTrue(match(r(i("milk", "0.3", "ml")), i("milk", "0.1", "ml"), i("milk", "0.2", "ml")));
  }

  @Test
  public void duplicateRequirementsAggregate() {
    assertFalse(match(r(i("egg", "2", "item"), i("eggs", "2", "item")), i("egg", "3", "item")));
  }

  @Test
  public void expiredBatchExcluded() {
    assertFalse(
        match(
            r(i("egg", "2", "item")),
            new Ingredient(1, "egg", new BigDecimal("2"), "item", today.minusDays(1))));
  }

  @Test
  public void todayExpiryIncluded() {
    assertTrue(
        match(
            r(i("egg", "2", "item")),
            new Ingredient(1, "egg", new BigDecimal("2"), "item", today)));
  }

  @Test
  public void expiredAndFreshNotCombined() {
    assertFalse(
        match(
            r(i("egg", "2", "item")),
            i("egg", "1", "item"),
            new Ingredient(1, "egg", BigDecimal.ONE, "item", today.minusDays(1))));
  }

  @Test
  public void cheeseTypesStayDistinct() {
    assertFalse(match(r(i("cheddar", "30", "g")), i("feta", "30", "g")));
  }

  @Test
  public void staplesNotAssumed() {
    assertFalse(match(r(i("egg", "2", "item"), i("oil", "5", "ml")), i("egg", "2", "item")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void zeroRejected() {
    i("egg", "0", "item");
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeRejected() {
    i("egg", "-1", "item");
  }

  @Test(expected = IllegalArgumentException.class)
  public void blankNameRejected() {
    i("   ", "1", "item");
  }

  @Test(expected = IllegalArgumentException.class)
  public void unsupportedUnitRejected() {
    i("egg", "1", "cup");
  }

  @Test(expected = IllegalArgumentException.class)
  public void precisionRejected() {
    i("egg", "0.0001", "item");
  }

  @Test(expected = IllegalArgumentException.class)
  public void hugeQuantityRejected() {
    i("egg", "1000001", "item");
  }

  @Test
  public void suggestionsExcludePartialRecipes() {
    List<Recipe> result =
        Matcher.suggestions(
            Arrays.asList(r(i("egg", "1", "item")), r(i("egg", "1", "item"), i("oil", "5", "ml"))),
            Arrays.asList(i("egg", "1", "item")),
            today);
    assertEquals(1, result.size());
  }
}
