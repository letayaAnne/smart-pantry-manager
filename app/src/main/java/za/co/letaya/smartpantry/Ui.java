package za.co.letaya.smartpantry;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.content.res.ColorStateList;
import android.view.*;
import android.widget.*;

/** Small shared view helpers keep the five Activities visually consistent. */
public final class Ui {
    public static final int CREAM=Color.rgb(255,249,240), INK=Color.rgb(66,46,48), ROSE=Color.rgb(140,71,93), BLUSH=Color.rgb(246,225,229), GOLD=Color.rgb(151,115,48), MUTED=Color.rgb(111,91,91);
    private Ui() { }
    public static int dp(Context c,float v) { return (int)(c.getResources().getDisplayMetrics().density*v+0.5f); }
    public static GradientDrawable rounded(Context c,int colour,int radius) {
        GradientDrawable d=new GradientDrawable(); d.setColor(colour); d.setCornerRadius(dp(c,radius)); return d;
    }
    public static TextView text(Context c,String s,int size,int colour) {
        TextView t=new TextView(c); t.setText(s); t.setTextSize(size); t.setTextColor(colour); t.setLineSpacing(dp(c,3),1); return t;
    }
    public static TextView heading(Context c,String s) {
        TextView t=text(c,s,29,INK); t.setTypeface(Typeface.create("serif",Typeface.NORMAL)); return t;
    }
    public static LinearLayout column(Context c) { LinearLayout l=new LinearLayout(c); l.setOrientation(LinearLayout.VERTICAL); return l; }
    public static Button button(Context c,String s,boolean primary) {
        Button b=new Button(c); b.setText(s); b.setAllCaps(false); b.setTextSize(15); b.setTextColor(primary?Color.WHITE:ROSE);
        b.setMinHeight(dp(c,50)); b.setMinimumHeight(dp(c,50)); b.setPadding(dp(c,14),dp(c,8),dp(c,14),dp(c,8));
        b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x228C475D),rounded(c,primary?ROSE:BLUSH,26),null));
        return b;
    }
    public static void space(LinearLayout l,int h) { View v=new View(l.getContext()); l.addView(v,new LinearLayout.LayoutParams(1,dp(l.getContext(),h))); }
    public static void buttonRow(LinearLayout l,Button... buttons) {
        LinearLayout row=new LinearLayout(l.getContext());
        for(Button b:buttons) { LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT,1); params.setMargins(dp(l.getContext(),3),0,dp(l.getContext(),3),0); row.addView(b,params); }
        l.addView(row); space(l,10);
    }
    public static EditText field(LinearLayout l,String label,String hint,int id) {
        TextView caption=text(l.getContext(),label,14,INK); l.addView(caption);
        EditText e=new EditText(l.getContext()); e.setId(id); caption.setLabelFor(id); e.setTextSize(17); e.setTextColor(INK); e.setHintTextColor(MUTED); e.setHint(hint);
        e.setSingleLine(true); e.setMinHeight(dp(l.getContext(),52)); e.setPadding(dp(l.getContext(),16),dp(l.getContext(),10),dp(l.getContext(),16),dp(l.getContext(),10));
        GradientDrawable background=rounded(l.getContext(),Color.WHITE,18); background.setStroke(dp(l.getContext(),1),0xFFE3CBD0); e.setBackground(background);
        l.addView(e,new LinearLayout.LayoutParams(-1,-2)); space(l,14); return e;
    }
    public static final class Flourish extends View {
        private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        public Flourish(Context c) { super(c); setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO); }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas); paint.setColor(BLUSH); paint.setStyle(Paint.Style.FILL);
            canvas.drawOval(getWidth()*0.60f,-getHeight()*0.7f,getWidth()*1.2f,getHeight()*0.9f,paint);
            paint.setStyle(Paint.Style.STROKE); paint.setColor(GOLD); paint.setStrokeWidth(dp(getContext(),1));
            Path path=new Path(); float cx=getWidth()*0.83f,cy=getHeight()*0.4f;
            for(int i=0;i<180;i++) { double a=i*0.045; float r=dp(getContext(),2+i*0.13f); float x=cx+(float)Math.cos(a)*r,y=cy+(float)Math.sin(a)*r; if(i==0)path.moveTo(x,y);else path.lineTo(x,y); }
            canvas.drawPath(path,paint);
        }
    }
}
