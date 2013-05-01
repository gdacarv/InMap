package com.contralabs.inmap.fragments;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

public class ProximityCheckDialogFragment extends DialogFragment {

	public static final String SHOW = "com.contralabs.inmap.fragments.ProximityCheckDialogFragment.SHOW";
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Context context = getActivity();
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() { 
			public void onClick(DialogInterface dialog, int id) {
				EasyTracker.getTracker().sendEvent("SystemAction", "ProximityAlert", "Validation", id == DialogInterface.BUTTON_POSITIVE ? 1l : 0l);
				Toast.makeText(context, "Obrigado!", Toast.LENGTH_SHORT).show();
			}
		};
		return new AlertDialog.Builder(context)
			.setMessage("O aplicativo identificou sua presença no Salvador Shopping. Você realmente está no Shopping?")
			.setPositiveButton("Sim", listener)
			.setNegativeButton("Não", listener)
			.create();
	}

	public static boolean showIfAppropriate(Intent intent, FragmentManager supportFragmentManager) {
		if(intent.getBooleanExtra(SHOW, false)) {
			new ProximityCheckDialogFragment().show(supportFragmentManager, "ProximityCheckDialogFragment");
			intent.getExtras().remove(SHOW);
			return true;
		}
		return false;
	}

}
