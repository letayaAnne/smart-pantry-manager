package za.co.letaya.smartpantry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Pure Java business logic: no database or Android dependency. */
public final class Matcher {
    private Matcher() { }
    public static final class Unit {
        final String dimension; final BigDecimal factor;
        Unit(String dimension, String factor) { this.dimension=dimension; this.factor=new BigDecimal(factor); }
    }
    public static final Map<String,Unit> UNITS;
    private static final Map<String,String> ALIASES;
    static {
        Map<String,Unit> u=new LinkedHashMap<>();
        u.put("g",new Unit("mass","1")); u.put("kg",new Unit("mass","1000"));
        u.put("ml",new Unit("volume","1")); u.put("l",new Unit("volume","1000"));
        u.put("tsp",new Unit("volume","5")); u.put("tbsp",new Unit("volume","15"));
        u.put("item",new Unit("count","1")); u.put("slice",new Unit("slice","1"));
        UNITS=Collections.unmodifiableMap(u);
        Map<String,String> a=new HashMap<>();
        alias(a,"egg","eggs"); alias(a,"potato","potatoes"); alias(a,"tomato","tomatoes");
        alias(a,"onion","onions"); alias(a,"carrot","carrots"); alias(a,"pepper","peppers","bell pepper","bell peppers","capsicum");
        alias(a,"noodles","noodle","plain noodles","dry noodles");
        alias(a,"cheddar","cheddar cheese"); alias(a,"feta","feta cheese");
        alias(a,"bread","sliced bread"); alias(a,"tea bag","tea bags","teabag","teabags");
        alias(a,"oil","cooking oil","vegetable oil","sunflower oil");
        alias(a,"spinach","spinach leaves"); alias(a,"garlic","garlic cloves","garlic clove");
        ALIASES=Collections.unmodifiableMap(a);
    }
    private static void alias(Map<String,String> a,String canonical,String... alternatives) {
        a.put(canonical,canonical); for(String name:alternatives) a.put(name,canonical);
    }
    public static String canonical(String name) {
        String n=name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+"," ");
        return ALIASES.getOrDefault(n,n);
    }
    private static String key(Ingredient i) { return canonical(i.name)+"|"+UNITS.get(i.unit).dimension; }
    private static void add(Map<String,BigDecimal> totals,Ingredient i) {
        BigDecimal value=i.quantity.multiply(UNITS.get(i.unit).factor);
        totals.merge(key(i),value,BigDecimal::add);
    }
    public static boolean canMake(Recipe recipe,List<Ingredient> pantry,LocalDate today) {
        // Empty recipes never qualify. Expired stock cannot make a recipe eligible.
        if(recipe.ingredients.isEmpty()) return false;
        Map<String,BigDecimal> available=new HashMap<>(), required=new HashMap<>();
        for(Ingredient i:pantry) if(!i.expired(today)) add(available,i);
        for(Ingredient i:recipe.ingredients) add(required,i);
        for(Map.Entry<String,BigDecimal> e:required.entrySet())
            if(available.getOrDefault(e.getKey(),BigDecimal.ZERO).compareTo(e.getValue())<0) return false;
        return true;
    }
    public static List<Recipe> suggestions(List<Recipe> recipes,List<Ingredient> pantry,LocalDate today) {
        List<Recipe> result=new ArrayList<>();
        for(Recipe r:recipes) if(canMake(r,pantry,today)) result.add(r);
        return result;
    }
}
