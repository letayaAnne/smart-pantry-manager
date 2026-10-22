package za.co.letaya.smartpantry;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import org.json.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/** A process-wide helper; activities must use it through BaseActivity's worker. */
public final class PantryDatabase extends SQLiteOpenHelper {
    private static PantryDatabase instance;
    private final Context context;
    public static synchronized PantryDatabase get(Context context) {
        if(instance==null) instance=new PantryDatabase(context.getApplicationContext());
        return instance;
    }
    private PantryDatabase(Context context) { super(context,"smart_pantry.db",null,1); this.context=context; }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_item (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, quantity TEXT NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipe (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, description TEXT NOT NULL, minutes INTEGER NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredient (_id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL REFERENCES recipe(_id) ON DELETE CASCADE, name TEXT NOT NULL, quantity TEXT NOT NULL, unit TEXT NOT NULL)");
        db.execSQL("CREATE INDEX ingredients_recipe ON recipe_ingredient(recipe_id)");
        // SQLiteOpenHelper already wraps onCreate in a transaction: all seeds succeed or none do.
        try(InputStream input=context.getAssets().open("recipes.json")) {
            ByteArrayOutputStream bytes=new ByteArrayOutputStream(); byte[] buffer=new byte[4096]; int count;
            while((count=input.read(buffer))!=-1) bytes.write(buffer,0,count);
            JSONArray recipes=new JSONArray(bytes.toString(StandardCharsets.UTF_8.name()));
            for(int n=0;n<recipes.length();n++) {
                JSONObject r=recipes.getJSONObject(n); ContentValues values=new ContentValues();
                values.put("name",r.getString("name")); values.put("description",r.getString("description")); values.put("minutes",r.getInt("minutes"));
                JSONArray steps=r.getJSONArray("steps"); StringBuilder method=new StringBuilder();
                for(int s=0;s<steps.length();s++) method.append(s+1).append(". ").append(steps.getString(s)).append("\n\n");
                values.put("steps",method.toString().trim()); long id=db.insertOrThrow("recipe",null,values);
                JSONArray ingredients=r.getJSONArray("ingredients");
                for(int k=0;k<ingredients.length();k++) {
                    JSONObject i=ingredients.getJSONObject(k); ContentValues v=new ContentValues();
                    // Validate seeds using the same model as user entries.
                    Ingredient valid=new Ingredient(0,i.getString("name"),new BigDecimal(i.getString("quantity")),i.getString("unit"),null);
                    v.put("recipe_id",id); v.put("name",valid.name); v.put("quantity",valid.quantity.toPlainString()); v.put("unit",valid.unit);
                    db.insertOrThrow("recipe_ingredient",null,v);
                }
            }
        } catch(IOException|JSONException e) { throw new IllegalStateException("Could not initialise recipe collection",e); }
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        // Version 1 has no upgrade path yet. Never silently delete a user's pantry.
        throw new IllegalStateException("A data-preserving migration is required.");
    }
    public List<Ingredient> pantry() {
        List<Ingredient> items=new ArrayList<>();
        try(Cursor c=getReadableDatabase().query("pantry_item",null,null,null,null,null,"CASE WHEN expiry IS NULL THEN 1 ELSE 0 END, expiry, name COLLATE NOCASE, _id")) {
            while(c.moveToNext()) items.add(readIngredient(c,true));
        } return items;
    }
    private Ingredient readIngredient(Cursor c,boolean hasExpiry) {
        String expiry=hasExpiry?c.getString(c.getColumnIndexOrThrow("expiry")):null;
        return new Ingredient(c.getLong(c.getColumnIndexOrThrow("_id")),c.getString(c.getColumnIndexOrThrow("name")),new BigDecimal(c.getString(c.getColumnIndexOrThrow("quantity"))),c.getString(c.getColumnIndexOrThrow("unit")),expiry==null?null:LocalDate.parse(expiry));
    }
    public Ingredient ingredient(long id) {
        try(Cursor c=getReadableDatabase().query("pantry_item",null,"_id=?",new String[]{Long.toString(id)},null,null,null)) { return c.moveToFirst()?readIngredient(c,true):null; }
    }
    public long save(Ingredient ingredient) {
        ContentValues v=new ContentValues(); v.put("name",ingredient.name); v.put("quantity",ingredient.quantity.toPlainString()); v.put("unit",ingredient.unit);
        if(ingredient.expiry==null) v.putNull("expiry"); else v.put("expiry",ingredient.expiry.toString());
        if(ingredient.id==0) return getWritableDatabase().insertOrThrow("pantry_item",null,v);
        if(getWritableDatabase().update("pantry_item",v,"_id=?",new String[]{Long.toString(ingredient.id)})!=1) throw new IllegalStateException("Ingredient no longer exists.");
        return ingredient.id;
    }
    public void delete(long id) {
        if(getWritableDatabase().delete("pantry_item","_id=?",new String[]{Long.toString(id)})!=1) throw new IllegalStateException("Ingredient no longer exists.");
    }
    public List<Recipe> recipes() {
        List<Recipe> recipes=new ArrayList<>();
        try(Cursor c=getReadableDatabase().query("recipe",null,null,null,null,null,"name COLLATE NOCASE")) {
            while(c.moveToNext()) {
                long id=c.getLong(c.getColumnIndexOrThrow("_id")); List<Ingredient> ingredients=new ArrayList<>();
                try(Cursor rows=getReadableDatabase().query("recipe_ingredient",null,"recipe_id=?",new String[]{Long.toString(id)},null,null,"_id")) { while(rows.moveToNext()) ingredients.add(readIngredient(rows,false)); }
                recipes.add(new Recipe(id,c.getString(c.getColumnIndexOrThrow("name")),c.getString(c.getColumnIndexOrThrow("description")),c.getInt(c.getColumnIndexOrThrow("minutes")),c.getString(c.getColumnIndexOrThrow("steps")),ingredients));
            }
        } return recipes;
    }
    public Recipe recipe(long id) { for(Recipe r:recipes()) if(r.id==id) return r; return null; }
}
