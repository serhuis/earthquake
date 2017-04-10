package com.example.serhiy.earthquake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;

public class Earthquake extends AppCompatActivity {

    static final private int MENU_PREFERENCES = Menu.FIRST + 1;
    static final private int MENU_UPDATE = Menu.FIRST + 2;

    public int minMagnitude = 0;
    public int updtatFreq = 0;
    public boolean autoUpdateChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateFromPreferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_PREFEREVCES)
            updateFromPreferences();

        FragmentManager fm = getFragmentManager();
        final EarthQuakeListFragment erthquakelist = (EarthQuakeListFragment)fm.findFragmentById(R.id.fEarthQuakeListFragment);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                erthquakelist.refreshEarthQuakes();
            }
        });
        th.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);

        return true;
    }

    private static final int SHOW_PREFEREVCES = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case (MENU_PREFERENCES): {
                Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ?
                PreferenceActivity.class : FragmentPreferences.class;
                Intent i = new Intent(this, c);
                startActivityForResult(i, SHOW_PREFEREVCES);
                return true;
            }
        }

        return false;
    }

    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        minMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
        updtatFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
    }
}
