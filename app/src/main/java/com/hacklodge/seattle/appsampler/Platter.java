package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.Group;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
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
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;

import java.util.logging.Handler;


public class Platter extends AppCompatActivity {
    InstalledAppsManager Manager;

    Button cycleB;
    ArrayList<ButtonHolder> favoriteButton;
    SubMenu favoritesMenu;
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
        setContentView(R.layout.container);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(android.R.drawable.btn_star_big_on);

//        handler = new Handler();
        Manager = new InstalledAppsManager(this.getApplicationContext());

        updateFavorites();

        apps = Manager.getPlatter();
        ImageView appIMG1 = (ImageView) findViewById(R.id.appIMG1);
        apps[0].loadIcon(appIMG1);
        ImageView appIMG2 = (ImageView) findViewById(R.id.appIMG2);
        apps[1].loadIcon(appIMG2);
        ImageView appIMG3 = (ImageView) findViewById(R.id.appIMG3);
        apps[2].loadIcon(appIMG3);
        ImageView appIMG4 = (ImageView) findViewById(R.id.appIMG4);
        apps[3].loadIcon(appIMG4);

        appTexts = new  ArrayList<TextView>();
        appTexts.add((TextView) findViewById(R.id.appTextView1));
        appTexts.add((TextView) findViewById(R.id.appTextView2));
        appTexts.add((TextView) findViewById(R.id.appTextView3));
        appTexts.add((TextView) findViewById(R.id.appTextView4));

        ImageButton cycleB = (ImageButton) findViewById(R.id.cycleB);
////        ImageView cycleP = (ImageView) findViewById(R.id.cycleP);
//        Picasso.get().load(R.drawable.cycle).into(cycleB);


        buttons = new ArrayList<>();
        buttons.add((Button) findViewById(R.id.app1));
        buttons.add((Button) findViewById(R.id.app2));
        buttons.add((Button) findViewById(R.id.app3));
        buttons.add((Button) findViewById(R.id.app4));

        favoriteButton = new ArrayList<ButtonHolder>();
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB1),false,apps[0]));
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB2),false,apps[1]));
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB3),false,apps[2]));
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB4),false,apps[3]));

        for(int i = 0; i < favoriteButton.size(); i++){
            final int index = i;
            favoriteButton.get(i).getImageButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ButtonHolder btn = favoriteButton.get(index);
                    AppHolder app = btn.getAppHolder();

                    if (Manager.isFavorite(app)) {
                        System.out.println("printing off");
                        btn.setOn(view.getContext(), false);

                        Manager.removeFavorite(view.getContext(), btn.getAppHolder());
                        updateFavorites();
                    } else {
                        if (Manager.isInstalled(app)) {
                            System.out.println("printing on");
                            btn.setOn(view.getContext(), true);

                            Manager.addFavorite(view.getContext(), app);
                            updateFavorites();
                        }
                    }
                }
            });
        }

        for (int i = 0; i < apps.length; i++) {
            appTexts.get(i).setText(apps[i].getAppName());
            fixText(appTexts.get(i));
            updateButton(i);
        }

        for(int i = 0; i < apps.length; i++){
            final int index = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (! Manager.isInstalled(apps[index])) {
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

                    animate(containers[index], 1, 500, new Callback() {
                        @Override
                        public void function() {
                            apps[index].loadIcon(views[index]);
                            updateButton(index);
                            appTexts.get(index).setText(apps[index].getAppName());
                            fixText(appTexts.get(index));
                            updateFavButton();
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
            fixText(appTexts.get(i));
            updateButton(i);
        }

        Manager.update(this);
        updateFavorites();
        updateFavButton();
    }

    private void updateFavorites() {
        final NavigationView nav = findViewById(R.id.nav_view);
        Menu menu = nav.getMenu();

        if (favoritesMenu != null) menu.removeItem(favoritesMenu.getItem().getItemId());

        favoritesMenu = menu.addSubMenu("Favorites");

        final AppHolder[] favorites = Manager.getFavorites();
        for (AppHolder app : favorites) {
            favoritesMenu.add(app.getAppName());
        }

        final Context c = this;

        nav.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
            @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    String appName = (String) menuItem.getTitle();
                    for (AppHolder app : favorites) {
                        if (app.getAppName().equals(appName)) {
                            Manager.removeFavorite(c, app);
                            updateFavorites();
                            return true;
                        }
                    }
                    return false;
                }
            });

        nav.invalidate();
    }

    private void fixText(final TextView text) {
        text.post(new Runnable() {
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

    private void updateButton(int num) {
        Manager.updateInstalled(this, apps[num]);

        if (! Manager.isInstalled(apps[num])) {
            buttons.get(num).setText("Install");
            buttons.get(num).setBackgroundColor(Color.CYAN);
        } else {
            buttons.get(num).setText("Play");
            buttons.get(num).setBackgroundColor(Color.GREEN);
        }
    }
    private void updateFavButton(){
        for(int i = 0; i < favoriteButton.size(); i++) {
            boolean fav = Manager.isFavorite(favoriteButton.get(i).getAppHolder());
            favoriteButton.get(i).setOn(this, fav);
        }
    }
    private void animate(final View viewToAnim, float scaleAmount, long time, final Callback callback) {
        final ScaleAnimation grow = new ScaleAnimation(1-scaleAmount, 1f, 1-scaleAmount, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation shrink = new ScaleAnimation(1f, 1-scaleAmount, 1f, 1-scaleAmount,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        grow.setDuration(time);
        shrink.setDuration(time);

        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (callback != null) {
                    callback.function();
                }
                viewToAnim.setAnimation(grow);
                grow.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        viewToAnim.startAnimation(shrink);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * callback class for animation that will be called mid-animation
     */
    private abstract class Callback {
        public abstract void function();
    }
}
