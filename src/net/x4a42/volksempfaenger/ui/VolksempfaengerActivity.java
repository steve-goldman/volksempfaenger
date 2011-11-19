package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class VolksempfaengerActivity extends FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volksempfaenger);
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		addGlobalMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			handleGlobalMenu(item);
		}
		return true;
	}
*/
}
