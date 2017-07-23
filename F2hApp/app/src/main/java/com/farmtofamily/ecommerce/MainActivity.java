package com.farmtofamily.ecommerce;

import java.io.IOException;
import java.util.ArrayList;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	ImageView iv_head_cart;
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private AdapterNavDrawerList adapter;
	ArrayList<ArrayList<Object>> data;
	// declare dbhelper and adapter object
	static DBHelper dbhelper;
	AdapterMainMenu mma;
	TextView tv_cart_notif;
	int n;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nav_drawer_main);

		// Parse push notification
		Parse.initialize(this, getString(R.string.parse_application_id), getString(R.string.parse_client_key));
		ParseAnalytics.trackAppOpened(getIntent());
		PushService.setDefaultPushCallback(this, MainActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		mDrawerLayout.setDrawerShadow(R.drawable.navigation_drawer_shadow, GravityCompat.START);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));

		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new AdapterNavDrawerList(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		/*getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		ActionBar bar = getActionBar();*/
		ActionBar actionBar = getActionBar();
		//getActionBar().setDisplayHomeAsUpEnabled(true);

//displaying custom ActionBar
		View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Category");
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));

		//bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
		tv_cart_notif =(TextView) findViewById(R.id.tv_cart_notif);
		iv_head_cart =(ImageView) findViewById(R.id.iv_head_cart);

		// get screen device width and height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// checking internet connection
		if (!Constant.isNetworkAvailable(MainActivity.this)) {
			Toast.makeText(MainActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
		}

		mma = new AdapterMainMenu(this);
		dbhelper = new DBHelper(this);

		// create database
		try {
			dbhelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		// then, the database will be open to use
		try {
			dbhelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		// if user has already ordered food previously then show confirm dialog
		if (dbhelper.isPreviousDataExist()) {
			//showAlertDialog();
			getDataFromDatabase();

		}

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav
																								// menu
																								// toggle
																								// icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		iv_head_cart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// refresh action
				Intent iMyOrder = new Intent(MainActivity.this, ActivityCart.class);
				startActivity(iMyOrder);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);


			}
		});
		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}
	public void getDataFromDatabase() {


		data = dbhelper.getAllData();
		n=data.size();
		Log.d("nvall", "" + n);
		if(n > 0) {


			cartNotifHandler.sendEmptyMessage(n);

		} else {

			cartNotifHandler.sendEmptyMessage(0);

		}


	}
	Handler cartNotifHandler = new Handler() {

		public void handleMessage(Message msg) {

			if(msg.what == 0) {

				tv_cart_notif.setVisibility(RelativeLayout.GONE);

			} else {

				tv_cart_notif.setText(msg.what + "");
				tv_cart_notif.setVisibility(RelativeLayout.VISIBLE);

			}

		};

	};
		// show confirm dialog to ask user to delete previous order or not
	void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm);
		builder.setMessage(getString(R.string.db_exist_alert));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// delete order data when yes button clicked
				dbhelper.deleteAllData();
				dbhelper.close();

			}
		});

		builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// close dialog when no button clicked
				dbhelper.close();
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}
		@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		dbhelper.deleteAllData();
		dbhelper.close();
		finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	/**
	 * Slide menu item click listener
	 */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.rate_app:
			/*try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
			}*/
			return true;
		case R.id.more_app:
			//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.more_apps))));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.ic_menu).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new Activity_Home1();
			break;
		/*case 1:
			startActivity(new Intent(getApplicationContext(), ActivityCategoryList.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);
			break;*/
		case 1:
			startActivity(new Intent(getApplicationContext(), ActivityCart.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);
			break;
		case 2:
			startActivity(new Intent(getApplicationContext(), ActivityCheckout.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);
			break;
		case 3:
			startActivity(new Intent(getApplicationContext(),Profile.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);
			break;
		case 4:
			startActivity(new Intent(getApplicationContext(), ordertrack.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);
			break;
		case 5:
			startActivity(new Intent(getApplicationContext(), ActivityAbout.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);

			break;
		case 6:
			Intent sendInt = new Intent(Intent.ACTION_SEND);
			sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
			/*sendInt.putExtra(Intent.EXTRA_TEXT, "E-Commerce Android App\n\"" + getString(R.string.app_name)
					+ "\" \nhttps://play.google.com/store/apps/details?id=" + getPackageName());*/
			sendInt.putExtra(Intent.EXTRA_TEXT, "E-Commerce Android App\n\"" + getString(R.string.app_name)
					);
			sendInt.setType("text/plain");
			startActivity(Intent.createChooser(sendInt, "Share"));
			break;
		case 7:
			startActivity(new Intent(getApplicationContext(), ActivityContactUs.class));
			overridePendingTransition(R.anim.open_next, R.anim.close_next);

			break;
		case 8:
			dbhelper.deleteAllData();
			dbhelper.close();
			try {
				UserAuth.CleanAuthenticationInfo();
				callLogin();
			} catch (Exception e) {

			}
			/*MainActivity.this.finish();
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
		*/	overridePendingTransition(R.anim.open_next, R.anim.close_next);

			break;

		default:
			break;
		}

		if (fragment != null) {
			android.app.FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}
	public void callLogin() {
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(loginIntent);
		finish();
	}
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("calling9","called");
		/*if (dbhelper.isPreviousDataExist()) {
			//showAlertDialog();
			getDataFromDatabase();

		}*/
		DBHelper dbhelper;
		ArrayList<ArrayList<Object>> data;
		dbhelper = new DBHelper(this);
		try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
		data = dbhelper.getAllData();
		n=data.size();
		if(n > 0) {


			cartNotifHandler.sendEmptyMessage(n);

		} else {

			cartNotifHandler.sendEmptyMessage(0);

		}
	}

}
