package com.hacklodge.seattle.appsampler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import java.util.*;
import android.widget.ImageView;
import android.widget.ImageButton;

import com.downloader.PRDownloader;
import android.widget.ArrayAdapter;
import java.util.logging.Handler;
import android.widget.Spinner;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
public class Platter extends AppCompatActivity {
    InstalledAppsManager Manager;
    private AdView mAdView;
    Button cycleB;
    ArrayList<ButtonHolder> favoriteButton;
    SubMenu favoritesMenu;
    Handler handler;
    ArrayList<TextView> appTexts;
    AppHolder[] apps;
    ArrayList<Button> buttons;
    String[] genre;
    String cGenre;
    Spinner genreDropDown;
//    AppHolder a1 = new AppHolder("com.supercell.brawlstars","BrawlStar" , null);
//    AppHolder a3 = new AppHolder("com.lemonjamstudio.infiniteknights","InfiniteKnight" , null);
//    AppHolder a2 = new AppHolder("com.mochibits.wordtoword.google","wordtoword" , null);
//    AppHolder a4 = new AppHolder("om.pinestreetcodeworks.TinyBubbles","TinnyBubbles" , null);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);
        MobileAds.initialize(this, "ca-app-pub-4157098826653042~2428164120");
        AdView adView = new AdView(this.getApplicationContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        genreDropDown = findViewById(R.id.spinner1);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        String[] genre = {"all","action","arcade","card","casual","music","racing","simulation","strategy","word","adventure","board","puzzle","role-playing","trivia","sports"};
        PRDownloader.initialize(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genre);
        genreDropDown.setAdapter(adapter);
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
        ImageView appIMG2 = (ImageView) findViewById( R.id.appIMG2);
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
            public void onClick(final View view) {
                apps = Manager.cycle(genreDropDown.getSelectedItem().toString());
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
                            appTexts.get(index).setText(apps[index].getAppName());
                            fixText(appTexts.get(index));
                            favoriteButton.get(index).setOn(view.getContext(),
                                    Manager.isInstalled(apps[index]));

                            updateEverything();
                        }
                    });

                }

                List<AppHolder> appsToUninstall = Manager.shouldBeUninstalled();
                for(int i = 0; i < appsToUninstall.size(); i++){
                    InstallUtility.uninstall(view.getContext(),appsToUninstall.get(i), Manager);
                }
            }
        });

        updateEverything();

    }

    public void onResume(){
        super.onResume();

        updateEverything();
    }

    private void updateEverything() {
        //UPDATE APPS
        Manager.update();
        for (AppHolder app : apps) {
            Manager.addInstalled(app);
        }
        apps = Manager.getPlatter();

        //UPDATE INSTALL BUTTONS

        for(int i = 0; i < apps.length; i++){
            final int index = i;
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (! Manager.isInstalled(apps[index])) {
                        InstallUtility.install(view.getContext(), apps[index], Manager);
                    } else {
                        InstallUtility.deleteApk(view.getContext(), apps[index]);
                        InstallUtility.launch(view.getContext(), apps[index]);
                    }
                }
            });
        }
        for (int i = 0; i < apps.length; i++) {
            appTexts.get(i).setText(apps[i].getAppName());
            fixText(appTexts.get(i));
            updateButton(i);
        }

        //UPDATE IMAGES
        ImageView[] views = new ImageView[4];
        views[0] = (ImageView) findViewById(R.id.appIMG1);
        views[1] = (ImageView) findViewById(R.id.appIMG2);
        views[2] = (ImageView) findViewById(R.id.appIMG3);
        views[3] = (ImageView) findViewById(R.id.appIMG4);
        for (int i = 0; i < apps.length; i++) {
            apps[i].loadIcon(views[i]);
        }

        //UPDATE FAVORITES BUTTONS
        favoriteButton = new ArrayList<ButtonHolder>();
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB1),
                Manager.isInstalled(apps[0]) ,apps[0]));
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB2),
                Manager.isInstalled(apps[1]) ,apps[1]));
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB3),
                Manager.isInstalled(apps[2]), apps[2]));
        favoriteButton.add(new ButtonHolder((ImageButton)findViewById(R.id.appFB4),
                Manager.isInstalled(apps[3]),apps[3]));
        updateFavButton();
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

                        Manager.removeFavorite(btn.getAppHolder());
                        updateFavorites();
                    } else {
                        if (Manager.isInstalled(app)) {
                            System.out.println("printing on");
                            btn.setOn(view.getContext(), true);

                            Manager.addFavorite(app);
                            updateFavorites();
                        }
                    }
                }
            });
        }

        updateFavorites();
    }

    private void updateFavorites() {
        final NavigationView nav = findViewById(R.id.nav_view);
        Menu menu = nav.getMenu();

        if (favoritesMenu != null) menu.removeItem(favoritesMenu.getItem().getItemId());

        favoritesMenu = menu.addSubMenu("Favorites");

        final AppHolder[] favorites = Manager.getFavorites();
        for (AppHolder app : favorites) {
            favoritesMenu.add("Play \"" + app.getAppName() + "\"");
            favoritesMenu.add("Delete \"" + app.getAppName() + "\"");
        }

        final Context c = this;

        nav.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
            @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    String action = (String) menuItem.getTitle();
                    action = action.substring(0, action.indexOf('\"') + 1);

                    String appName = (String) menuItem.getTitle();
                    appName = appName.substring(appName.indexOf('\"') + 1, appName.length()-1);
                    for (AppHolder app : favorites) {
                        if (app.getAppName().equals(appName)) {
                            if (action.equals("Play \"")) {
                                InstallUtility.launch(c, app);
                            } else {
                                Manager.removeFavorite(app);
                            }

                            updateEverything();
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
                text.setTextColor(-1);
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
        Manager.updateInstalled(apps[num]);
        if (! Manager.isInstalled(apps[num])) {
            buttons.get(num).setText("Install");
            buttons.get(num).setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            buttons.get(num).setText("Play");
            buttons.get(num).setTextColor(Color.parseColor("#FFFFFF"));
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
                updateEverything();
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
