package com.hacklodge.seattle.appsampler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;
import java.util.*;
import android.widget.ImageView;


public class Platter extends AppCompatActivity {
    InstalledAppsManager Manager;
    Button appB1;
    Button appB2;
    Button appB3;
    Button appB4;
    AppHolder[] apps;
//    AppHolder a1 = new AppHolder("com.supercell.brawlstars","BrawlStar" , null);
//    AppHolder a3 = new AppHolder("com.lemonjamstudio.infiniteknights","InfiniteKnight" , null);
//    AppHolder a2 = new AppHolder("com.mochibits.wordtoword.google","wordtoword" , null);
//    AppHolder a4 = new AppHolder("om.pinestreetcodeworks.TinyBubbles","TinnyBubbles" , null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platter);
        Manager = new InstalledAppsManager(this.getApplicationContext());
        apps = Manager.getPlatter();
//        ImageView appIMG1 = (ImageView) findViewById(R.id.appIMG1);
//        apps[0].loadIcon(appIMG1);
        ArrayList<Button> buttons= new ArrayList<Button>();
        appB1 = (Button)findViewById(R.id.app1);
        appB2 = (Button)findViewById(R.id.app2);
        appB3 = (Button)findViewById(R.id.app3);
        appB4 = (Button)findViewById(R.id.app4);
        buttons.add(appB1);
        buttons.add(appB2);
        buttons.add(appB3);
        buttons.add(appB4);
        for(int i = 0; i < apps.length; i++) {
            if (Manager.isInstalled( this.getApplicationContext(),apps[i]) == false) {
                buttons.get(i).setText("Install");
            } else {
                buttons.get(i).setText("Play");
            }
        }
        appB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Manager.isInstalled(view.getContext(), apps[0]) == false) {
                    InstallUtility.install(view.getContext() , apps[0], Manager);
                    appB1.setText("Play");
                }else {
                    InstallUtility.launch(view.getContext(), apps[0]);
                }
            }
        });
        appB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.isInstalled(view.getContext(),apps[1]) == false) {
                    InstallUtility.install(view.getContext(), apps[1], Manager);
                    appB2.setText("Play");
                }else{

                }
            }
        });
        appB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.isInstalled(view.getContext(),apps[2]) == false) {
                    InstallUtility.install(view.getContext(), apps[2], Manager);
                    appB3.setText("Play");
                }else{

                }
            }
        });
        appB4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.isInstalled(view.getContext(),apps[3]) == false) {
                    InstallUtility.install(view.getContext(), apps[4], Manager);
                    appB4.setText("Play");
                }else{

                }
            }
        });
    }
}
