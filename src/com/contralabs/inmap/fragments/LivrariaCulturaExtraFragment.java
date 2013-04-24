package com.contralabs.inmap.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.contralabs.inmap.views.EventosCulturaView;
import com.contralabs.inmap.R;

public class LivrariaCulturaExtraFragment extends ExtraFragment {

	@Override
	public String getTitle() {
		return getString(R.string.eventos);
	}

	@Override
	public int getIconResId() {
		return getIconResIdStatic();
	}

	public static int getIconResIdStatic() {
		return R.drawable.ico_eventos_lojas;
	}
	
	public static String getDescription(Resources res) {
		return res.getString(R.string.msg_description_cultura);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setReady(true); // TODO Call setReady(true) when content was loaded
		return new EventosCulturaView(getActivity()); 
	}
}
