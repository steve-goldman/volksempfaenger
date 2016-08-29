package net.x4a42.volksempfaenger.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.data.Columns;
import net.x4a42.volksempfaenger.data.PodcastCursor;
import net.x4a42.volksempfaenger.data.VolksempfaengerContentProvider;
import net.x4a42.volksempfaenger.export.OpmlExport;

import java.io.File;
import java.io.IOException;

/**
 * Created by kolja on 20.05.13.
 */
public class ExportTask extends AsyncTask<Void, Void, Boolean> {

	private Context context;
	private String filename;


	public ExportTask(Context context)
	{
		this.context = context;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);
		if(success)
		{
			Toast.makeText(context, context.getString(R.string.message_export_ok, filename), Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(context, R.string.message_export_failed, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(
				VolksempfaengerContentProvider.PODCAST_URI,
				new String[] {Columns.Podcast.DESCRIPTION, Columns.Podcast.FEED,
						Columns.Podcast.TITLE, Columns.Podcast.WEBSITE }, null, null, null);
		PodcastCursor podcastCursor = new PodcastCursor(cursor);
		File file = new File(Environment.getExternalStorageDirectory(), "holopd_export.opml");
		try {
			OpmlExport.export(podcastCursor, file);
		} catch (IOException e) {
			Log.e(Log.getTag(this.getClass()), "failed to export", e);
			return false;
		}
		filename = file.getAbsolutePath();
		return true;
	}
}
