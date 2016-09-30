package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.ui.playlist.PlaylistFragment;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.SubscriptionGridFragment;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity implements OnUpPressedCallback {

	public static final String TAG_SUBSCRIPTIONS = "subscriptions";
	public static final String TAG_PLAYLIST       = "playlist";

	private static List<FragmentTab> fragmentTabs;

	static {
		fragmentTabs = new ArrayList<FragmentTab>(3);
		fragmentTabs.add(new FragmentTab(TAG_PLAYLIST,
										 R.string.title_tab_playlist, PlaylistFragment.class));
		fragmentTabs.add(new FragmentTab(TAG_SUBSCRIPTIONS,
				R.string.title_tab_subscriptions,
				SubscriptionGridFragment.class));
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

		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionbar.setHomeButtonEnabled(true);

		// create a TabListener that simply changes the page in the
		// ViewPager
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				setViewPagerCurrentItem(tab.getPosition());
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

		onNewIntent(getIntent());
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);

		if (getIntent().hasExtra("tag")) {
			String tag = getIntent().getStringExtra("tag");
			for (int i = 0; i < fragmentTabs.size(); ++i) {
				if (fragmentTabs.get(i).tag.equals(tag)) {
					viewpager.setCurrentItem(i, false);
					break;
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageReadable(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
					Log.v(this, "Found " + debugDir + ". Enabling debug mode.");
				} else {
					Log.v(this, "Did not find " + debugDir
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
			super(getFragmentManager());
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
		setViewPagerCurrentItem(0);
	}

	public void setViewPagerCurrentItem(int item) {
		// workaround for bug in viewpager. see
		// https://code.google.com/p/android/issues/detail?id=29472#c8
		if (viewpager.getCurrentItem() != item) {
			viewpager.setCurrentItem(item);
		}
	}

}
