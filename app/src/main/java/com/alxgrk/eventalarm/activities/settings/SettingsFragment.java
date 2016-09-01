/*
 * Created on 01.04.2016
 *
 * author Alex
 */
package com.alxgrk.eventalarm.activities.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.alxgrk.eventalarm.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
