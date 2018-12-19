package com.hacklodge.seattle.appsampler;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import java.util.*;
import android.widget.ImageView;
import android.widget.ImageButton;

import com.squareup.picasso.Picasso;

import java.util.logging.Handler;


public class Platter extends AppCompatActivity {
    InstalledAppsManager Manager;
    Button appB1;
    Button appB2;
    Button appB3;
    Button appB4;
    Button cycleB;
    Handler handler;
    ArrayList<TextView> appTexts;
    AppHolder[] apps;
    ArrayList<Button> buttons;
//    AppHolder a1 = new AppHolder("com.supercell.brawlstars","BrawlStar" , null);
//    AppHolder a3 = new AppHolder("com.lemonjamstudio.infiniteknights","InfiniteKnight" , null);
//    AppHolder a2 = new AppHolder("com.mochibits.wordtoword.google","wordtoword" , null);
//    AppHolder a4 = new AppHolder("om.pinestreetcodeworks.TinyBubbles","TinnyBubbles" , null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platter);
//        handler = new Handler();
        Manager = new InstalledAppsManager(this.getApplicationContext());
        apps = Manager.getPlatter();
        ImageView appIMG1 = (ImageView) findViewById(R.id.appIMG1);
        apps[0].loadIcon(appIMG1);
        ImageView appIMG2 = (ImageView) findViewById(R.id.appIMG2);
        apps[1].loadIcon(appIMG2);
        ImageView appIMG3 = (ImageView) findViewById(R.id.appIMG3);
        apps[2].loadIcon(appIMG3);
        ImageView appIMG4 = (ImageView) findViewById(R.id.appIMG4);
        apps[3].loadIcon(appIMG4);
        buttons = new ArrayList<Button>();
        appTexts = new  ArrayList<TextView>();
        TextView appTextView1 = (TextView) findViewById(R.id.appTextView1);
        TextView appTextView2 = (TextView) findViewById(R.id.appTextView2);
        TextView appTextView3 = (TextView) findViewById(R.id.appTextView3);
        TextView appTextView4 = (TextView) findViewById(R.id.appTextView4);
//        TextView tester = (TextView) findViewById(R.id.tester);
//        tester.post(new Runnable() {
//            @Override
//            public void run() {
//                String line = "                          ";
//                TextView tester = (TextView) findViewById(R.id.tester);
//                String TextHolder = (String) tester.getText();
//                int lineCount = tester.getLineCount();
//                TextView tester2 = (TextView) findViewById(R.id.tester2);
//                int number = 0;
//                tester2.setText(lineCount+"");
//                for(int i = 3-lineCount; i > 0; i--){
//                    String Holder = (String)tester.getText();
//                    Holder = "\n"+Holder;
//                    tester.setText(Holder);
//                    number++;
//
//                }
//                tester2.setText(number+"");
//
//                // Use lineCount here
//            }
//        });
        appTexts.add(appTextView1);
        appTexts.add(appTextView2);
        appTexts.add(appTextView3);
        appTexts.add(appTextView4);
        ImageButton cycleB = (ImageButton) findViewById(R.id.cycleB);
        ImageView cycleP = (ImageView) findViewById(R.id.cycleP);
        Picasso.get().load(R.drawable.cycle).into(cycleP);
        appB1 = (Button) findViewById(R.id.app1);
        appB2 = (Button) findViewById(R.id.app2);
        appB3 = (Button) findViewById(R.id.app3);
        appB4 = (Button) findViewById(R.id.app4);
        buttons.add(appB1);
        buttons.add(appB2);
        buttons.add(appB3);
        buttons.add(appB4);
        for (int i = 0; i < apps.length; i++) {
            appTexts.get(i).setText(apps[i].getAppName());
            final TextView text = appTexts.get(i);
            appTexts.get(i).post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = text.getLineCount();
                    for(int i = 3-lineCount; i > 0; i--){
                        String Holder = (String)text.getText();
                        Holder = "\n"+Holder;
                        text.setText(Holder);
                    }
                }
            });
            updateButton(i);
        }
        for(int i = 0; i < apps.length; i++){
            final int index = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Manager.isInstalled(view.getContext(), apps[index]) == false) {
                        InstallUtility.install(view.getContext(), apps[index], Manager);
                    } else {
                        InstallUtility.launch(view.getContext(), apps[index]);
                    }
                }
            });
        }
        cycleB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apps = Manager.cycle(view.getContext());
                final ImageView[] views = new ImageView[4];
                views[0] = (ImageView) findViewById(R.id.appIMG1);
                views[1] = (ImageView) findViewById(R.id.appIMG2);
                views[2] = (ImageView) findViewById(R.id.appIMG3);
                views[3] = (ImageView) findViewById(R.id.appIMG4);
                final View[] containers = {findViewById(R.id.element1),findViewById(R.id.element2),
                        findViewById(R.id.element3),findViewById(R.id.element4)};

                for(int i = 0; i < apps.length ;i++) {
                    final int index = i;

                    animate(containers[index], new Callback() {
                            @Override
                            public void function() {
                                apps[index].loadIcon(views[index]);
                                updateButton(index);
                            }
                    });

                    appTexts.get(i).setText(apps[i].getAppName());
                    final TextView text = appTexts.get(i);
                    appTexts.get(i).post(new Runnable() {
                        @Override
                        public void run() {
                            int lineCount = text.getLineCount();
                            for(int i = 3-lineCount; i > 0; i--){
                                String Holder = (String)text.getText();
                                Holder = "\n"+Holder;
                                text.setText(Holder);
                            }
                        }
                    });
                }

                List<AppHolder> appsToUninstall = Manager.shouldBeUninstalled();
                for(int i = 0; i < appsToUninstall.size(); i++){
                    InstallUtility.uninstall(view.getContext(),appsToUninstall.get(i), Manager);
                }
            }
        });

    }
    public void onResume(){
        super.onResume();
        for(int i = 0; i < apps.length ;i++) {
            appTexts.get(i).setText(apps[i].getAppName());
            final TextView text = appTexts.get(i);
            appTexts.get(i).post(new Runnable() {
                @Override
                public void run() {
                    int lineCount = text.getLineCount();
                    for(int i = 3-lineCount; i > 0; i--){
                        String Holder = (String)text.getText();
                        Holder = "\n"+Holder;
                        text.setText(Holder);
                    }
                }
            });
            updateButton(i);
        }
    }

    private void updateButton(int num) {
        if (! Manager.isInstalled(this.getApplicationContext(), apps[num])) {
            buttons.get(num).setText("Install");
            buttons.get(num).setBackgroundColor(Color.CYAN);
        } else {
            appB1.setText("Play");
            buttons.get(num).setBackgroundColor(Color.GREEN);
        }
    }

    private void animate(final View viewToAnim, final Callback callback) {
        final ScaleAnimation grow = new ScaleAnimation(0f, 1f, 0f, 1f);
        final ScaleAnimation shrink = new ScaleAnimation(1f, 0f, 1f, 0f);

        grow.setDuration(500);
        shrink.setDuration(500);

        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                callback.function();
                viewToAnim.setAnimation(grow);
                grow.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        viewToAnim.startAnimation(shrink);
    }

    /**
     * callback class for animation that will be called mid-animation
     */
    private abstract class Callback {
        public abstract void function();
    }
}
