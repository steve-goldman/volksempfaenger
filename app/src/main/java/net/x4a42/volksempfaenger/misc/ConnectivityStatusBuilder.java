package net.x4a42.volksempfaenger.misc;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectivityStatusBuilder
{
    public ConnectivityStatus build(Context context)
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return new ConnectivityStatus(connectivityManager);
    }
}
