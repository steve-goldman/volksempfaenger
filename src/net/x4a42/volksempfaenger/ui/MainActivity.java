package net.x4a42.volksempfaenger.ui;

import java.util.List;
import java.util.Vector;

import net.x4a42.volksempfaenger.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class MainActivity extends FragmentActivity implements
		OnTabChangeListener, OnPageChangeListener {

	public static final String TAG = "MainActivity";

	private PagerAdapter adapter;
	private ViewPager viewPager;
	private TabHost tabs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tabs = (TabHost) findViewById(R.id.tabhost);
		tabs.setup();
		addTab(tabs.newTabSpec("SubscriptionGridTab").setIndicator(
				getString(R.string.title_tab_subscriptions)));

		addTab(tabs.newTabSpec("DownloadListTab").setIndicator(
				getString(R.string.title_tab_downloads)));

		addTab(tabs.newTabSpec("VolksempfaengerTab").setIndicator(
				getString(R.string.title_tab_buttons)));

		tabs.setOnTabChangedListener(this);

		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this,
				SubscriptionGridFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				DownloadListFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				VolksempfaengerFragment.class.getName()));
		adapter = new PagerAdapter(getSupportFragmentManager(), fragments);

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPager.setAdapter(adapter);
		viewPager.setPageMargin(10);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		BaseActivity.addGlobalMenu(this, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return BaseActivity.handleGlobalMenu(this, item);
	}

	@Override
	public void onTabChanged(String tabId) {
		int pos = tabs.getCurrentTab();
		viewPager.setCurrentItem(pos);
	}

	private void addTab(TabHost.TabSpec tabSpec) {

		// Attach a Tab view factory to the spec
		tabSpec.setContent(this.new TabFactory(this));
		tabs.addTab(tabSpec);
	}

	private class PagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}

	}

	private class TabFactory implements TabContentFactory {
		private final Context context;

		public TabFactory(Context context) {
			this.context = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(context);
			v.setMinimumHeight(0);
			v.setMinimumWidth(0);
			v.setVisibility(View.GONE);
			return v;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		tabs.setCurrentTab(position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub

	}

}
