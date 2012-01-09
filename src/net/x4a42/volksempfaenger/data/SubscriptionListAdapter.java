package net.x4a42.volksempfaenger.data;

import java.io.File;
import java.util.HashMap;

import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.Utils;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SubscriptionListAdapter extends SimpleCursorAdapter {

	static final String[] from = { DatabaseHelper.ExtendedPodcast.TITLE };
	static final int[] to = { R.id.podcast_title };

	// TODO: there must be a better way to do this...
	private HashMap<Long, Bitmap> logoCache;

	public SubscriptionListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.subscription_list_row, cursor, from, to);
		logoCache = new HashMap<Long, Bitmap>(cursor.getCount());
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		super.bindView(view, context, cursor);

		TextView newEpisodesText = (TextView) view
				.findViewById(R.id.new_episodes);
		long newEpisodes = cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.ExtendedPodcast.NEW_EPISODES));
		if (newEpisodes > 0) {
			newEpisodesText.setText(newEpisodes < 10 ? String
					.valueOf(newEpisodes) : "+");
			newEpisodesText.setVisibility(View.VISIBLE);
		} else {
			newEpisodesText.setVisibility(View.INVISIBLE);
		}

		ImageView podcastLogo = (ImageView) view
				.findViewById(R.id.podcast_logo);
		Long podcastId = cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.Podcast.ID));
		if (!podcastId.equals(podcastLogo.getTag(R.id.podcast_logo))) {
			podcastLogo.setTag(R.id.podcast_logo, podcastId);
			Bitmap podcastLogoBitmap = null;
			if (logoCache.containsKey(podcastId)) {
				podcastLogoBitmap = logoCache.get(podcastId);
			} else {
				File podcastLogoFile = Utils.getPodcastLogoFile(context,
						podcastId);
				if (podcastLogoFile.isFile()) {
					podcastLogoBitmap = BitmapFactory
							.decodeFile(podcastLogoFile.getAbsolutePath());
				}
				logoCache.put(podcastId, podcastLogoBitmap);
			}
			if (podcastLogoBitmap == null) {
				podcastLogo.setImageResource(R.drawable.default_logo);
			} else {
				podcastLogo.setImageBitmap(podcastLogoBitmap);
			}
		}
	}
}
