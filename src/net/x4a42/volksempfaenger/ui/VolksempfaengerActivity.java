package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class VolksempfaengerActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volksempfaenger);
		MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);
	}

	public static class MyAdapter extends FragmentPagerAdapter {
		private FragmentManager fragmentManager;
		private static final int NUM = 2;
		private Fragment[] fragments = new Fragment[NUM];
		
		public MyAdapter(FragmentManager fm) {
			super(fm);
			this.fragmentManager = fm;
		}

		VolksempfaengerFragment f;

		@Override
		public int getCount() {
			return NUM;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments[position];
		}

		@Override
		public Object instantiateItem(View container, int position) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			Fragment f = fragments[position];
			switch(position) {
				case 0:
					f = new VolksempfaengerFragment();
					break;
				case 1:
					f = new DownloadListFragment();
					break;
			}
			fragmentTransaction.add(container.getId(), f);
			fragmentTransaction.commit();
			return f;
		}
	}

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

	public void addGlobalMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.global, menu);
	}

	public boolean handleGlobalMenu(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return false;
		}
	}

}
