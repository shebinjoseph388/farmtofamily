package com.farmtofamily.ecommerce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivityCategoryList extends FragmentActivity {
	
	ListView listCategory;
	ProgressBar prgLoading;
	TextView txtAlert;
	TextView tv_cart_notif;
	static DBHelper dbhelper;
	// declare adapter object to create custom category list
	AdapterCategoryList cla;
	ImageView iv_head_cart;
	
	// create arraylist variables to store data from server
	static ArrayList<Long> Category_ID = new ArrayList<Long>();
	static ArrayList<String> Category_name = new ArrayList<String>();
	static ArrayList<String> Category_image = new ArrayList<String>();
	ArrayList<ArrayList<Object>> data;
	
	String CategoryAPI;
	int IOConnect = 0,n;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);
       /*
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle("Category");
     */
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

		prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        listCategory = (ListView) findViewById(R.id.listCategory);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
		tv_cart_notif =(TextView) findViewById(R.id.tv_cart_notif);

		cla = new AdapterCategoryList(ActivityCategoryList.this);
		Log.d("calling12","calling1");
		iv_head_cart =(ImageView) findViewById(R.id.iv_head_cart);

		dbhelper = new DBHelper(this);

		// open database
		try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
		if (dbhelper.isPreviousDataExist()) {
			//showAlertDialog();
			updateCart();
		}
		// category API url
    	CategoryAPI = Constant.CategoryAPI+"?accesskey="+Constant.AccessKey;
        
        // call asynctask class to request data from server
		Log.d("calling13","calling1");
        new getDataTask().execute();
		iv_head_cart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// refresh action
				Intent iMyOrder = new Intent(ActivityCategoryList.this, ActivityCart.class);
				startActivity(iMyOrder);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);


			}
		});
        // event listener to handle list when clicked
		listCategory.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				// go to menu page
				Intent iMenuList = new Intent(ActivityCategoryList.this, ActivityMenuList.class);
				iMenuList.putExtra("category_id", Category_ID.get(position));
				iMenuList.putExtra("category_name", Category_name.get(position));
				startActivity(iMenuList);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);
			}
		});
        
    }
	public void updateCart() {


		data = dbhelper.getAllData();
		n=data.size();
		Log.d("nccc",""+n);
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_category, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.cart:
			// refresh action
			Intent iMyOrder = new Intent(ActivityCategoryList.this, ActivityCart.class);
			startActivity(iMyOrder);
			overridePendingTransition (R.anim.open_next, R.anim.close_next);
			return true;
			
		case R.id.refresh:
			IOConnect = 0;
			listCategory.invalidateViews();
			clearData();
			new getDataTask().execute();
			return true;
			
		case android.R.id.home:
            // app icon in action bar clicked; go home
        	this.finish();
        	overridePendingTransition(R.anim.open_main, R.anim.close_next);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    // clear arraylist variables before used
    void clearData(){
    	Category_ID.clear();
    	Category_name.clear();
    	Category_image.clear();
    }
    
    // asynctask class to handle parsing json in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
    	getDataTask(){
    		if(!prgLoading.isShown()){
    			prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
    		}
    	}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			Log.d("calling","calling");
			parseJSONData();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(8);
			// if internet connection and data available show data on list
			// otherwise, show alert text
			if((Category_ID.size() > 0) && (IOConnect == 0)){
				listCategory.setVisibility(0);
				listCategory.setAdapter(cla);
			}else{
				txtAlert.setVisibility(0);
			}
		}
    }
    
    // method to parse json data from server
    public void parseJSONData(){
    	
    	clearData();
    	
    	try {
    		// request data from Category API
	        HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
	        HttpUriRequest request = new HttpGet(CategoryAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();
			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
		
	        String line;
	        String str = "";
	        while ((line = in.readLine()) != null){
	        	str += line;
	        }
			Log.d("dataa",""+str);
	        // parse json data and store into arraylist variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data");
			Log.d("dataa",""+data);

			for (int i = 0; i < data.length(); i++) {
			    JSONObject object = data.getJSONObject(i); 
			    
			    JSONObject category = object.getJSONObject("Category");
			    
			   Category_ID.add(Long.parseLong(category.getString("category_id")));


				Category_name.add(category.getString("name"));
			    Category_image.add(category.getString("image"));
			    Log.d("Category name", Category_name.get(i));
				    
			}
				
				
		} catch (MalformedURLException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
			IOConnect = 1;
		    e.printStackTrace();
		} catch (JSONException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}	
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//cla.imageLoader.clearCache();
    	listCategory.setAdapter(null);
    	super.onDestroy();
    }

    
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	finish();
    	overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("calling9", "called");
		/*if (dbhelper.isPreviousDataExist()) {
			//showAlertDialog();
			updateCart();

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
