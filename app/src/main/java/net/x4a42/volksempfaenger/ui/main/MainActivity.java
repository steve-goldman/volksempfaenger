package net.x4a42.volksempfaenger.ui.main;

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

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiver;
import net.x4a42.volksempfaenger.event.playback.PlaybackEventReceiverBuilder;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManager;
import net.x4a42.volksempfaenger.service.playback.PlaybackServiceConnectionManagerBuilder;
import net.x4a42.volksempfaenger.ui.ExternalStorageHelper;
import net.x4a42.volksempfaenger.ui.OnUpPressedCallback;
import net.x4a42.volksempfaenger.ui.playlist.PlaylistFragment;
import net.x4a42.volksempfaenger.ui.subscriptiongrid.SubscriptionGridFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements OnUpPressedCallback
{

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
	private OptionsMenuManager menuManager;
	private PlaybackServiceConnectionManager connectionManager;
	private PlaybackEventReceiver playbackEventReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		connectionManager = new PlaybackServiceConnectionManagerBuilder().build(this);
		menuManager = new OptionsMenuManagerBuilder().build(this, connectionManager);
		connectionManager.setListener(menuManager);
		connectionManager.onCreate();

		playbackEventReceiver = new PlaybackEventReceiverBuilder().build();
		playbackEventReceiver.setListener(menuManager);

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
	public void onDestroy()
	{
		super.onDestroy();
		connectionManager.onDestroy();
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
		playbackEventReceiver.subscribe();
		invalidateOptionsMenu();
		ExternalStorageHelper.assertExternalStorageReadable(this);
	}

	@Override
	public void onPause()
	{
		super.onPause();
		playbackEventReceiver.unsubscribe();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return menuManager.onCreateOptionsMenu(menu, getMenuInflater());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return menuManager.onOptionsItemSelected(item);
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
