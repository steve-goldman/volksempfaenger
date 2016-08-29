package net.x4a42.volksempfaenger.ui;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class DisplayValueListPreference extends ListPreference {

	public DisplayValueListPreference(Context context) {
		super(context);
	}

	public DisplayValueListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public CharSequence getSummary() {
		CharSequence entry = getEntry();
		if (entry != null) {
			return entry;
		} else {
			return null;
		}
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
		notifyChanged();
	}

}
