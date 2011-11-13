package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import net.x4a42.volksempfaenger.R;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class LicenseActivity extends BaseActivity {

	private TextView textLicense;
	private TextView textAuthors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.license);

		textLicense = (TextView) findViewById(R.id.license);
		textAuthors = (TextView) findViewById(R.id.authors);

		new LoadAssetTask("LICENSE", textLicense).execute();
		new LoadAssetTask("AUTHORS", textAuthors).execute();
	}

	private class LoadAssetTask extends AsyncTask<Void, Void, String> {

		String source;
		TextView target;

		public LoadAssetTask(String source, TextView target) {
			this.source = source;
			this.target = target;
		}

		@Override
		protected String doInBackground(Void... params) {
			InputStream is = null;
			try {
				is = getResources().getAssets().open(source,
						AssetManager.ACCESS_BUFFER);
				Writer wr = new StringWriter();
				char[] buf = new char[1024];
				Reader rd = new BufferedReader(new InputStreamReader(is,
						"utf-8"));
				int n;
				while ((n = rd.read(buf)) != -1) {
					wr.write(buf, 0, n);
				}
				return wr.toString();
			} catch (Exception e) {
				return "";
			} finally {
				try {
					is.close();
				} catch (Exception e) {

				}
			}
		}

		@Override
		protected void onPostExecute(String result) {
			target.setText(result);
		}

	}

}