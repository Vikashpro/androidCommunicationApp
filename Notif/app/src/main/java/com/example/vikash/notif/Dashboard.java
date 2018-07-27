package com.example.vikash.notif;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.public_notice_board.view.FragmentPublicNoticeBoard;
import com.example.vikash.notif.updates.view.DashboardFragment;
import com.example.vikash.notif.updates.view.DatesheetFragment;
import com.example.vikash.notif.conversations.view.ConversationFragment;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.view.LoginActivity;
import com.example.vikash.notif.loginDirectory.view.ProfilePicUploadFragment;
import com.example.vikash.notif.notifications.helper.CircleTransform;
import com.example.vikash.notif.notifications.view.NotificationFragment;


public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;
    private static final String SERVER_PATH = "Path_to_your_server";
    TextView textViewName, textViewEmail;
    ImageView profilePicImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        View headerView = navigationView.getHeaderView(0);
        textViewName = (TextView) headerView.findViewById(R.id.textViewName);
        textViewEmail = (TextView) headerView.findViewById(R.id.textViewEmail);
        textViewName.setText(SharedPrefManager.getInstance(this).getUser().getName());
        textViewEmail.setText(SharedPrefManager.getInstance(this).getUser().getEmail());
        profilePicImageView = (ImageView) headerView.findViewById(R.id.profilePicImageView);
        //loading home fragment by default
        Fragment fragment = null;
        fragment = new DashboardFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        String imageUrl = APIUrl.BASE_URL + SharedPrefManager.getInstance(this).getUser().getImage();
        System.out.println("Shared Pref! " + imageUrl);
        Glide.with(profilePicImageView.getContext()).load(imageUrl)
                .thumbnail(0.5f)
                .crossFade()
                .transform(new CircleTransform(profilePicImageView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profilePicImageView);

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

        getMenuInflater().inflate(R.menu.dashboard, menu);
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
        }else if(id == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_conversation) {
            // Handle the camera action
            fragment = new ConversationFragment();
        } else if (id == R.id.nav_notification) {
            fragment = new NotificationFragment();

        } else if (id == R.id.nav_update_profile) {
            fragment = new ProfilePicUploadFragment();
        } else if (id == R.id.nav_updates) {
            fragment = new DatesheetFragment();
        } else if (id == R.id.nav_public_notice) {
        fragment = new FragmentPublicNoticeBoard();
        } else if (id == R.id.nav_send) {

        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPrefManager.getInstance(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }


}

