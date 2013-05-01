package com.contralabs.inmap.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.contralabs.inmap.R;

public class SettingsActivity extends PreferenceActivity {
	
	public static final String RELOAD_MAP_TYPE = "com.contralabs.inmap.activities.SettingsActivity.RELOAD_MAP_TYPE";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		final ListPreference listPreference = (ListPreference) findPreference(getString(R.string.pref_key_maps_type));
        if(listPreference.getValue() == null) {
            // to ensure we don't get a null value
            // set first value by default
            listPreference.setValueIndex(0);
        }
		listPreference.setSummary(listPreference.getEntry());
        listPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary(listPreference.getEntries()[listPreference.findIndexOfValue(newValue.toString())]);
                return true;
            }
        });
	}
}
