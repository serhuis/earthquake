package com.example.serhiy.earthquake;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

public class FragmentPreferences extends PreferenceActivity {

    SharedPreferences prefs;

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    private void savePreferences(){
        Editor editor = prefs.edit();
        editor.commit();
    }

}
