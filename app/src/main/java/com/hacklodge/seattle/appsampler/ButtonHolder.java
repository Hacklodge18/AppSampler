package com.hacklodge.seattle.appsampler;
import android.content.Context;
import android.widget.ImageButton;
public class ButtonHolder {
    private ImageButton b;
    private boolean on;
    private AppHolder app;
    public ButtonHolder(ImageButton b, Boolean on, AppHolder app){
        this.b = b;
        this.on = on;
        this.app = app;
    }
    public boolean getOn(){
        return on;
    }
    public void setOn(Context c, boolean a){
        on = a;

        if (on) {
            getImageButton().setBackground(c.getDrawable(android.R.drawable.btn_star_big_on));
        } else {
            getImageButton().setBackground(c.getDrawable(android.R.drawable.btn_star));
        }
    }
    public ImageButton getImageButton(){
        return b;
    }
    public AppHolder getAppHolder(){
        return app;
    }

}
