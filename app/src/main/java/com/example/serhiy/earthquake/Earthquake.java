package com.example.serhiy.earthquake;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
            if (resultCode == Activity.RESULT_OK) {
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
                Intent i = new Intent(this,
                        PreferencesActivity.class);
                startActivityForResult(i, SHOW_PREFEREVCES);
                return true;
            }
        }

        return false;
    }

    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int minMagIndex = prefs.getInt(PreferencesActivity.PREF_MIN_MAG_INDEX, 0);
        if (minMagIndex < 0)
            minMagIndex = 0;

        int freqIndex = prefs.getInt(PreferencesActivity.PREF_UPDATE_FREG_INDEX, 0);
        if (freqIndex < 0)
            freqIndex = 0;

        autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);

        Resources r = getResources();

        String[] minMagValues = r.getStringArray(R.array.magnitude);
        String[] freqValues = r.getStringArray(R.array.update_freq_values);

        minMagnitude = Integer.valueOf(freqValues[freqIndex]);
        updtatFreq = Integer.valueOf(freqValues[freqIndex]);
    }
}
