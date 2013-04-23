package com.inmap.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.inmap.salvadorshop.R;

public class WebViewCinemarkExtraFragment extends ExtraFragment {

	@Override
	public String getTitle() {
		return getString(R.string.filmes_e_hor_rios);
	}

	@Override
	public int getIconResId() {
		return getIconResIdStatic();
	}

	public static int getIconResIdStatic() {
		return R.drawable.ico_eventos_lojas;
	}
	
	public static String getDescription(Resources res) {
		return res.getString(R.string.msg_description_cinemark);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		WebView webView = new WebView(getActivity());
		webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.cinemark_height)));
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
