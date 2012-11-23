package net.x4a42.volksempfaenger.ui;

import java.util.List;

import com.nostra13.universalimageloader.core.assist.OnScrollSmartOptions;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import net.x4a42.volksempfaenger.data.Columns.Podcast;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.receiver.BackgroundErrorReceiver;
import net.x4a42.volksempfaenger.service.UpdateService;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus.GlobalUpdateListener;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus.UiThreadUpdateServiceStatusListenerWrapper;
import net.x4a42.volksempfaenger.service.UpdateServiceStatus.UpdateServiceStatusListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SubscriptionGridFragment extends Fragment implements
		OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	// private static final int CONTEXT_EDIT = 0;
	private static final int CONTEXT_DELETE = 1;

	private static final int PICK_FILE_REQUEST = 0;

	private static final String PODCAST_ORDER = "title IS NULL, title COLLATE NOCASE ASC";

	private GridView grid;
	private View loading;
	private View empty;
	private Adapter adapter;
	private AdapterView.AdapterContextMenuInfo currentMenuInfo;

	private boolean isUpdating = false;
	private UpdateServiceStatusListener updateListener;

	private BroadcastReceiver mErrorReceiver = new ErrorReceiver();

	private final OnScrollSmartOptions smartImageOptions = new OnScrollSmartOptions(
			PodcastLogoView.options);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		adapter = new Adapter();
		Intent intent = getActivity().getIntent();
		String action = intent.getAction();
		if (action != null
				&& action
						.equals(BackgroundErrorReceiver.ACTION_BACKGROUND_ERROR)) {
			showErrorIntent(intent);
		}
		updateListener = new UiThreadUpdateServiceStatusListenerWrapper(
				getActivity(), new UpdateListener());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.subscription_list, container,
				false);

		grid = (GridView) view.findViewById(R.id.grid);
		loading = view.findViewById(R.id.loading);
		empty = view.findViewById(R.id.empty);

		grid.setOnItemClickListener(this);
		grid.setOnCreateContextMenuListener(this);
		grid.setAdapter(adapter);
		grid.setOnScrollListener(smartImageOptions);

		show(GLE.LOADING);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		getLoaderManager().restartLoader(0, null, this);

		UpdateService.Status
				.registerUpdateServiceStatusListener(updateListener);

		IntentFilter filter = new IntentFilter(
				BackgroundErrorReceiver.ACTION_BACKGROUND_ERROR);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
		getActivity().registerReceiver(mErrorReceiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();

		UpdateService.Status
				.unregisterUpdateServiceStatusListener(updateListener);

		getActivity().unregisterReceiver(mErrorReceiver);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.subscription_list, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		MenuItem update = menu.findItem(R.id.item_update);
		if (isUpdating) {
			update.setActionView(R.layout.actionbar_updating);
		} else {
			update.setActionView(null);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_add:
			startActivity(new Intent(getActivity(),
					AddSubscriptionActivity.class));
			return true;
		case R.id.item_update:
			getActivity().startService(
					new Intent(getActivity(), UpdateService.class));
			return true;
		case R.id.import_items:
			Intent intent = new Intent(Constants.ACTION_OI_PICK_FILE);
			intent.putExtra(Constants.EXTRA_OI_TITLE,
					getString(R.string.dialog_choose_opml));

			// check if any app handles the pick file intent
			List<ResolveInfo> list = getActivity().getPackageManager()
					.queryIntentActivities(intent,
							PackageManager.MATCH_DEFAULT_ONLY);
			if (list.size() > 0) {
				startActivityForResult(intent, PICK_FILE_REQUEST);
			} else {
				Log.v(this, "Could not start " + Constants.ACTION_OI_PICK_FILE
						+ " Intent");
				new AlertDialog.Builder(getActivity())
						.setMessage(R.string.dialog_filemanager_missing_message)
						.setPositiveButton(R.string.install,
								installFileManagerDialogOnClickListener)
						.setNegativeButton(R.string.cancel,
								installFileManagerDialogOnClickListener).show();
			}

			return true;
		default:
			return false;
		}
	}

	DialogInterface.OnClickListener installFileManagerDialogOnClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {

			case DialogInterface.BUTTON_POSITIVE:
				Intent intent = new Intent(Intent.ACTION_VIEW);
				try {
					intent.setData(Constants.OI_FILEMANGER_URI_PLAY);
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getActivity(),
							R.string.message_play_store_missing,
							Toast.LENGTH_LONG).show();
					intent.setData(Constants.OI_FILEMANGER_URI_HTTP);
					startActivity(intent);
				}
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				dialog.cancel();
				break;

			}
		}
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

		TextView podcastTitle = (TextView) info.targetView
				.findViewById(R.id.podcast_title);
		String title = podcastTitle.getText().toString();
		menu.setHeaderTitle(title);

		menu.add(0, CONTEXT_DELETE, 0, R.string.context_delete);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		currentMenuInfo = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Intent intent;
		switch (item.getItemId()) {
		case CONTEXT_DELETE:
			intent = new Intent(getActivity(), DeleteSubscriptionActivity.class);
			intent.putExtra("id", currentMenuInfo.id);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PICK_FILE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				if (data != null) {
					try {
						importFile(data.getData().getPath());
					} catch (Exception e) {
						ActivityHelper
								.buildErrorDialog(
										getActivity(),
										getString(R.string.title_import_error),
										getString(R.string.message_error_import_unexpected),
										e).show();
					}
				}
			}
			break;
		}
	}

	public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
		Intent intent = new Intent(getActivity(),
				ViewSubscriptionActivity.class);
		intent.putExtra("id", id);
		startActivity(intent);
	}

	private void importFile(String path) {
		final Intent intent = new Intent(getActivity(),
				ImportFileActivity.class);
		intent.putExtra(ImportFileActivity.EXTRA_IMPORT_FILE_PATH, path);
		startActivity(intent);
	}

	private class Adapter extends SimpleCursorAdapter {

		public Adapter() {
			super(getActivity(), R.layout.subscription_list_row, null,
					new String[] { Podcast.TITLE },
					new int[] { R.id.podcast_title }, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			try {
				if (convertView != null) {
					PodcastLogoView podcastLogo = (PodcastLogoView) convertView
							.findViewById(R.id.podcast_logo);
					if (podcastLogo != null) {
						podcastLogo.reset();
					}
				}
			} catch (ClassCastException e) {
				Log.v(this, e.toString());
			}
			return super.getView(position, convertView, parent);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			super.bindView(view, context, c);
			PodcastCursor cursor = (PodcastCursor) c;

			PodcastLogoView podcastLogo = (PodcastLogoView) view
					.findViewById(R.id.podcast_logo);
			TextView podcastTitle = (TextView) view
					.findViewById(R.id.podcast_title);
			TextView newEpisodesText = (TextView) view
					.findViewById(R.id.new_episodes);
			View loading = view.findViewById(R.id.loading);

			boolean titleIsNull = cursor.titleIsNull();

			podcastLogo.setVisibility(titleIsNull ? View.GONE : View.VISIBLE);
			newEpisodesText.setVisibility(titleIsNull ? View.GONE
					: View.VISIBLE);
			loading.setVisibility(titleIsNull ? View.VISIBLE : View.GONE);

			if (titleIsNull) {
				podcastTitle.setText(cursor.getFeed());
			} else {
				int newEpisodes = cursor.getNewEpisodes();
				int listeningEpisodes = cursor.getListeningEpisodes();
				int listeningOrNewEpisodes = newEpisodes + listeningEpisodes;
				if (listeningOrNewEpisodes > 0) {
					if (listeningOrNewEpisodes > 9) {
						newEpisodesText.setText("+");
					} else {
						newEpisodesText.setText(String
								.valueOf(listeningOrNewEpisodes));
					}
					if (newEpisodes == 0) {
						newEpisodesText
								.setBackgroundResource(R.drawable.badge_subscription_listening);
					} else {
						newEpisodesText
								.setBackgroundResource(R.drawable.badge_subscription_new);
					}
					newEpisodesText.setVisibility(View.VISIBLE);
				} else {
					newEpisodesText.setVisibility(View.INVISIBLE);
				}
				podcastLogo.setPodcastId(cursor.getId(),
						smartImageOptions.getOptions());
			}

		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(),
				VolksempfaengerContentProvider.PODCAST_URI, new String[] {
						Podcast._ID, Podcast.TITLE, Podcast.FEED,
						Podcast.NEW_EPISODES, Podcast.LISTENING_EPISODES },
				null, null, PODCAST_ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data.getCount() == 0) {
			show(GLE.EMPTY);
			adapter.swapCursor(null);
		} else {
			adapter.swapCursor(new PodcastCursor(data));
			show(GLE.GRID);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
		show(GLE.LOADING);
	}

	private enum GLE { // TODO find a meaningful name
		GRID, LOADING, EMPTY
	}

	private void show(GLE gle) {
		switch (gle) {
		case GRID:
			empty.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			grid.setVisibility(View.VISIBLE);
			break;

		case LOADING:
			grid.setVisibility(View.GONE);
			empty.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			break;

		case EMPTY:
			grid.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			empty.setVisibility(View.VISIBLE);
			break;
		}
	}

	private class UpdateListener extends GlobalUpdateListener {

		@Override
		public void onGlobalUpdateStarted() {
			isUpdating = true;
			getActivity().invalidateOptionsMenu();
		}

		@Override
		public void onGlobalUpdateStopped() {
			isUpdating = false;
			getActivity().invalidateOptionsMenu();
		}

	}

	private class ErrorReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			showErrorIntent(intent);
			abortBroadcast();
		}

	}

	private void showErrorIntent(Intent intent) {
		String title = intent
				.getStringExtra(BackgroundErrorReceiver.EXTRA_ERROR_TITLE);
		String text = intent
				.getStringExtra(BackgroundErrorReceiver.EXTRA_ERROR_TEXT);
		if (title == null) {
			return;
		}
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				getActivity());
		builder.setTitle(title)
				.setCancelable(false)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
		if (text != null) {
			builder.setMessage(text);
		}
		if (intent.getIntExtra(BackgroundErrorReceiver.EXTRA_ERROR_ID, 0) == BackgroundErrorReceiver.ERROR_IMPORT) {
			final TextView textView = new TextView(getActivity());
			int padding = Utils.dpToPx(getActivity(), 10);
			textView.setPadding(padding, 0, padding, padding);
			textView.setText(intent
					.getStringExtra(ImportFileActivity.EXTRA_IMPORT_FAILED_ITEMS));
			builder.setView(textView);
		}
		builder.create().show();
	}
}
