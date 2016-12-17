package com.udacity.mauricio.popularmovies.gui.view;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.utils.AppUtils;

public class MovieSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private boolean starting;
    private SettingsActivity settingsActivity;
    private String oldLanguage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        settingsActivity = (SettingsActivity) getActivity();
        starting = true;
        bindPreferenceToSummaryValue(findPreference(getString(R.string.pref_sort_key)));
        bindPreferenceToSummaryValue(findPreference(getString(R.string.pref_language_key)));
    }

    private void bindPreferenceToSummaryValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        String strValue = value.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(strValue);
            if (index >= 0) {
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
            if (listPreference.getKey().equals(getString(R.string.pref_language_key)))
                setLocale(strValue);
        } else {
            preference.setSummary(strValue);
        }

        return true;
    }

    public void setLocale(String lang) {

        if (starting) {
            starting = false;
            oldLanguage = lang;
            return;
        }

        if (!TextUtils.equals(oldLanguage, lang)) {
            oldLanguage = lang;
            AppUtils.setLocale(getActivity(), lang);
            settingsActivity.restart();
        }
    }

}
