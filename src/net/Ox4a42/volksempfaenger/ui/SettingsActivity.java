package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  

        addPreferencesFromResource(R.xml.settings);
    }
}