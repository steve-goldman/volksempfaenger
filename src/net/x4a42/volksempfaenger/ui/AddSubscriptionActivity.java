package net.x4a42.volksempfaenger.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.x4a42.volksempfaenger.Log;
import net.x4a42.volksempfaenger.R;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

public class AddSubscriptionActivity extends Activity implements
		OnClickListener {

	private EditText editTextUrl;
	private Button buttonAdd;
	private Button buttonCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_add_subscription);
		setContentView(R.layout.add_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes(params);

		editTextUrl = (EditText) findViewById(R.id.edittext_url);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonAdd.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();

		Uri data = getIntent().getData();
		if (data != null) {
			editTextUrl.setText(data.toString());
		} else {
			// see if there is a link in the clipboard
			ClipboardManager cm = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
			ClipData clip = cm.getPrimaryClip();
			if (clip != null) {
				ClipData.Item item = clip.getItemAt(0);
				if (item != null && item.getText() != null) {
					String text = item.getText().toString();
					try {
						URL url = new URL(text);
						editTextUrl.setText(url.toString());
					} catch (MalformedURLException e) {
						Log.e(this, "no url");
						Matcher matcher = Pattern
								.compile(
										"\\b(http|https):[/]*[\\w-]+\\.[\\w./?&@#-]+")
								.matcher(text);
						if (matcher.find()) {
							Log.e(this, "url found");
							editTextUrl.setText(matcher.group());
						}
						Log.e(this, "finish");
					}
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		ExternalStorageHelper.assertExternalStorageWritable(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
			subscribeToFeed();
			return;
		case R.id.button_cancel:
			finish();
			return;
		}
	}

	private void subscribeToFeed() {
		String feedUrl = editTextUrl.getText().toString();

		new AddFeedTask(getApplicationContext()).execute(feedUrl);
		finish();
	}
}
