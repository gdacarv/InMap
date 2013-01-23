package com.inmap.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.inmap.R;
import com.inmap.actionbar.ActionBarActivity;
import com.inmap.model.Store;

public class StoreDetailsActivity extends ActionBarActivity {

	public static final String STORE = "store";
	private Store mStore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_details);
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if(savedInstanceState != null)
			mStore = (Store) savedInstanceState.getSerializable(STORE);
		else
			mStore = (Store) getIntent().getSerializableExtra(STORE);
		
		setActionBarTitle(mStore.getTitle());
		populateViews();
	}

	private void populateViews() {
		((TextView)findViewById(R.id.txt_details_description)).setText(getString(R.string.descricao_) + " " + mStore.getDescription());
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
