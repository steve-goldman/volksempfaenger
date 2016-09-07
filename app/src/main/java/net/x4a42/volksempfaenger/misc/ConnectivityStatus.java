package net.x4a42.volksempfaenger.misc;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityStatus
{
    private final ConnectivityManager connectivityManager;

    public ConnectivityStatus(ConnectivityManager connectivityManager)
    {
        this.connectivityManager = connectivityManager;
    }

    public boolean isWifiConnected()
    {
        return isConnected(ConnectivityManager.TYPE_WIFI);
    }

    public boolean isMobileConnected()
    {
        return isConnected(ConnectivityManager.TYPE_MOBILE);
    }

    private boolean isConnected(int networkType)
    {
        //noinspection deprecation
        for (NetworkInfo networkInfo : connectivityManager.getAllNetworkInfo())
        {
            if (networkType == networkInfo.getType() && networkInfo.isConnected())
            {
                return true;
            }
        }

        return false;
    }
}
