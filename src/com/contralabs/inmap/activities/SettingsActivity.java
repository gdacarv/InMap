package com.contralabs.inmap.activities;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.Toast;

import com.contralabs.inmap.R;
import com.facebook.Session;

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
        
        Session activeSession = Session.getActiveSession();
		if(activeSession != null && activeSession.isOpened()) {
			final PreferenceCategory category = (PreferenceCategory) findPreference(getString(R.string.pref_key_social));
			Preference logout = new Preference(this);
			logout.setTitle(R.string.msg_logout);
			logout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Session.getActiveSession().closeAndClearTokenInformation();
					category.removePreference(preference);
					Toast.makeText(SettingsActivity.this, R.string.desconectado_, Toast.LENGTH_SHORT).show();
					return true;
				}
			});
			category.addPreference(logout);
		}
	}
}
