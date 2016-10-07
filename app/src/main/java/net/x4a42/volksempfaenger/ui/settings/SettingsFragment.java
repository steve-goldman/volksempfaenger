package net.x4a42.volksempfaenger.ui.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment
{
    private SettingsFragmentProxy proxy;

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        proxy = new SettingsFragmentProxyBuilder().build(this);
        proxy.onCreate();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        proxy.onResume();
    }
}
