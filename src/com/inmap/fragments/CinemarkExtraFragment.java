package com.inmap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inmap.salvadorshop.R;

public class CinemarkExtraFragment extends ExtraFragment {

	@Override
	public String getTitle() {
		return getString(R.string.filmes_e_hor_rios);
	}

	@Override
	public int getIconResId() {
		return R.drawable.ico_eventos_lojas;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		WebView webView = new WebView(getActivity());
		WebSettings settings = webView.getSettings();
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		webView.loadUrl("http://www.cinemark.com.br/programacao/bolso/salvador/salvador/26/785");
		webView.setWebViewClient(new WebViewClient() {

			   public void onPageFinished(WebView view, String url) {
			        setReady(true);
			    }
			});
		return webView;
	}
}
