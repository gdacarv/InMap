package com.contralabs.inmap.fragments;

import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.widget.ScrollView;
import android.widget.TextView;

import com.contralabs.inmap.R;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LegalNoticesDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context context = getActivity();

		ScrollView scrollView = new ScrollView(context);
		// Set up the TextView
		final TextView message = new TextView(context);
		// We'll use a spannablestring to be able to make links clickable
		final SpannableString s = new SpannableString(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(context));

		// Set some padding
		int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
		message.setPadding(padding, padding, padding, padding);
		// Set up the final string
		message.setText(s);
		scrollView.addView(message);
		// Now linkify the text
		Linkify.addLinks(message, Linkify.ALL);
		Linkify.addLinks(message, Pattern.compile("@([A-Za-z0-9_-]+)"), "https://twitter.com/#!/");
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.legal_notices)
			.setCancelable(true)
			.setIcon(R.drawable.ic_launcher)
			.setPositiveButton(context.getString(R.string.voltar), (OnClickListener) null)
			.setView(scrollView)
			.create();
	}
}
