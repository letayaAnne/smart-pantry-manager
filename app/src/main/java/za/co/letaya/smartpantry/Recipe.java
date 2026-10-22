package za.co.letaya.smartpantry;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
public final class Recipe {
    public final long id;
    public final String name, description, steps;
    public final int minutes;
    public final List<Ingredient> ingredients;
    public Recipe(long id, String name, String description, int minutes, String steps, List<Ingredient> ingredients) {
        this.id=id; this.name=name; this.description=description; this.minutes=minutes; this.steps=steps;
        this.ingredients=Collections.unmodifiableList(new ArrayList<>(ingredients));
    }
}
