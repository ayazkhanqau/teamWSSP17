package com.zeeroapps.wssp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zeeroapps.wssp.fragments.MethodFragment;
import com.zeeroapps.wssp.fragments.MyComplaintsFragment;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.fragments.ViewPagerFragment;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FragmentManager fragmentManager;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton btnMenu;
    LinearLayout btnNewComp, btnMyComps, btnCall1334, btnMethod, btnFeedback;
    TextView btnLogout;
    SharedPreferences sp;
    SharedPreferences.Editor spEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        fragmentManager = getSupportFragmentManager();

        sp = this.getSharedPreferences(getString(R.string.sp), this.MODE_PRIVATE);
        spEdit = sp.edit();

        Intent intent = getIntent();
        if (intent.getExtras() != null){
             if (intent.getExtras().getBoolean("CALLED_FROM_THANK_YOU_ACTIVITY")){
                 fragmentManager.beginTransaction().replace(R.id.container, MyComplaintsFragment.newInstance()).commit();
             }
        }else {
            fragmentManager.beginTransaction().replace(R.id.container, ViewPagerFragment.newInstance()).commit();
        }
        initUIComponents();

    }

    void initUIComponents(){
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hv = navigationView.getHeaderView(0);
        btnNewComp = (LinearLayout) hv.findViewById(R.id.llNewComplaint);
        btnMyComps = (LinearLayout) hv.findViewById(R.id.llMyComplaints);
        btnCall1334 = (LinearLayout) hv.findViewById(R.id.llCall1334);
        btnMethod = (LinearLayout) hv.findViewById(R.id.llMethod);
//        btnFeedback = (LinearLayout) hv.findViewById(R.id.llFeedback);
        btnLogout = (TextView) hv.findViewById(R.id.tvLogout);

        btnMenu.setOnClickListener(this);
        btnNewComp.setOnClickListener(this);
        btnMyComps.setOnClickListener(this);
        btnCall1334.setOnClickListener(this);
        btnMethod.setOnClickListener(this);
//        btnFeedback.setOnClickListener(this);
        btnLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnMenu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.llNewComplaint:
                changeFragment(ViewPagerFragment.newInstance());
                break;
            case R.id.llMyComplaints:
                changeFragment(MyComplaintsFragment.newInstance());
                break;
            case R.id.llCall1334:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel://1334")));
                break;
            case R.id.llMethod:
                changeFragment(MethodFragment.newInstance());
                break;
            case R.id.tvLogout:
                logoutUser();
                break;
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void logoutUser() {
        spEdit.putString(getString(R.string.spUMobile), null);
        spEdit.putString(getString(R.string.spUPass), null);
        spEdit.commit();
        startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
        finish();
    }

    void changeFragment(Fragment fr){
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fr).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .commit();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}