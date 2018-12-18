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
    Button uninstallB;
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
        appB1 = (Button) findViewById(R.id.app1);
        appB2 = (Button) findViewById(R.id.app2);
        appB3 = (Button) findViewById(R.id.app3);
        appB4 = (Button) findViewById(R.id.app4);
        uninstallB = (Button) findViewById(R.id.uninstall);
        buttons.add(appB1);
        buttons.add(appB2);
        buttons.add(appB3);
        buttons.add(appB4);
        for (int i = 0; i < apps.length; i++) {
            if (Manager.isInstalled(this.getApplicationContext(), apps[i]) == false) {
                buttons.get(i).setText("Install");
            } else {
                appB1.setText("Play");
            }
        }
        appB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Manager.isInstalled(view.getContext(), apps[0]) == false) {
                    InstallUtility.install(view.getContext(), apps[0], Manager);
                } else {
                    InstallUtility.launch(view.getContext(), apps[0]);
                }
            }
        });
        appB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Manager.isInstalled(view.getContext(), apps[1]) == false) {
                    InstallUtility.install(view.getContext(), apps[1], Manager);
                } else {

                }
            }
        });
        appB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Manager.isInstalled(view.getContext(), apps[2]) == false) {
                    InstallUtility.install(view.getContext(), apps[2], Manager);
                } else {

                }
            }
        });
        appB4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Manager.isInstalled(view.getContext(), apps[3]) == false) {
                    InstallUtility.install(view.getContext(), apps[4], Manager);
                } else {

                }
            }
        });
        uninstallB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
        public void onResume(){
            super.onResume();
            for(int i = 0; i < apps.length ;i++) {
                if (Manager.isInstalled(this.getApplicationContext(), apps[i]) == false) {
                    buttons.get(i).setText("Install");
                } else {
                    buttons.get(i).setText("Play");
                }
            }
        }


}
