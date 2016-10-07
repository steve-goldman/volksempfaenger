package net.x4a42.volksempfaenger.ui.settings;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity
{
    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
