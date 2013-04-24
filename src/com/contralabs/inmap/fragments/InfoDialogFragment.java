package com.contralabs.inmap.fragments;

import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.widget.TextView;

import com.contralabs.inmap.R;

public class InfoDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context context = getActivity();
		// Try to load the a package matching the name of our own package
		PackageInfo pInfo = null;
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String versionInfo = pInfo.versionName;

		String aboutTitle = context.getString(R.string.sobre) + " " + context.getString(R.string.app_name);
		String versionString = String.format("%s: %s", context.getString(R.string.versao), versionInfo);

		// Set up the TextView
		final TextView message = new TextView(context);
		// We'll use a spannablestring to be able to make links clickable
		final SpannableString s = new SpannableString(context.getString(R.string.sobre_texto));

		// Set some padding
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		message.setPadding(padding, padding, padding, padding);
		// Set up the final string
		message.setText(versionString + "\n\n" + s);
		// Now linkify the text
		Linkify.addLinks(message, Linkify.ALL);
		Linkify.addLinks(message, Pattern.compile("@([A-Za-z0-9_-]+)"), "https://twitter.com/#!/");
		return new AlertDialog.Builder(getActivity())
			.setTitle(aboutTitle)
			.setCancelable(true)
			.setIcon(R.drawable.ic_launcher)
			.setPositiveButton(context.getString(R.string.voltar), (OnClickListener) null)
			.setView(message)
			.create();
	}
}
