package com.farmtofamily.ecommerce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
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
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityMenuDetail extends Activity {
	
	ImageView imgPreview;
	TextView txtText, txtSubText;
	WebView txtDescription;
	Button btnAdd,btn_plus, btn_minus, btn_number;
	ScrollView sclDetail;
	ProgressBar prgLoading;
	TextView txtAlert;
	TextView tv_cart_notif;
	TextView txtback;
	ArrayList<ArrayList<Object>> data;
	TextView checkout;
	// declare dbhelper object
	static DBHelper dbhelper;
	int n;
	
	// declare ImageLoader object
	ImageLoader imageLoader;
	
	// declare variables to store menu data
	String Menu_image,Menu_serve, Menu_name, Menu_description;
	int Menu_serve1;
	double Menu_price;
	double Menu_tax;
	int Menu_quantity;
	double Menu_weight;
	long Menu_ID;
	String MenuDetailAPI;
	int IOConnect = 0;
	int value = 0;
	SessionManager mSessionManager;
	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");
	ImageView iv_head_cart;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_detail);
		mSessionManager = SessionManager.getInstance(getApplicationContext());

		if (!UserAuth.isUserLoggedIn()) {
			// finish();
			Toast.makeText(getApplicationContext(),"Please Register with us",Toast.LENGTH_SHORT);

			callLogin();
			return;
		}
        /*ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Detail Menu");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);*/
		ActionBar actionBar = getActionBar();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
//displaying custom ActionBar
		View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Product Details");
		actionBar.setDisplayShowHomeEnabled(true);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        txtText = (TextView) findViewById(R.id.txtText);
        txtSubText = (TextView) findViewById(R.id.txtSubText);
		checkout = (TextView) findViewById(R.id.txtcheckout);

		txtDescription = (WebView) findViewById(R.id.txtDescription);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        //btnShare = (Button) findViewById(R.id.btnShare);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
		btn_minus = (Button) findViewById(R.id.btn_minus);
		btn_plus = (Button) findViewById(R.id.btn_plus);
		btn_number = (Button) findViewById(R.id.btn_number);
		iv_head_cart =(ImageView) findViewById(R.id.iv_head_cart);
		tv_cart_notif =(TextView) findViewById(R.id.tv_cart_notif);
		txtback =(TextView) findViewById(R.id.txtback);
		// get screen device width and height
        DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int wPix = dm.widthPixels;
		int hPix = wPix / 2 + 50;
		//Log.d("came1", "came");
		// change menu image width and height
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
        imgPreview.setLayoutParams(lp);

        imageLoader = new ImageLoader(ActivityMenuDetail.this);
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

		// get menu id that sent from previous page
        Intent iGet = getIntent();
        Menu_ID = iGet.getLongExtra("menu_id", 0);

        // Menu detail API url
        MenuDetailAPI = Constant.MenuDetailAPI+"?accesskey="+Constant.AccessKey+"&menu_id="+Menu_ID;

        // call asynctask class to request data from server
        new getDataTask().execute();      
        
        // event listener to handle add button when clicked
        btnAdd.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// show input dialog
				inputDialog();
			}
		});
		txtback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				finish();

			}

		});
		checkout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent iMyOrder = new Intent(ActivityMenuDetail.this, ActivityCart.class);
				startActivity(iMyOrder);
				overridePendingTransition(R.anim.open_next, R.anim.close_next);

			}

		});
		btn_number.setText("" + value);
		iv_head_cart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// refresh action
				Intent iMyOrder = new Intent(ActivityMenuDetail.this, ActivityCart.class);
				startActivity(iMyOrder);
				overridePendingTransition (R.anim.open_next, R.anim.close_next);


			}
		});
		// number of person(minus button)
		btn_minus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (value <= 0) {
					value = 0;
					btn_number.setText("" + value);
				} else {
					value--;
					btn_number.setText("" + value);
				}

			}
		});

		// number of person(add button)
		btn_plus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btn_number.setError(null);
				if((value+1)<=Menu_quantity)
				value++;

				btn_number.setText("" + value);
			}
		});
        
    }
	public void callLogin() {
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(loginIntent);
		finish();
	}
	public void updateCart() {


		data = dbhelper.getAllData();
		n=data.size();
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
		//getMenuInflater().inflate(R.menu.menu_detail, menu);
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
			Intent iMyOrder = new Intent(ActivityMenuDetail.this, ActivityCart.class);
			startActivity(iMyOrder);
			overridePendingTransition (R.anim.open_next, R.anim.close_next);
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
	void inputDialog(){

		// open database first
		try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
Log.d("Valueval",""+value);
				int quantity = 0;

				// when add button clicked add menu to order table in database
				if(value != 0){
					btn_number.setError(null);
					quantity = value;
					if(dbhelper.isDataExist(Menu_ID)){
						dbhelper.updateData(Menu_ID, quantity, (Menu_price*quantity));
					}else{
/*
    	    			dbhelper.addData(Menu_ID, Menu_name, Menu_tax,quantity, (Menu_price*quantity));
*/
						dbhelper.addData(Menu_ID, Menu_name, Menu_tax,quantity, (Menu_price*quantity));

					}
					updateCart();
				}else{
					btn_number.setError("Enter Number of items");
				}




	}
    // method to show number of order form
    /*void inputDialog(){
    	
    	// open database first
    	try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	
    	alert.setTitle(R.string.order);
    	alert.setMessage(R.string.number_order);
    	alert.setCancelable(false);
    	final EditText edtQuantity = new EditText(this);
    	int maxLength = 3;    
    	edtQuantity.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    	edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
    	alert.setView(edtQuantity);
    	
    	alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		String temp = edtQuantity.getText().toString();
    		int quantity = 0;
    		
    		// when add button clicked add menu to order table in database
    		if(!temp.equalsIgnoreCase("")){
    			quantity = Integer.parseInt(temp);
    			if(dbhelper.isDataExist(Menu_ID)){
    	        		dbhelper.updateData(Menu_ID, quantity, (Menu_price*quantity));
    	    		}else{
*//*
    	    			dbhelper.addData(Menu_ID, Menu_name, Menu_tax,quantity, (Menu_price*quantity));
*//*
					dbhelper.addData(Menu_ID, Menu_name, Menu_tax,quantity, (Menu_price*quantity));

				}
    		}else{
    			dialog.cancel();
    		}       	  		
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {

      			// when cancel button clicked close dialog
    		  	dialog.cancel();
    	  }
    	});

    	alert.show();
    }*/
    
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
			parseJSONData();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(8);

			// if internet connection and data available show data
			// otherwise, show alert text
			if((Menu_name != null) && IOConnect == 0){
				sclDetail.setVisibility(0);
			
				imageLoader.DisplayImage(Constant.AdminPageURL + Menu_image, imgPreview);
				
				txtText.setText(Menu_name);
			//	txtSubText.setText("Price : " +Menu_price+" "+ActivityMenuList.Currency+"\n"+"Status : "+Menu_serve+"\n"+"Stock : "+Menu_quantity);
				txtSubText.setText("Price : " + String.format( " %.2f", Menu_price ) + " " + "Rs" + "\n" + "Status : " + Menu_serve + "\n" + "Stock : " + Menu_quantity + "\n" + "Weight : " + Menu_weight + " " + "gram");
				/*txtDescription.postDelayed(new Runnable() {
					@Override
					public void run() {
						txtDescription.loadDataWithBaseURL(null, Menu_description, "text/html", "UTF-8", null);
						txtDescription.setBackgroundColor(Color.parseColor("#e7e7e7"));

					}
				}, 100);*/
				/*txtDescription.getSettings().setLoadWithOverviewMode(true);
				txtDescription.getSettings().setUseWideViewPort(true);
				txtDescription.getSettings().setSupportZoom(true);
				txtDescription.getSettings().setBuiltInZoomControls(true);*/
				String formattedString=android.text.Html.fromHtml(Menu_description).toString();
				txtDescription.loadDataWithBaseURL(null, formattedString, "text/html", "UTF-8","about:blank");
		       txtDescription.setBackgroundColor(Color.parseColor("#e7e7e7"));
			}else{
				txtAlert.setVisibility(0);
			}
		}
    }

    // method to parse json data from server
    public void parseJSONData(){
    	
    	try {
    		// request data from menu detail API
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
	        HttpUriRequest request = new HttpGet(MenuDetailAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();

			
			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
		        
	        String line;
	        String str = "";
	        while ((line = in.readLine()) != null){
	        	str += line;
	        }
			Log.d("strrm", "" + str);
        
	        // parse json data and store into tax and currency variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part
				
			for (int i = 0; i < data.length(); i++) {
			    JSONObject object = data.getJSONObject(i); 
			    
			    JSONObject menu = object.getJSONObject("Menu_detail");
				Log.d("Menu_weightt",""+menu.getDouble("weight"));
				Log.d("Menu_quantity",""+menu.getDouble("quantity"));

				Menu_image = menu.getString("image");
			    Menu_name = menu.getString("name");
				Log.d("Menu_name",""+Menu_name);
				Log.d("Menu_descr",""+menu.getString("description"));
				Menu_weight = menu.getDouble("weight");
				Menu_quantity = menu.getInt("quantity");
				Menu_description = menu.getString("description");
				/*Menu_description=Menu_description.replace("<p>","");
				Menu_description=Menu_description.replace("</p>","");*/
				Log.d("Menu_status",""+menu.getString("description"));

				Log.d("Menu_status", "" + menu.getString("status"));
				Log.d("Menu_status1",""+menu.getInt("status"));
				Menu_price = Double.valueOf(formatData.format(menu.getDouble("price")));
				//Menu_tax = Double.valueOf(formatData.format(menu.getDouble("tax")));
				Menu_tax=0.0;
			   Menu_serve1 = menu.getInt("status");
				if(Menu_serve1==1){Menu_serve="Available";
				} else{Menu_serve="Out Of Stock";}

				Log.d("Menu_weighttt",""+Menu_weight);
				Log.d("Menu_quantityy",""+Menu_quantity);

				    
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

	
    // close database before back to previous page
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	dbhelper.close();
    	finish();
    	overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	//imageLoader.clearCache();
    	super.onDestroy();
    }
	@Override
	protected void onResume() {
		super.onResume();
		/*Log.d("calling9", "called");
		if (dbhelper.isPreviousDataExist()) {
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
    
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
	    // Ignore orientation change to keep activity from restarting
	    super.onConfigurationChanged(newConfig);
	}
    
    
}
