package com.example.sms.dustinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sms.dustinfo.common.AddLocationDialogFragment;
import com.example.sms.dustinfo.db.LocationRealmObject;
import com.example.sms.dustinfo.finddust.FineDustContract;
import com.example.sms.dustinfo.finddust.FineDustFragment;
import com.example.sms.dustinfo.util.GeoUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_CODE_FINE_COARSE_PERMISSION = 1000;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Pair<Fragment, String>> mFragmentList;
    private Realm mRealm;
    private Toolbar mToolbar;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRealm = Realm.getDefaultInstance();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddLocationDialogFragment.newInstance(new AddLocationDialogFragment.OnClickListener() {
                    @Override
                    public void onOkClicked(final String city) {
                        GeoUtil.getLocationFromName(MainActivity.this, city, new GeoUtil.GeoUtilListener() {
                            @Override
                            public void onSuccess(double lat, double lng) {
                                saveNewCity(lat, lng, city);
                                addNewFragment(lat, lng, city);
                            }

                            @Override
                            public void onError(String message) {
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).show(getSupportFragmentManager(), "dialog");
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
  */
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setUpViewPager();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void saveNewCity(double lat, double lng, String city) {
        mRealm.beginTransaction();
        LocationRealmObject newLocationRealmObject = mRealm.createObject(LocationRealmObject.class);
        newLocationRealmObject.setName(city);
        newLocationRealmObject.setLat(lat);
        newLocationRealmObject.setLng(lng);
        mRealm.commitTransaction();
    }


    private void addNewFragment(double lat, double lng, String city) {
        mFragmentList.add(new Pair<Fragment, String>(
                FineDustFragment.newInstance(lat, lng), city));
        mViewPager.getAdapter().notifyDataSetChanged();
    }


    private void setUpViewPager() {
        mTabLayout =(TabLayout) findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        loadDbData();

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);

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
        if (id == R.id.action_all_delete) {
            mRealm.beginTransaction();
            mRealm.where(LocationRealmObject.class).findAll().deleteAllFromRealm();
            mRealm.commitTransaction();
            setUpViewPager();
            return true;
        } else if (id == R.id.action_delete) {
            if (mTabLayout.getSelectedTabPosition() == 0) {
                Toast.makeText(this, "현재 위치 탭은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return true;
            }
            mRealm.beginTransaction();
            mRealm.where(LocationRealmObject.class).findAll().get(mTabLayout.getSelectedTabPosition() - 1).deleteFromRealm();
            mRealm.commitTransaction();
            setUpViewPager();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;
        /*if (id == R.id.nav_source) {
            intent = IntentUtil.getSourceCodeIntent();
        } else if (id == R.id.nav_book) {
            intent = IntentUtil.getBuyBookIntent();
        } else if (id == R.id.nav_facebook) {
            intent = IntentUtil.getFacebookPageIntent();
        } else if (id == R.id.nav_publisher) {
            intent = IntentUtil.getPublisherIntent();
        } else if (id == R.id.nav_home) {
            intent = IntentUtil.getHomepageIntent();
        } else if (id == R.id.nav_location) {
            intent = IntentUtil.getLocationIntent();
        } else if (id == R.id.nav_phone) {
            intent = IntentUtil.getDialPhoneIntent();
        }*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }


        return true;
    }


    private void loadDbData() {
        RealmResults<LocationRealmObject> realmResults = mRealm.where(LocationRealmObject.class).findAll();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(new Pair<Fragment, String>(new FineDustFragment(), "현재위치"));

        for (LocationRealmObject realmObject : realmResults) {
            mFragmentList.add(new Pair<Fragment, String>(FineDustFragment.newInstance(realmObject.getLat(), realmObject.getLng()), realmObject.getName()));
        }
    }

    public void getLastKnownLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_FINE_COARSE_PERMISSION);
             return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>()
                {
                    @Override
                    public void onSuccess(Location location)
                    {
                        if (location != null)
                        {
                            FineDustContract.View view = (FineDustContract.View) mFragmentList.get(0).first;
                            view.relaod(location.getLatitude(), location.getLongitude());
                        }
                    }

                });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_FINE_COARSE_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }


    private static class MyPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Pair<Fragment, String>> mFragmentList;

        public MyPagerAdapter(FragmentManager fm, List<Pair<Fragment, String>> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).first;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).second;
        }
    }




}
