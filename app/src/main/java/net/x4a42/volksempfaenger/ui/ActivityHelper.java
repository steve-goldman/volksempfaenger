package net.x4a42.volksempfaenger.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.x4a42.volksempfaenger.Constants;
import net.x4a42.volksempfaenger.R;
import net.x4a42.volksempfaenger.VolksempfaengerApplication;
import net.x4a42.volksempfaenger.ui.OnUpPressedCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActivityHelper {

	public VolksempfaengerApplication getApp(Activity activity) {
		return (VolksempfaengerApplication) activity.getApplication();
	}

	public static void addGlobalMenu(Activity activity, Menu menu) {
		MenuInflater inflater = activity.getMenuInflater();
		inflater.inflate(R.menu.global, menu);
	}

	public static boolean handleGlobalMenu(Activity activity, MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case R.id.item_settings:
			intent = new Intent(activity, SettingsActivity.class);
			activity.startActivity(intent);
			return true;

		case android.R.id.home:
			if (activity instanceof OnUpPressedCallback) {
				((OnUpPressedCallback) activity).onUpPressed();
				return true;
			} else {
				return false;
			}

		default:
			return false;

		}
	}

	public static AlertDialog buildErrorDialog(final Context context,
			final String title, final String message, final Exception e) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message).setCancelable(false);
		final OnClickListener dismissListener = new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		if (e != null) {
			builder.setPositiveButton(R.string.report_error,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							sendCrashReport(context, e);
						}
					});
			builder.setNegativeButton(R.string.cancel, dismissListener);

		} else {
			builder.setPositiveButton(R.string.ok, dismissListener);
		}

		final AlertDialog alert = builder.create();
		return alert;
	}

	public static void sendCrashReport(Context context, Exception exception) {
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				net.x4a42.volksempfaenger.Constants.FEEDBACK_TO);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Error Report");
		final String template = context
				.getString(R.string.template_error_report_message);

		final StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		final String report = exception.toString() + "\n"
				+ exception.getMessage() + "\n" + sw.toString();

		boolean appendStackTraceToMessage = false;
		final File outputDir = context.getExternalCacheDir();
		if (outputDir == null) {
			appendStackTraceToMessage = true;
		}
		try {
			final File reportFile = File.createTempFile(
					Constants.ERROR_REPORT_PREFIX, ".txt", outputDir);
			final FileWriter writer = new FileWriter(reportFile);
			writer.write(report);
			writer.close();
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(reportFile));
		} catch (IOException e) {
			appendStackTraceToMessage = true;
		}
		final String message = String.format(template,
				appendStackTraceToMessage ? report : "");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		context.startActivity(Intent.createChooser(emailIntent,
				context.getString(R.string.title_send_error_report)));
	}

}
