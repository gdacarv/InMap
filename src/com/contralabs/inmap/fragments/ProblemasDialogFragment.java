package com.contralabs.inmap.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.contralabs.inmap.model.Store;
import com.contralabs.inmap.R;

public class ProblemasDialogFragment extends DialogFragment {
	

	public static final String STORE_KEY = "store_key";
	private Store mStore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		Bundle args = getArguments();
		if(args != null)
			mStore = (Store) args.getSerializable(STORE_KEY);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle(R.string.reporte_um_problema);
		return dialog;
	}

	@Override
	public View getView() {
		FragmentActivity activity = getActivity();
		ListView list = new ListView(activity);
		list.setCacheColorHint(Color.TRANSPARENT);
		list.setAdapter(new SimpleAdapter(activity, getData(), android.R.layout.simple_list_item_1, new String[] {""}, new int[] {android.R.id.text1}));
		list.setOnItemClickListener(onItemClickListener);
		return list;
	}

	private List<? extends Map<String, ?>> getData() {
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		String[] options = getResources().getStringArray(R.array.problem_options);
        for(int i = 0; i < options.length; i++){
        	HashMap<String, String> map = new HashMap<String, String>();
        	map.put("", options[i]);
        	fillMaps.add(map);
        }
		return fillMaps;
	}

	private static void sendEmail(Context context, String msg, String subject) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
		        "mailto", context.getString(R.string.email_problems), null));
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, msg);

		context.startActivity(Intent.createChooser(intent, context.getString(R.string.mandar_e_mail_por_)));
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String msg = ((TextView) view.findViewById(android.R.id.text1)).getText().toString() + "\n\n";
			if(mStore != null)
				msg += getString(R.string.loja_) + mStore.getName() + "\n\n";
			msg += getString(R.string.msg_detalhe);
			String subject = getString(R.string.reporte_de_problema) + (mStore != null ? " - " + mStore.getName() : "");
			sendEmail(getActivity(), msg, subject);
		}
	
	};

	public static void sendSearchQueryNotFound(Context context, String searchQuery) {
		String subject = String.format(context.getString(R.string.msg_subject_querynotfound), searchQuery);
		String msg = String.format(context.getString(R.string.msg_body_querynotfound), searchQuery);
		sendEmail(context, msg, subject);
	}
	
}
