package com.inmap.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inmap.InMapApplication;
import com.inmap.actionbar.ActionBarActivity;
import com.inmap.fragments.ExtraFragment.OnReadyChangeListener;
import com.inmap.fragments.ExtraFragment;
import com.inmap.fragments.ProblemasDialogFragment;
import com.inmap.interfaces.ApplicationDataFacade;
import com.inmap.model.Store;
import com.inmap.salvadorshop.R;
import com.inmap.views.EventosCulturaView;

public class StoreDetailsActivity extends ActionBarActivity {

	public static final String STORE = "store";
	private Store mStore;
	private ApplicationDataFacade mApplicationDataFacade;
	private View mTabDescription, mTabExtra, mTabLine, mViewDescription;
	private FrameLayout mLayoutExtra;

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
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STORE, mStore);
	}

	private void populateViews() {
		TextView phoneTextView = (TextView)findViewById(R.id.txt_details_phone);
		String phone = mStore.getPhone();
		if(phone == null || phone.length() < 3)
			phoneTextView.setVisibility(View.GONE);
		else
			phoneTextView.setText(phone);
		TextView websiteTextView = (TextView)findViewById(R.id.txt_details_website);
		String website = mStore.getWebsite();
		if(website == null || website.length() == 0)
			websiteTextView.setVisibility(View.GONE);
		else
			websiteTextView.setText(website);
		((TextView)findViewById(R.id.txt_details_category)).setText(getString(mStore.getCategory().getTitleRes()));
		((TextView)findViewById(R.id.txt_details_level)).setText(mApplicationDataFacade.getLevelInformation().getTitle(mStore.getLevel()));
		((TextView)(mViewDescription = findViewById(R.id.txt_details_description))).setText(mStore.getDescription());

		phoneTextView.setOnClickListener(onPhoneClickListener);
		websiteTextView.setOnClickListener(onWebsiteClickListener);
		findViewById(R.id.txt_problemas).setOnClickListener(onProblemasClickListener);

		mTabDescription = findViewById(R.id.tab_description);
		mTabDescription.bringToFront();

		mLayoutExtra = (FrameLayout) findViewById(R.id.layout_details_extra);
		mTabExtra = findViewById(R.id.tab_extra);
		mTabLine = findViewById(R.id.view_tab_line);

		String extra = mStore.getExtra();
		if(extra != null && extra.length() > 0) {
			try {
				mExtraFragment = (ExtraFragment) Class.forName(extra).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(mExtraFragment != null) {
				mLayoutExtra = (FrameLayout) findViewById(R.id.layout_details_extra);
				getSupportFragmentManager().beginTransaction().add(R.id.layout_details_extra, mExtraFragment).commit();
				if(mExtraFragment.isReady())
					onExtraReadyChangeListener.onReadyChanged(true);
				mExtraFragment.setOnReadyChangeListener(onExtraReadyChangeListener);
			}
		}
	}

	private OnReadyChangeListener onExtraReadyChangeListener = new OnReadyChangeListener() {

		@Override
		public void onReadyChanged(boolean ready) {
			if(ready) {
				mTabDescription.setOnClickListener(onTabClickListener);
				mTabExtra.setOnClickListener(onTabClickListener);
				mTabExtra.setVisibility(View.VISIBLE);
				TextView textTabExtra = (TextView) mTabExtra;
				textTabExtra.setText(mExtraFragment.getTitle());
				textTabExtra.setCompoundDrawablesWithIntrinsicBounds(mExtraFragment.getIconResId(), 0, 0, 0);
			} else {
				onTabClickListener.onClick(mTabDescription);
				mTabDescription.setOnClickListener(null);
				mTabExtra.setOnClickListener(null);
				mTabExtra.setVisibility(View.INVISIBLE);
			}
		}
	};

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

		case R.id.menu_problemas:
			onProblemasClickListener.onClick(null);
			break;

		case R.id.menu_show_on_map:
			showOnMap();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

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

	private OnClickListener onProblemasClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DialogFragment dialog = new ProblemasDialogFragment();
			Bundle args = new Bundle();
			args.putSerializable(ProblemasDialogFragment.STORE_KEY, mStore);
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "ProblemasDialogFragment");
		}
	};

	private OnClickListener onTabClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mTabLine.bringToFront();
			v.bringToFront();
			if(v == mTabDescription) {
				mViewDescription.setVisibility(View.VISIBLE);
				mLayoutExtra.setVisibility(View.GONE);
			} else {
				mViewDescription.setVisibility(View.GONE);
				mLayoutExtra.setVisibility(View.VISIBLE);
			}
			((View) v.getParent()).invalidate();
		}
	};
	private ExtraFragment mExtraFragment;

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

	public void showOnMap() {
		if(mStore.getLevel() >= 3) {// XXX Take it off when maps L2 and L3 are ready
			Toast.makeText(StoreDetailsActivity.this, String.format(getString(R.string.msg_mapanaodisponivel), mApplicationDataFacade.getLevelInformation().getTitle(mStore.getLevel())), Toast.LENGTH_SHORT).show();
			return;
		}
		Intent i = new Intent(StoreDetailsActivity.this, MainActivity.class);
		i.putExtra(MainActivity.SHOW_STORE_INMAP, mStore);
		startActivity(i);
	}
}
