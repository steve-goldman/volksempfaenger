package net.Ox4a42.volksempfaenger.ui;

import net.Ox4a42.volksempfaenger.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VolksempfaengerActivity extends BaseActivity implements OnClickListener {
	private Button buttonAddSubscription;
    private Button buttonSubscriptionList;
    private Button buttonListenQueue;
    private Button buttonDownloadQueue;
    private Button buttonSettings;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonAddSubscription = (Button) findViewById(R.id.button_addsubscription);
        buttonSubscriptionList = (Button) findViewById(R.id.button_subscriptionlist);
        buttonListenQueue = (Button) findViewById(R.id.button_listenqueue);
        buttonDownloadQueue = (Button) findViewById(R.id.button_downloadqueue);
        buttonSettings = (Button) findViewById(R.id.button_settings);
        
        buttonAddSubscription.setOnClickListener(this);
        buttonSubscriptionList.setOnClickListener(this);
        buttonListenQueue.setOnClickListener(this);
        buttonDownloadQueue.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);
    }

	public void onClick(View v) {
		Intent intent;
		
		switch (v.getId()) {
		case R.id.button_addsubscription:
			intent = new Intent(this, AddSubscriptionActivity.class);
			startActivity(intent);
			break;
		case R.id.button_subscriptionlist:
			intent = new Intent(this, SubscriptionListActivity.class);
			startActivity(intent);
			break;
		case R.id.button_listenqueue:
			intent = new Intent(this, ListenQueueActivity.class);
			startActivity(intent);
			break;
		case R.id.button_downloadqueue:
			intent = new Intent(this, DownloadQueueActivity.class);
			startActivity(intent);
			break;
		case R.id.button_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.itemSettings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}
		return true;
	}
}