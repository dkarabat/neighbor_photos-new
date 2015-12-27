package info.fandroid.navdrawer;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import info.fandroid.navdrawer.fragments.FragmentGallery;
import info.fandroid.navdrawer.fragments.FragmentMap;
import info.fandroid.navdrawer.fragments.FragmentSend;
import info.fandroid.navdrawer.fragments.FragmentShare;
import info.fandroid.navdrawer.fragments.FragmentSlideshow;
import info.fandroid.navdrawer.fragments.FragmentTools;
import info.fandroid.navdrawer.util.GPSTracker;
import info.fandroid.navdrawer.util.LocationParams;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentMap fimport;
    FragmentGallery fgallery;
    FragmentSend fsend;
    FragmentShare fshare;
    FragmentSlideshow fshow;
    FragmentTools ftools;
    FragmentManager fragmentManager;
    protected GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gps = new GPSTracker(MainActivity.this);
        setLocation();
//        Bundle bundle = new Bundle();
//        bundle.putDouble("latitude", latitude);
//        bundle.putDouble("longitude", longitude);
// set Fragmentclass Arguments
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fgallery = new FragmentGallery();
        fimport = new FragmentMap();
        fsend = new FragmentSend();
        fshare = new FragmentShare();
        fshow = new FragmentSlideshow();
        ftools = new FragmentTools();


//        fimport.setArguments(bundle);
    }

    private void setLocation() {
        if (gps.canGetLocation()) {
            ((LocationParams) getApplication()).setLatitude(gps.getLatitude());
            ((LocationParams) getApplication()).setLongitude(gps.getLongitude());
            Log.i("latitude {}", String.valueOf(gps.getLatitude()));
            Log.i("longitude {}", String.valueOf(gps.getLongitude()));
//            double altitude = gps.getAltitude();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragmentManager = getFragmentManager();
        FragmentTransaction ftrans = fragmentManager.beginTransaction();

        if (id == R.id.nav_camara) {
            ftrans.replace(R.id.container, fimport);
        } else if (id == R.id.nav_gallery) {
            ftrans.replace(R.id.container, fgallery);

        } else if (id == R.id.nav_slideshow) {
            ftrans.replace(R.id.container, fshow);

        } else if (id == R.id.nav_manage) {
            ftrans.replace(R.id.container, ftools);

        } else if (id == R.id.nav_share) {
            ftrans.replace(R.id.container, fshare);

        } else if (id == R.id.nav_send) {
            ftrans.replace(R.id.container, fsend);

        }
        ftrans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
