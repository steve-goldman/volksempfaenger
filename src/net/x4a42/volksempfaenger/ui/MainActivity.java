package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.x4a42.volksempfaenger.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends FragmentActivity implements
		OnUpPressedCallback {

	public static final String TAG = "MainActivity";

	private static List<FragmentTab> fragmentTabs;
	private Menu menu;

	static {
		fragmentTabs = new ArrayList<FragmentTab>(3);
		fragmentTabs.add(new FragmentTab("subscriptions",
				R.string.title_tab_subscriptions,
				SubscriptionGridFragment.class));
		fragmentTabs.add(new FragmentTab("downloads",
				R.string.title_tab_downloads, DownloadListFragment.class));
	}

	private ViewPager viewpager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initialize ViewPager
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		viewpager.setAdapter(new PagerAdapter());
		viewpager.setPageMargin(10);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			// ActionBar is only available on Honeycomb and later
			ActionBar actionbar = getActionBar();
			actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionbar.setHomeButtonEnabled(true);

			// create a TabListener that simply changes the page in the
			// ViewPager
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					viewpager.setCurrentItem(tab.getPosition());
				}

				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}

				public void onTabReselected(Tab tab, FragmentTransaction ft) {
				}
			};

			// iterate over all tabs and add them to the ActionBar
			for (FragmentTab ft : fragmentTabs) {
				actionbar.addTab(actionbar.newTab().setText(ft.string)
						.setTabListener(tabListener));
			}

			// add an OnPageChangeListener that switches to the correct tab in
			// the ActionBar
			viewpager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					getActionBar().setSelectedNavigationItem(position);
				}
			});

		} else {

			// for Gingerbread we use a TabHost as fallback
			final TabHost tabs = (TabHost) findViewById(R.id.tabhost);
			tabs.setup();

			// iterate over all tabs and add them to the TabHost
			for (FragmentTab ft : fragmentTabs) {
				TabSpec tabSpec = tabs.newTabSpec(ft.tag).setIndicator(
						getString(ft.string));
				tabSpec.setContent(R.id.empty_view);
				tabs.addTab(tabSpec);
			}

			// set an OnTabChangeListener that changes the page in the ViewPager
			tabs.setOnTabChangedListener(new OnTabChangeListener() {
				@Override
				public void onTabChanged(String tabId) {
					int pos = tabs.getCurrentTab();
					viewpager.setCurrentItem(pos);
				}
			});

			// add an OnPageChangeListener that switches to the correct tab in
			// the TabHost
			viewpager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					tabs.setCurrentTab(position);
					if (menu == null) {
						return;
					}
					menu.clear();
					onCreateOptionsMenu(menu);
					MenuInflater inflater = getMenuInflater();
					PagerAdapter adapter = (PagerAdapter) viewpager
							.getAdapter();
					adapter.fragments[position].onCreateOptionsMenu(menu,
							inflater);
				}
			});
		}

		if (getIntent().hasExtra("tag")) {
			String tag = getIntent().getStringExtra("tag");
			for (int i = 0; i < fragmentTabs.size(); ++i) {
				if (fragmentTabs.get(i).tag.equals(tag)) {
					viewpager.setCurrentItem(i, false);
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Workaround for Bug #19917
		// http://code.google.com/p/android/issues/detail?id=19917
		// TODO: fix this!
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		ActivityHelper.addGlobalMenu(this, menu);
		{
			// Add debug menu item if
			// /sdcard/Android/data/net.x4a42.volksempfaenger/debug/ exists
			File ext = getExternalFilesDir(null);
			if (ext != null) {
				File debugDir = new File(ext.getParent(), "debug");
				if (debugDir != null && debugDir.isDirectory()) {
					MenuItem item = menu.add("Debug");
					item.setIntent(new Intent(this, DebugActivity.class));
					Log.d(TAG, "Found " + debugDir + ". Enabling debug mode.");
				} else {
					Log.d(TAG, "Did not find " + debugDir
							+ ". Disabling debug mode.");
				}
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return ActivityHelper.handleGlobalMenu(this, item);
	}

	private class PagerAdapter extends FragmentPagerAdapter {
		private Fragment[] fragments;

		public PagerAdapter() {
			super(getSupportFragmentManager());
			fragments = new Fragment[fragmentTabs.size()];
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			if (fragments[position] == null) {
				fragment = Fragment.instantiate(MainActivity.this,
						fragmentTabs.get(position).fragment.getName());
				fragments[position] = fragment;
			} else {
				fragment = fragments[position];
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return fragments.length;
		}
	}

	// abstract class that implements 2 methods of an interface in order to
	// avoid having empty methods in anonymous classes that implement
	// OnPageChangeListener
	private static abstract class SimpleOnPageChangeListener implements
			OnPageChangeListener {
		@Override
		public abstract void onPageSelected(int arg0);

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	// simple data structure for storing information about all tabs
	private static class FragmentTab {
		private String tag;
		private int string;
		private Class<? extends Fragment> fragment;

		public FragmentTab(String tag, int string,
				Class<? extends Fragment> fragment) {
			this.tag = tag;
			this.string = string;
			this.fragment = fragment;
		}
	}

	@Override
	public void onUpPressed() {
		viewpager.setCurrentItem(0);
	}

}
