package net.x4a42.volksempfaenger.ui;

import net.x4a42.volksempfaenger.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class VolksempfaengerFragment extends Fragment implements
		OnClickListener {

	private Button buttonListenQueue;
	private Button buttonDebug;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.buttons, container, false);

		buttonListenQueue = (Button) view.findViewById(R.id.button_listenqueue);
		buttonDebug = (Button) view.findViewById(R.id.button_debug);

		buttonListenQueue.setOnClickListener(this);
		buttonDebug.setOnClickListener(this);
		return view;
	}

	public void onClick(View v) {
		Intent intent;

		switch (v.getId()) {
		case R.id.button_listenqueue:
			intent = new Intent(getActivity(), ListenQueueActivity.class);
			startActivity(intent);
			return;
		case R.id.button_debug:
			intent = new Intent(getActivity(), DebugActivity.class);
			startActivity(intent);
			return;
		}
	}
}
