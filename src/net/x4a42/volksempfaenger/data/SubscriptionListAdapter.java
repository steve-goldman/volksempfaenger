package net.x4a42.volksempfaenger.data;

import java.io.File;

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

	static final String[] from = { DatabaseHelper.ExtendedPodcast.TITLE,
			DatabaseHelper.ExtendedPodcast.DESCRIPTION };
	static final int[] to = { R.id.podcast_title, R.id.podcast_description };

	public SubscriptionListAdapter(Context context, Cursor cursor) {
		super(context, R.layout.subscription_list_row, cursor, from, to);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		super.bindView(view, context, cursor);

		TextView newEpisodesText = (TextView) view
				.findViewById(R.id.new_episodes);
		long newEpisodes = cursor.getLong(cursor
				.getColumnIndex(DatabaseHelper.ExtendedPodcast.NEW_EPISODES));
		if (newEpisodes > 0) {
			newEpisodesText.setText(String.valueOf(newEpisodes));
		}

		File podcastLogoFile = Utils.getPodcastLogoFile(context, cursor
				.getLong(cursor.getColumnIndex(DatabaseHelper.Podcast.ID)));
		ImageView podcastLogo = (ImageView) view
				.findViewById(R.id.podcast_logo);
		if (podcastLogoFile.isFile()) {
			Bitmap podcastLogoBitmap = BitmapFactory.decodeFile(podcastLogoFile
					.getAbsolutePath());
			podcastLogo.setImageBitmap(podcastLogoBitmap);
		} else {
			podcastLogo.setImageResource(R.drawable.default_logo);
		}

	}

}
