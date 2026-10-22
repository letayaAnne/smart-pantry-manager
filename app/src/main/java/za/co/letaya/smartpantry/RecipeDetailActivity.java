package za.co.letaya.smartpantry;
import android.os.Bundle;
import android.widget.*;
import java.time.LocalDate;
public final class RecipeDetailActivity extends BaseActivity {
    @Override public void onCreate(Bundle state){super.onCreate(state);screen("Recipe details","A little inspiration for your next meal.",true);}
    @Override protected void onResume(){super.onResume();long id=getIntent().getLongExtra("recipe_id",-1);
        work(()->{Recipe recipe=db.recipe(id);return recipe==null?null:new Detail(recipe,Matcher.canMake(recipe,db.pantry(),LocalDate.now()));},result->{
            body.removeAllViews();if(result==null){body.addView(Ui.text(this,"This recipe is unavailable.",18,Ui.INK));return;}
            Recipe r=result.recipe;body.addView(Ui.heading(this,r.name));Ui.space(body,10);body.addView(Ui.text(this,r.description,16,Ui.MUTED));Ui.space(body,8);
            body.addView(Ui.text(this,r.minutes+" minutes · 1 serving",14,Ui.GOLD));Ui.space(body,12);
            body.addView(Ui.text(this,result.ready?"Ready to make · all quantities available":"Not ready · one or more ingredients or quantities are missing",16,Ui.ROSE));Ui.space(body,18);
            body.addView(Ui.heading(this,"What you’ll need"));Ui.space(body,10);
            for(Ingredient i:r.ingredients)body.addView(Ui.text(this,"• "+i.amount()+" "+i.name,17,Ui.INK));
            Ui.space(body,20);body.addView(Ui.heading(this,"Make it yours"));Ui.space(body,12);body.addView(Ui.text(this,r.steps,17,Ui.INK));Ui.space(body,16);
            body.addView(Ui.text(this,"Amounts are for one serving. Recipe suggestions are independent: cooking one may use ingredients needed for another. Update your pantry after cooking.",13,Ui.MUTED));
            Ui.space(body,16);Button back=Ui.button(this,"Back to recipes",false);back.setOnClickListener(v->finish());body.addView(back);
        });
    }
    private static final class Detail{final Recipe recipe;final boolean ready;Detail(Recipe r,boolean ready){recipe=r;this.ready=ready;}}
}
