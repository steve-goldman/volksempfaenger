package net.x4a42.volksempfaenger.net;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import android.content.Context;

public abstract class Downloader {
	
	private Context context;
	
	public Downloader(Context context) {
		this.context = context;
	}
	
	protected Context getContext() {
		return context;
	}

	public String getUserAgent() {
		String name = context.getString(R.string.app_name_ascii);
		String version = VolksempfaengerApplication.getPackageInfo(context).versionName;
		return String.format("%s/%s", name, version);
	}
	
}
