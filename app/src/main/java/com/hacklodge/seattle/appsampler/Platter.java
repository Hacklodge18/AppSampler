package com.hacklodge.seattle.appsampler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.net.Uri;


public class Platter extends AppCompatActivity {
    InstalledAppsManager Manager;
    AppHolder a1 = new AppHolder("com.supercell.brawlstars","BrawlStar" , null);
    AppHolder a3 = new AppHolder("com.lemonjamstudio.infiniteknights","InfiniteKnight" , null);
    AppHolder a2 = new AppHolder("com.mochibits.wordtoword.google","wordtoword" , null);
    AppHolder a4 = new AppHolder("om.pinestreetcodeworks.TinyBubbles","TinnyBubbles" , null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platter);
        Manager = new InstalledAppsManager(this.getApplicationContext());
        Button appB1 = (Button)findViewById(R.id.app1);
        appB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Manager.isInstalled(a1) == false) {
//                    InstallUtility.install(view.getContext() , app1, Manager);
                }else {
                }
            }
        });
//        Button appB2 = (Button)findViewById(R.id.app2);
//        appB2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InstallUtility.install(view.getContext() , a2 , Manager);
//            }
//        });
//        Button appB3 = (Button)findViewById(R.id.app3);
//        appB3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InstallUtility.install(view.getContext() , a3 , Manager);
//            }
//        });
//        Button appB4 = (Button)findViewById(R.id.app4);
//        appB4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InstallUtility.install(view.getContext() , a4 , Manager);
//            }
//        });
    }
}
