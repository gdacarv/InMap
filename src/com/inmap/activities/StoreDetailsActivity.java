package com.inmap.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.inmap.InMapApplication;
import com.inmap.actionbar.ActionBarActivity;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.model.Store;
import com.inmap.salvadorshop.R;
import com.inmap.views.EventosCulturaView;

public class StoreDetailsActivity extends ActionBarActivity {

	public static final String STORE = "store";
	private Store mStore;
	private ApplicationDataFacade mApplicationDataFacade;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_details);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			getActionBar().setHomeButtonEnabled(true);

		if(savedInstanceState != null)
			mStore = (Store) savedInstanceState.getSerializable(STORE);
		else
			mStore = (Store) getIntent().getSerializableExtra(STORE);

		mApplicationDataFacade = ((InMapApplication)getApplication()).getApplicationDataFacade();
		setActionBarTitle("  " + mStore.getTitle());
		populateViews();

		findViewById(R.id.btn_details_map).setOnClickListener(onMapButtonClick);
		
		checkAndConfigureCinema();
		checkAndConfigureLivrariaCultura();
	}

	private void populateViews() {
		((TextView)findViewById(R.id.txt_details_description)).setText(getString(R.string.descricao_) + " " + mStore.getDescription());
		TextView phoneTextView = (TextView)findViewById(R.id.txt_details_phone);
		phoneTextView.setText(getString(R.string.telefone_) + " " + mStore.getPhone());
		TextView websiteTextView = (TextView)findViewById(R.id.txt_details_website);
		websiteTextView.setText(getString(R.string.site_) + " " + mStore.getWebsite());
		((TextView)findViewById(R.id.txt_details_category)).setText(getString(R.string.categoria_) + " " + getString(mStore.getCategory().getTitleRes()));
		((TextView)findViewById(R.id.txt_details_level)).setText(getString(R.string.andar_) + " " + mApplicationDataFacade.getLevelInformation().getTitle(mStore.getLevel()));

		phoneTextView.setOnClickListener(onPhoneClickListener);
		websiteTextView.setOnClickListener(onWebsiteClickListener);
	}

	private void setActionBarTitle(String title) {
		setTitle(title);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_store_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			startActivity(new Intent(StoreDetailsActivity.this, MainActivity.class));
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener onMapButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(mStore.getLevel() >= 3) {// XXX Take it off when maps L2 and L3 are ready
				Toast.makeText(StoreDetailsActivity.this, String.format(getString(R.string.msg_mapanaodisponivel), mApplicationDataFacade.getLevelInformation().getTitle(mStore.getLevel())), Toast.LENGTH_SHORT).show();
				return;
			}
			Intent i = new Intent(StoreDetailsActivity.this, MainActivity.class);
			i.putExtra(MainActivity.SHOW_STORE_INMAP, mStore);
			startActivity(i);
		}
	};

	private OnClickListener onPhoneClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialPhone(mStore.getPhone());
		}
	};

	private OnClickListener onWebsiteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			openUrl(mStore.getWebsite());
		}
	};

	private void dialPhone(String number) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + number.trim()));
		startActivity(intent);
	}
	
	private void openUrl(String url) {
		if(!url.startsWith("http://"))
			url = "http://" + url;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	private void checkAndConfigureCinema() {
		if(mStore.getId() == 409) {
			WebView webView = new WebView(this);
			((ViewGroup) findViewById(R.id.layout_root)).addView(webView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			webView.loadUrl("http://www.cinemark.com.br/programacao/bolso/salvador/salvador/26/785");
		}
	}
	
	private void checkAndConfigureLivrariaCultura() {
		if(mStore.getId() == 207) {
			View view = new EventosCulturaView(this);
			((ViewGroup) findViewById(R.id.layout_root)).addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		}
	}
}
