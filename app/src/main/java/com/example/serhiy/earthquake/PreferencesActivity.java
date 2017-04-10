package com.example.serhiy.earthquake;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import static junit.runner.BaseTestRunner.savePreferences;

public class PreferencesActivity extends Activity {
    CheckBox autoUPdate;
    Spinner updateFrequencySPinner;
    Spinner magnitudeSpinner;

    public static final String USER_PREFEREMCE = "USER_PREFERENCE";
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
    public static final String PREF_UPDATE_FREG_INDEX = "PREF_UPDATE_FREG_INDEX";

    SharedPreferences prefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        updateFrequencySPinner = (Spinner)findViewById(R.id.spUPdateFreq);
        magnitudeSpinner = (Spinner)findViewById(R.id.spQuakeMag);
        autoUPdate =(CheckBox)findViewById(R.id.checkboxAutoUpdate);

        populateSpinners();

        Context context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        updateUIFromPreferences();

        Button okButon = (Button)findViewById(R.id.okButton);
        okButon.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View _view) {
                                           savePreferences();
                                           PreferencesActivity.this.setResult(RESULT_OK);
                                           finish();
                                       }
                                   }
        );

        Button cancelButton = (Button)findViewById(R.id.canselButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesActivity.this.setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
    private void savePreferences(){
        int updateIndex = updateFrequencySPinner.getSelectedItemPosition();
        int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
        boolean autoUpdateChecked = autoUPdate.isChecked();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
        editor.putInt(PREF_UPDATE_FREG_INDEX, updateIndex);
        editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);
        editor.commit();
    }

    private void updateUIFromPreferences() {
        boolean autoUpdateChecked = prefs.getBoolean(PREF_AUTO_UPDATE, false);
        int updateFrequencyIndex = prefs.getInt(PREF_UPDATE_FREG_INDEX, 2);
        int minMagIndex = prefs.getInt(PREF_MIN_MAG_INDEX, 0);

        updateFrequencySPinner.setSelection(updateFrequencyIndex);
        magnitudeSpinner.setSelection(minMagIndex);
        autoUPdate.setChecked(autoUpdateChecked);
    }

    private void populateSpinners() {
        ArrayAdapter<CharSequence> fAdapter;

        fAdapter = ArrayAdapter.createFromResource(this, R.array.update_freq_options,
            android.R.layout.simple_spinner_item);
        int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
        fAdapter.setDropDownViewResource(spinner_dd_item);
        updateFrequencySPinner.setAdapter(fAdapter);

        ArrayAdapter<CharSequence> mAdapter;

        mAdapter = ArrayAdapter.createFromResource(this,
                R.array.magnitude_options,
                android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(spinner_dd_item);
        magnitudeSpinner.setAdapter(mAdapter);
    }
}
