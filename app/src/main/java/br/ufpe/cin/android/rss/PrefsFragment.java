package br.ufpe.cin.android.rss;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class PrefsFragment extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private String APP_TAG;

    public PrefsFragment(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.rss_preferences, rootKey);
        APP_TAG = getString(R.string.app_name).concat(" (PrefsFragment)");
        Log.d(APP_TAG, "onCreatePreferences");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(APP_TAG, "onResume");
        getPreferenceManager() .getSharedPreferences().registerOnSharedPreferenceChangeListener(this.listener);
    }

    @Override
    public void onDestroy() {
        Log.d(APP_TAG, "onDestroy");
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this.listener);
        super.onDestroy();
    }
}