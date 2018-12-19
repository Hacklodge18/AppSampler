package com.hacklodge.seattle.appsampler;
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
    public void setOn(boolean a){
        on = a;
    }
    public ImageButton getImageButton(){
        return b;
    }
    public AppHolder getAppHolder(){
        return app;
    }

}
