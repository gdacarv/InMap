package com.contralabs.inmap.activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.contralabs.inmap.InMapApplication;
import com.contralabs.inmap.actionbar.ActionBarActivity;
import com.contralabs.inmap.fragments.ExtraFragment;
import com.contralabs.inmap.fragments.InfoDialogFragment;
import com.contralabs.inmap.fragments.LegalNoticesDialogFragment;
import com.contralabs.inmap.fragments.ProximityCheckDialogFragment;
import com.contralabs.inmap.fragments.ExtraFragment.OnReadyChangeListener;
import com.contralabs.inmap.interfaces.ApplicationDataFacade;
import com.contralabs.inmap.model.Store;
import com.google.analytics.tracking.android.EasyTracker;
import com.helpshift.Helpshift;
import com.contralabs.inmap.R;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class StoreDetailsActivity extends ActionBarActivity {

	private static final String SHOWING_EXTRA = "showingExtra";
	public static final String STORE = "store";
	public static final String SHOW_EXTRA = "com.contralabs.inmap.SHOW_EXTRA";
	private Store mStore;
	private ApplicationDataFacade mApplicationDataFacade;
	private View mTabDescription, mTabExtra, mTabLine, mViewDescription;
	private FrameLayout mLayoutExtra;
	private boolean mShowingExtra = false;
	private Helpshift mHelpshift;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_details);

		Intent intent = getIntent();
		if(savedInstanceState != null)
			mStore = (Store) savedInstanceState.getSerializable(STORE);
		else
			mStore = (Store) intent.getSerializableExtra(STORE);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			ActionBar actionBar = getActionBar();
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setBackgroundDrawable(new ColorDrawable(mStore.getCategory().getMenuColor()));
		}

		mApplicationDataFacade = ((InMapApplication)getApplication()).getApplicationDataFacade();
		setActionBarTitle("  " + mStore.getTitle());

		populateViews(savedInstanceState);

		EasyTracker.getInstance().setContext(getApplicationContext());
		EasyTracker.getTracker().sendEvent("UserAction", "StoreDetails", "StoreView", mStore.getId());
		
		ProximityCheckDialogFragment.showIfAppropriate(intent, getSupportFragmentManager());
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(STORE, mStore);
		outState.putBoolean(SHOWING_EXTRA, mShowingExtra);
	}

	private void populateViews(Bundle savedInstanceState) {
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
				if(savedInstanceState == null)
					mExtraFragment = (ExtraFragment) Class.forName(extra).newInstance();
				else
					mExtraFragment = (ExtraFragment) getSupportFragmentManager().findFragmentByTag("mExtraFragment");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if(mExtraFragment != null) {
				mLayoutExtra = (FrameLayout) findViewById(R.id.layout_details_extra);
				if(savedInstanceState == null) 
					getSupportFragmentManager().beginTransaction().add(R.id.layout_details_extra, mExtraFragment, "mExtraFragment").commit();
				if(mExtraFragment.isReady())
					onExtraReadyChangeListener.onReadyChanged(true);
				mExtraFragment.setOnReadyChangeListener(onExtraReadyChangeListener);
				if(savedInstanceState != null && savedInstanceState.getBoolean(SHOWING_EXTRA, false))
					onTabClickListener.onClick(mTabExtra);
			}

			String imageUrl = getImageUrl();
			if(imageUrl != null)
				UrlImageViewHelper.setUrlDrawable((ImageView) findViewById(R.id.img_details_logo), imageUrl, R.drawable.img_no_brands_descricao);
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
				if(getIntent().getBooleanExtra(SHOW_EXTRA, false))
					onTabClickListener.onClick(mTabExtra);
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

		case R.id.menu_sobre:
			new InfoDialogFragment().show(getSupportFragmentManager(), "InfoDialogFragment");
			break;

		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;

		case R.id.menu_legal_notices:
			new LegalNoticesDialogFragment().show(getSupportFragmentManager(), "LegalNoticesDialogFragment");
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
			/*DialogFragment dialog = new ProblemasDialogFragment();
			Bundle args = new Bundle();
			args.putSerializable(ProblemasDialogFragment.STORE_KEY, mStore);
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "ProblemasDialogFragment");*/
			if(mHelpshift == null) mHelpshift = new Helpshift(StoreDetailsActivity.this);
			mHelpshift.leaveBreadCrumb("StoreDetails Id " + mStore.getId() + " Name " + mStore.getName());
			mHelpshift.showSupport(StoreDetailsActivity.this);
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
				mShowingExtra = false;
			} else {
				mViewDescription.setVisibility(View.GONE);
				mLayoutExtra.setVisibility(View.VISIBLE);
				EasyTracker.getTracker().sendView(String.format(getString(R.string.view_detailsextra), mStore.getId()));
				mShowingExtra = true;
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
		/*if(mStore.getLevel() == 3) {
			Toast.makeText(StoreDetailsActivity.this, String.format(getString(R.string.msg_mapanaodisponivel), mApplicationDataFacade.getLevelInformation().getTitle(mStore.getLevel())), Toast.LENGTH_SHORT).show();
			return;
		}*/
		Intent i = new Intent(StoreDetailsActivity.this, MainActivity.class);
		i.putExtra(MainActivity.SHOW_STORE_INMAP, mStore);
		startActivity(i);
	}

	private String getImageUrl() {
		String string = getString(R.string.imgs_url);
		return String.format(string, mStore.getId());
	}
}
