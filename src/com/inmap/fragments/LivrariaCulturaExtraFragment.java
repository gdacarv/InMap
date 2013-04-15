package com.inmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inmap.salvadorshop.R;
import com.inmap.views.EventosCulturaView;

public class LivrariaCulturaExtraFragment extends ExtraFragment {

	@Override
	public String getTitle() {
		return getString(R.string.eventos);
	}

	@Override
	public int getIconResId() {
		return R.drawable.ico_eventos_lojas;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setReady(true); // TODO Call setReady(true) when content was loaded
		return new EventosCulturaView(getActivity()); 
	}
}
