package net.x4a42.volksempfaenger.misc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.EnumSet;
import java.util.Set;

public class NetworkHelper {

	public enum NetworkType {
		NETWORK_WIFI,
		NETWORK_MOBILE
	}

	public static Set<NetworkType> getNetworkType(Context ctx) {
		EnumSet<NetworkType> networkType = EnumSet.noneOf(NetworkType.class);
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		for (NetworkInfo netInfo : cm.getAllNetworkInfo()) {
			if (netInfo != null && netInfo.isConnected()) {
				switch (netInfo.getType()) {
					case ConnectivityManager.TYPE_WIFI:
						networkType.add(NetworkType.NETWORK_WIFI);
						break;
					case ConnectivityManager.TYPE_MOBILE:
						networkType.add(NetworkType.NETWORK_MOBILE);
						break;
				}
			}
		}
		return networkType;
	}
}
