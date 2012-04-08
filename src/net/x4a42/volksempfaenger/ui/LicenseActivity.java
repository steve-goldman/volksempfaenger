package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import net.x4a42.volksempfaenger.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class LicenseActivity extends Activity {


	private TextView textLicense;
	private TextView textAuthors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.license);

		textLicense = (TextView) findViewById(R.id.license);
		textAuthors = (TextView) findViewById(R.id.authors);

		new LoadAssetTask("LICENSE", textLicense, true).execute();
		new LoadAssetTask("AUTHORS", textAuthors, false).execute();
	}

	private class LoadAssetTask extends AsyncTask<Void, Void, String> {

		String source;
		TextView target;
		boolean skipSingleLineBreak;

		public LoadAssetTask(String source, TextView target,
				boolean skipSingleLineBreak) {
			this.source = source;
			this.target = target;
			this.skipSingleLineBreak = skipSingleLineBreak;
		}

		@Override
		protected String doInBackground(Void... params) {
			InputStream is = null;
			try {
				is = getResources().getAssets().open(source,
						AssetManager.ACCESS_BUFFER);
				Writer wr = new StringWriter();
				Reader rd = new BufferedReader(new InputStreamReader(is,
						"utf-8"));
				int n, nn;
				while ((n = rd.read()) != -1) {
					if (skipSingleLineBreak && n == '\n') {
						nn = rd.read();
						if (nn == '\n') {
							wr.write('\n');
						}
						if (nn != -1) {
							wr.write(nn);
						}
					} else {
						wr.write(n);
					}
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