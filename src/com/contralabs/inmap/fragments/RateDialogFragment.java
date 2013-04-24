package com.contralabs.inmap.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.contralabs.inmap.R;

public class RateDialogFragment extends DialogFragment {

	private static final String RATED_KEY = "hasRated";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Context context = getActivity();
		return new AlertDialog.Builder(context)
			.setIcon(android.R.drawable.star_big_on)
			.setMessage(R.string.msg_ratedialog_msg)
			.setTitle(R.string.msg_ratedialog_title)
			.setPositiveButton(R.string.msg_ratedialog_positive, new DialogInterface.OnClickListener() { 
				public void onClick(DialogInterface dialog, int id) {
					Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+context.getApplicationInfo().packageName));
					marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					context.startActivity(marketIntent);
					PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(RATED_KEY, true).commit();
					finish();
				}
			})
			.setNeutralButton(R.string.msg_ratedialog_neutral, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setNegativeButton(R.string.msg_ratedialog_negative, new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(RATED_KEY, true).commit();
					finish();
				}
			})
			.create();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		finish();
	}

	private void finish() {
		FragmentActivity activity = getActivity();
		activity.setResult(Activity.RESULT_OK);
		activity.finish();
	}

	public static boolean showIfAppropriate(Context context, FragmentManager manager) {
		if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(RATED_KEY, false)) {
			new RateDialogFragment().show(manager, "RateDialogFragment");
			return true;
		}
		return false;
	}
}
