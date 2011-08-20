package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddSubscriptionActivity extends Activity implements
		OnClickListener {
	private EditText editTextUrl;
	private Button buttonAdd;
	private Button buttonCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subscription);

		LayoutParams params = getWindow().getAttributes();
		params.width = LayoutParams.FILL_PARENT;
		params.flags = LayoutParams.FLAG_BLUR_BEHIND;
		getWindow().setAttributes(params);

		editTextUrl = (EditText) findViewById(R.id.edittext_url);
		buttonAdd = (Button) findViewById(R.id.button_add);
		buttonCancel = (Button) findViewById(R.id.button_cancel);

		buttonAdd.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_add:
			subscribeToFeed();
			break;
		case R.id.button_cancel:
			finish();
			break;
		}
	}

	private void subscribeToFeed() {
		// TODO: subscribe to feed
		Toast.makeText(this, "Successfully subscribed to feed (haha)",
				Toast.LENGTH_SHORT).show();
		finish();
	}
}
