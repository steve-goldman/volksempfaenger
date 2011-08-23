package net.x4a42.volksempfaenger.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import net.x4a42.volksempfaenger.R;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class ViewEpisodeActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_episode);

		TextView textView = (TextView) findViewById(R.id.textView2);

		InputStream ins = getResources().openRawResource(R.raw.description);
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuilder total = new StringBuilder();
		String line;
		String description;
		try {
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		description = normalizeString(total.toString());
		textView.setText(Html.fromHtml(description));
	}

	private String normalizeString(String string) {
		return string.replaceAll("\\s+", " ");
	}

}
