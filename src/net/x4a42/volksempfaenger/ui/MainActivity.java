package net.x4a42.volksempfaenger.ui;

import java.util.ArrayList;

import net.x4a42.volksempfaenger.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.viewpagertabs.ViewPagerTabProvider;
import com.astuetz.viewpagertabs.ViewPagerTabs;

public class MainActivity extends FragmentActivity {

	public static final String TAG = "MainActivity";

	private Adapter adapter;
	private ViewPager viewPager;
	private ViewPagerTabs tabs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.volksempfaenger);

		adapter = new Adapter(getSupportFragmentManager());
		adapter.addFragment(getString(R.string.title_tab_subscriptions),
				SubscriptionGridFragment.class);
		adapter.addFragment(getString(R.string.title_tab_downloads),
				DownloadListFragment.class);
		adapter.addFragment(getString(R.string.title_tab_buttons),
				VolksempfaengerFragment.class);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);

		tabs = (ViewPagerTabs) findViewById(R.id.tabs);
		tabs.setViewPager(viewPager);
	}

	public static class Adapter extends FragmentPagerAdapter implements
			ViewPagerTabProvider {

		private FragmentManager fragmentManager;
		private ArrayList<Class<? extends Fragment>> fragments;
		private ArrayList<String> titles;

		public Adapter(FragmentManager fm) {
			super(fm);
			fragmentManager = fm;
			fragments = new ArrayList<Class<? extends Fragment>>();
			titles = new ArrayList<String>();
		}

		public void addFragment(String title, Class<? extends Fragment> fragment) {
			titles.add(title);
			fragments.add(fragment);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		public String getTitle(int position) {
			return titles.get(position);
		}

		@Override
		public Fragment getItem(int position) {
			try {
				return fragments.get(position).newInstance();
			} catch (InstantiationException e) {
				Log.wtf(TAG, e);
			} catch (IllegalAccessException e) {
				Log.wtf(TAG, e);
			}
			return null;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			Fragment f = getItem(position);
			fragmentTransaction.add(container.getId(), f);
			fragmentTransaction.commit();
			return f;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		BaseActivity.addGlobalMenu(this, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		default:
			return BaseActivity.handleGlobalMenu(this, item);
		}
	}

}
