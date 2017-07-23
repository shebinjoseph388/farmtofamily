package com.farmtofamily.ecommerce;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
//import android.widget.CheckBox;

public class ActivityCheckout extends FragmentActivity {
	
	Button btnSend;
	static Button btnDate;
	static Button btnTime;
	EditText edtName,edtLName,edtApart,edtName2, edtAddr1,edtPhone, edtOrderList, edtComment, edtAlamat, edtEmail, edtKota, edtProvinsi,editCoupon;
	ScrollView sclDetail;
	ProgressBar prgLoading;
	TextView txtAlert,tvCoupon,tvtotal,tvdisamt,tvreward,tvrewardbal;
	Spinner spinner;
	String custmid,wallet,walletres;
	String date,time1;
	// declare dbhelper object
	static DBHelper dbhelper;
	ArrayList<ArrayList<Object>> data;
	
	// declare string variables to store data
	String Name,Lname, Name2,Date, Time, Phone, Date_n_Time, Alamat, apart,Email, Kota, Provinsi;
	String couponname,couponcode,type,datestart,dateend;
	int couponid=0,usestoatal,usescustomer,status;

	double discount=0.0,walletamt=0.0,walletamt1=0.0;
	String Addr1="";
	String couponresult;
	String OrderList = "";
	String Comment = "";
	static int i;
	Boolean apply=false;

	// declare static int variables to store date and time
	private static int mYear;
	private static int mMonth;
	private static int mDay;
	private static int mHour;
	private static int mMinute;

	AlertDialog alertDialog;

	Date time;
	
	// declare static variables to store tax and currency data
	static double Tax,points=0,reward=0;
	static String Currency;


	static String servertime;
	int day,month;
	static final String TIME_DIALOG_ID = "timePicker";
	static final String DATE_DIALOG_ID = "datePicker";

	static ArrayList<Integer> Menu_ID = new ArrayList<Integer>();
	static ArrayList<String> Menu_name = new ArrayList<String>();
	static ArrayList<Integer> Quantity = new ArrayList<Integer>();
	static ArrayList<Double> Sub_total_price = new ArrayList<Double>();
	double Total_price;
	double Total_price1,Total_price2=0.0;
	public static final String TIME_SERVER = "time-a.nist.gov";


	// create price format
	DecimalFormat formatData = new DecimalFormat("#.##");

	String Result,couponres;
	String orderid;
	String NewCouponAPI;
	int IOConnect = 0;
	int menuid,quant;
	String name,code;
	double tot;
	SessionManager mSessionManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);
		if (!UserAuth.isUserLoggedIn()) {
			// finish();
			callLogin();
			return;
		}
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Checkout");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
		mSessionManager = SessionManager.getInstance(getApplicationContext());
		tvCoupon=(TextView)findViewById(R.id.tvCoupon);
		tvrewardbal=(TextView)findViewById(R.id.tvrewardbal);
		tvtotal=(TextView)findViewById(R.id.tvtotal);
		tvdisamt=(TextView)findViewById(R.id.tvdisamt);
        edtName = (EditText) findViewById(R.id.edtName);
		edtLName=(EditText) findViewById(R.id.edtLName);
        edtName2 = (EditText) findViewById(R.id.edtName2);
		edtAddr1 = (EditText) findViewById(R.id.edtAddress2);
		editCoupon =(EditText) findViewById(R.id.editCoupon);
		edtEmail = (EditText) findViewById(R.id.edtEmail);
        btnDate = (Button) findViewById(R.id.btnDate);
        //btnTime = (Button) findViewById(R.id.btnTime);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtOrderList = (EditText) findViewById(R.id.edtOrderList);
       // edtComment = (EditText) findViewById(R.id.edtComment);
        btnSend = (Button) findViewById(R.id.btnSend);
        sclDetail = (ScrollView) findViewById(R.id.sclDetail);
        prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
		tvdisamt = (TextView) findViewById(R.id.tvdisamt);
		edtAlamat = (EditText) findViewById(R.id.edtAlamat);
        edtKota = (EditText) findViewById(R.id.edtKota);
		edtApart = (EditText) findViewById(R.id.edtapart);
        edtProvinsi = (EditText) findViewById(R.id.edtProvinsi);//post code


		edtName.setText(mSessionManager.getUserFName());
		edtLName.setText(mSessionManager.getUserLName());
		edtAlamat.setText(mSessionManager.getUserAddr1());
		edtAddr1.setText(mSessionManager.getUserAddr2());
		edtEmail.setText(mSessionManager.getUserEmailId());
		edtPhone.setText(mSessionManager.getUserPhone());
		edtKota.setText(mSessionManager.getUserCity());
		edtProvinsi.setText(mSessionManager.getUserCode());
		custmid = mSessionManager.getUserId();
		wallet=Constant.WalletAPI+"?custid="+custmid;
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	             R.array.shipping_array, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     spinner.setAdapter(adapter);
	     
	     spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			 @Override
			 public void onItemSelected(AdapterView<?> arg0, View arg1,
										int arg2, long arg3) {
				 // TODO Auto-generated method stub

				 switch (arg2) {

					 case 0:
						 edtName2.setText("COD");
						 break;


					 default:
						 edtName2.setText("COD"); //COD
						 break;
				 }
			 }

			 @Override
			 public void onNothingSelected(AdapterView<?> arg0) {
				 // TODO Auto-generated method stub

			 }
		 });


		//new GetTimeFromNetwork().execute();
		Log.d("p1", "" + mSessionManager.getUserId());
		dbhelper = new DBHelper(this);
        // open database
		try{
			dbhelper.openDataBase();
		}catch(SQLException sqle){
			throw sqle;
		}
		new getDataTask().execute();

        
        // event listener to handle date button when pressed
        btnDate.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show date picker dialog
				DialogFragment newFragment = new DatePickerFragment();
			    newFragment.show(getSupportFragmentManager(), DATE_DIALOG_ID);
			}
		});
        
        // event listener to handle time button when pressed
       /* btnTime.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// show time picker dialog
				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getSupportFragmentManager(), TIME_DIALOG_ID);
				//new GetTimeFromNetwork().execute();


			}
		});*/
		tvCoupon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				code=editCoupon.getText().toString();
				// COUPON API url

				if(code.equalsIgnoreCase("Your Wallet is Empty")){
					code="000";
				}
				else{
					code=String.valueOf(walletamt1);
				}

				NewCouponAPI = Constant.CouponAPI+"?code="+code+"&custid="+custmid;
				Log.d("codee",""+code+" "+custmid+" "+NewCouponAPI);
				NewCouponAPI = Constant.CouponAPI+"?code="+code+"&custid="+custmid;
				getDataFromDatabase();
				Log.d("codee1", "" + Total_price1);
				if(code.isEmpty()){
					Log.d("codee2", "" + Total_price1);
					tvtotal.setText(String.valueOf(Total_price1));
					tvdisamt.setText(0);
				}
				else{
					Log.d("codee3", "" + Total_price1);
					new getCouponData().execute();
					//double fin=Total_price1-disamount;
					//tvtotal.setText(String.valueOf());
					//tvdisamt.setText(0);
				}
			}
		});
		Log.d("p2", "" + mSessionManager.getUserId());
        // event listener to handle send button when pressed
        btnSend.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d("timee",""+mHour);
			//boolean b=	takeOrder(mHour);

				// get data from all forms and send to server
				Name = edtName.getText().toString();
				Lname =edtLName.getText().toString();
				Alamat = edtAlamat.getText().toString();
				Addr1 = edtAddr1.getText().toString();
				Log.d("p4", "" + Name);
				Log.d("p5", "" + Lname);Log.d("p6", "" + Alamat);
				Kota = edtKota.getText().toString();
				Provinsi = edtProvinsi.getText().toString();
				Email = edtEmail.getText().toString();
				Name2 = edtName2.getText().toString();
				Date = btnDate.getText().toString();
				//Time = btnTime.getText().toString();
				Phone = edtPhone.getText().toString();
				//Comment = edtComment.getText().toString();
				//Date_n_Time = Date+" "+Time;
				Date_n_Time = Date;

				if(Name.equalsIgnoreCase("") ||Lname.equalsIgnoreCase("") || Name2.equalsIgnoreCase("") || Email.equalsIgnoreCase("") || Alamat.equalsIgnoreCase("") || Kota.equalsIgnoreCase("") || Provinsi.equalsIgnoreCase("") ||
						Date.equalsIgnoreCase(getString(R.string.date)) ||
						//Time.equalsIgnoreCase(getString(R.string.time)) ||
						Phone.equalsIgnoreCase("")){
					Toast.makeText(ActivityCheckout.this, R.string.form_alert, Toast.LENGTH_SHORT).show();
				}else if((data.size() == 0)){
					Toast.makeText(ActivityCheckout.this, R.string.order_alert, Toast.LENGTH_SHORT).show();
				/*}else 	if(b==false){
					//Toast.makeText(ActivityCheckout.this, "Our shop has closed", Toast.LENGTH_SHORT).show();*/
				}
				//	new sendData().execute();
else{
					if(!(Date.equalsIgnoreCase(getString(R.string.date)))){
						new GetTimeFromNetwork().execute();
					}

				}
			}
		});
		Log.d("p3", "" + mSessionManager.getUserId());
    }
	// method to get data from server
	public void insert1(){

		/*Total_price = 0;
		clearData();
		data = dbhelper.getAllData();

		// store data to arraylist variables
		for(i=0;i<data.size();i++){
			ArrayList<Object> row = data.get(i);
Log.d("ivaluee",""+i);
			menuid=quant=0;tot=0.0;name="";
			menuid = Integer.parseInt(row.get(0).toString());
			name=row.get(1).toString();
			quant = Integer.parseInt(row.get(2).toString());
			tot=Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString())));
			//Total_price += Sub_total_price.get(i);
Log.d("checkk", "" + menuid + name + quant + tot);
			*//*sendDataUser gc[i] = new sendDataUser();
			gc[i].execute();*//*
			new sendDataUser().execute();
		}*/
		new sendDataUser().execute();

		// count total order
		Total_price -= (Total_price * (Tax/100));
		Total_price = Double.parseDouble(formatData.format(Total_price));
	}
	public void callLogin() {
		Toast.makeText(getApplicationContext(),"Please Register with us",Toast.LENGTH_SHORT);
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
		loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(loginIntent);
		finish();
	}
	void clearData(){
		Menu_ID.clear();
		Menu_name.clear();
		Quantity.clear();
		Sub_total_price.clear();
	}
	public void insert(){

		Total_price = 0;
		//clearData();
		data = dbhelper.getAllData();

		// store data to arraylist variables
		for(int i=0;i<data.size();i++){
			ArrayList<Object> row = data.get(i);
			clearData();
			Menu_ID.add(Integer.parseInt(row.get(0).toString()));
			Menu_name.add(row.get(1).toString());
			Quantity.add(Integer.parseInt(row.get(2).toString()));
			Sub_total_price.add(Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString()))));
			Total_price += Sub_total_price.get(i);
			new sendData().execute();

		}

		// count total order
		Total_price -= (Total_price * (Tax/100));
		Total_price = Double.parseDouble(formatData.format(Total_price));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			
		case android.R.id.home:
            // app icon in action bar clicked; go home
        	this.finish();
        	overridePendingTransition(R.anim.open_main, R.anim.close_next);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    // method to create date picker dialog
    public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// set default date
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// get selected date
			mYear = year;
			mMonth = month;
			mDay = day;
			
			// show selected date to date button
			btnDate.setText(new StringBuilder()
					.append(mYear).append("-")
					.append(mMonth + 1).append("-")
					.append(mDay).append(" "));

		}
    }
    
    // method to create time picker dialog
    public static class TimePickerFragment extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// set default time
			final Calendar c = Calendar.getInstance();
	        int hour = c.get(Calendar.HOUR_OF_DAY);
	        int minute = c.get(Calendar.MINUTE);
			int f=Calendar.AM_PM;
			
			// Create a new instance of DatePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
	                DateFormat.is24HourFormat(getActivity()));

		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// get selected time
			mHour = hourOfDay;
			String hr = (String.valueOf(mHour));
			mMinute = minute;
		//	takeOrder(mhour)
			// show selected time to time button
			btnTime.setText(new StringBuilder()
            .append(pad(mHour)).append(":")
            .append(pad(mMinute)).append(":")
            .append("00")); 	
		}
    }

    // asynctask class to handle parsing json in background
    public class getCouponData extends AsyncTask<Void, Void, Void>{
    	
    	// show progressbar first
		getCouponData(){
	 		if(!prgLoading.isShown()){
	 			prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
	 		}
	 	}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			couponres=parseJSONDataCoupon();
			Log.d("couponres2",""+couponres);
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
 			prgLoading.setVisibility(8);
 			// if internet connection and data available request menu data from server
 			// otherwise, show alert text

			if(couponres.equalsIgnoreCase(" Invalid Coupon")){
				Toast.makeText(getApplicationContext(),"Invalid Coupon Code",Toast.LENGTH_LONG).show();
			}
			else{
				try {
					Log.d("couponres1",""+couponres);
					JSONObject json = new JSONObject(couponres);
					//Log.d("iddd1",""+json);
					//String id=json.getString("coupon_id");
					JSONArray data=json.getJSONArray("data");
					//Log.d("iddd2",""+id);
					for (int i = 0; i < data.length(); i++) {
						JSONObject object = data.getJSONObject(i);

						//JSONObject menu = object.getJSONObject("code");
						//reward+=menu.getDouble("amount");
						reward=object.getDouble("code");
						/*couponid=menu.getInt("coupon_id");
						couponname=menu.getString("name");
						code=menu.getString("code");
						type=menu.getString("type");
						discount=menu.getDouble("discount");
						datestart=menu.getString("date_start");
						dateend=menu.getString("date_end");
						usestoatal=menu.getInt("uses_total");
						usescustomer=menu.getInt("uses_customer");
						status=menu.getInt("status");*/

					}
					Log.d("reward",""+reward);
					//Log.d("discountt",""+discount);
					//Log.d("couponidd",""+couponid);
					//Log.d("couponname",""+couponname);

					if(Total_price1 > reward){

					Total_price1=Total_price1-reward;
						Total_price2=-reward;
						points=0;
						discount=reward;
						reward =0;

					}
					else if(Total_price1 == reward){
						Total_price2=-Total_price1;
						Total_price1=0;
						points=0;
						discount=reward;
						reward=0;

					}
					else{
						points=reward-Total_price1;
						discount=Total_price1;
						Total_price2=-Total_price1;
						Total_price1=0;
						reward=reward-discount;


					}

					tvdisamt.setText(String.format("%.2f", discount));
					tvtotal.setText(String.format("%.2f", Total_price1));
					tvrewardbal.setText(String.format("%.2f", reward));
					apply=true;
				}catch (JSONException e){
					e.printStackTrace();
				}
			}

			if(IOConnect == 0){
				//new getDataTask().execute();
			}else{
				txtAlert.setVisibility(0);
			}
		}
    }
	public String parseJSONDataCoupon(){

		try {
			// request data from tax and currency API
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
			HttpUriRequest request = new HttpGet(NewCouponAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();


			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

			String line;
			String str = "";
			while ((line = in.readLine()) != null){
				str += line;
			}
			Log.d("couponData",""+str);
			int b1=str.compareToIgnoreCase(" Invalid Coupon");
			boolean b=str.equalsIgnoreCase(" Invalid Coupon");
			Log.d("boolee",""+b);
			Log.d("boolee1",""+b1);
			if(str.equalsIgnoreCase(" Invalid Coupon")){
				//Toast.makeText(getApplicationContext(),"Invalid Coupon",Toast.LENGTH_LONG);
				couponresult=str;
				Log.d("couponcheck1",""+couponresult);

			}
			else {
				// parse json data and store into tax and currency variables
				JSONObject json = new JSONObject(str);
				//JSONArray data = json.getJSONArray("hour"); // this is the "items: [ ] part
				couponresult = str;
				//servertime=data.getString(0);
				Log.d("couponData2", "" + couponresult);
		/*	JSONObject object_tax = data.getJSONObject(0);
			JSONObject tax = object_tax.getJSONObject("tax_n_currency");

			Tax = Double.parseDouble(tax.getString("Value"));*/

			/*JSONObject object_currency = data.getJSONObject(1);
			JSONObject currency = object_currency.getJSONObject("tax_n_currency");

			Currency = currency.getString("Value");*/
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
		Log.d("couponcheck",""+couponresult);
		return couponresult;
	}
    // method to parse json data from server
	public void parseJSONDataTax(){
	
		try {
			// request data from tax and currency API
	        HttpClient client = new DefaultHttpClient();
	        HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
	        HttpUriRequest request = new HttpGet(Constant.TimeAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();
	
			
			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
		        
	        String line;
	        String str = "";
	        while ((line = in.readLine()) != null){
	        	str += line;
	        }
			Log.d("servertime1",""+str);
	        // parse json data and store into tax and currency variables
			JSONObject json = new JSONObject(str);
			//JSONArray data = json.getJSONArray("hour"); // this is the "items: [ ] part
			servertime =json.getString("hour");
			day =Integer.valueOf(json.getString("day"));
			month=Integer.valueOf(json.getString("month"));

			//servertime=data.getString(0);
			Log.d("servertime",""+servertime);
		/*	JSONObject object_tax = data.getJSONObject(0);
			JSONObject tax = object_tax.getJSONObject("tax_n_currency");
			    
			Tax = Double.parseDouble(tax.getString("Value"));*/
				   
			/*JSONObject object_currency = data.getJSONObject(1);
			JSONObject currency = object_currency.getJSONObject("tax_n_currency");
				    
			Currency = currency.getString("Value");*/
					
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
		//return Integer.valueOf(servertime);
	}
	
	// asynctask class to get data from database in background
    public class getDataTask extends AsyncTask<Void, Void, Void>{
    	
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			getDataFromDatabase();
			walletres=parseJSONData();
			return null;
		}
    	
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// hide progressbar and show reservation form
			prgLoading.setVisibility(8);
			sclDetail.setVisibility(0);

			if(walletres.equalsIgnoreCase(" No data")){
				editCoupon.setText("Your Wallet is Empty");
			}
			else{
				try {
					Log.d("walletnres1",""+walletres);
					JSONObject json = new JSONObject(walletres);
					//Log.d("iddd1",""+json);
					//String id=json.getString("coupon_id");
					JSONArray data=json.getJSONArray("data");
					//Log.d("iddd2",""+id);
					for (int i = 0; i < data.length(); i++) {
						JSONObject object = data.getJSONObject(i);

						//JSONObject menu = object.getJSONObject("code");
						//walletamt1 += menu.getDouble("amount");
						//walletamt += menu.getDouble("amount");
						walletamt1 += object.getDouble("code");
						walletamt += object.getDouble("code");
						/*couponid=menu.getInt("coupon_id");
						couponname=menu.getString("name");
						code=menu.getString("code");
						type=menu.getString("type");
						discount=menu.getDouble("discount");
						datestart=menu.getString("date_start");
						dateend=menu.getString("date_end");
						usestoatal=menu.getInt("uses_total");
						usescustomer=menu.getInt("uses_customer");
						status=menu.getInt("status");*/

					}
					Log.d("reward",""+walletamt+" Rs");
					//Log.d("discountt",""+discount);
					//Log.d("couponidd",""+couponid);
					//Log.d("couponname",""+couponname);
					editCoupon.setText(String.format( "%.2f", walletamt ));
					tvtotal.setText(String.format("%.2f", Total_price1)+" Rs");
					tvdisamt.setText("0.00 Rs");
					/*if(walletamt ){
					tvreward.setText(String.valueOf(walletamt));}*/
				}catch (JSONException e){
					e.printStackTrace();
				}
			}


			
		}
    }
	public String parseJSONData(){
		String result="";
		try {
			// request data from menu detail API
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
			HttpUriRequest request = new HttpGet(wallet);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();


			BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

			String line;
			String str = "";
			while ((line = in.readLine()) != null) {
				str += line;
			}
			Log.d("strrm1", "" + str);
			if (str.equalsIgnoreCase(" No data")) {
				//Toast.makeText(getApplicationContext(),"Invalid Coupon",Toast.LENGTH_LONG);
				result = str;
				Log.d("couponcheck1", "" + couponresult);

			} else {
				// parse json data and store into tax and currency variables
				/*JSONObject json = new JSONObject(str);
				JSONArray data = json.getJSONArray("data"); // this is the "items: [ ] part

				for (int i = 0; i < data.length(); i++) {
					JSONObject object = data.getJSONObject(i);

					JSONObject menu = object.getJSONObject("Menu_detail");
					Log.d("walleet", "" + menu.getDouble("points"));
					walletamt = menu.getInt("wallet");

				}
*/
				result = str;
			}
			}catch(MalformedURLException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(IOException e){
				// TODO Auto-generated catch block
				IOConnect = 1;
				e.printStackTrace();
			}

		return result;
	}

	// asynctask class to send data to server in background
	public class sendDataUser extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;

		// show progress dialog
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog= ProgressDialog.show(ActivityCheckout.this, "",
					getString(R.string.sending_alert), true);

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// send data to server and store result to variable
/*
			Result = getRequest(Name,Lname, Alamat, Kota, Provinsi, Email, Name2, Date_n_Time, Phone, OrderList,Comment);
*/Log.d("checkk1", "" + menuid + name + quant + Total_price1+couponid+discount+reward);
			Log.d("orderiddd","orderid");
/*
			orderid = getRequest2(couponid,discount,custmid, Name, Lname, Alamat, Addr1, Kota, Provinsi, Email, Name2, Phone, Total_price1,Date_n_Time);
*/
			orderid = getRequest2(reward,custmid, Name, Lname, Alamat, Addr1, Kota, Provinsi, Email, Name2, Phone, Total_price1,Total_price2,Date_n_Time,OrderList);

			Log.d("orderidd",""+orderid);
			clearData();
			data = dbhelper.getAllData();
			Total_price = 0;
			// store data to arraylist variables
			for(i=0;i<data.size();i++) {
				ArrayList<Object> row = data.get(i);
				Log.d("ivaluee", "" + i);
				menuid = quant = 0;
				tot = 0.0;
				name = "";
				menuid = Integer.parseInt(row.get(0).toString());
				name = row.get(1).toString();
				quant = Integer.parseInt(row.get(2).toString());
				tot = Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString())));
				//Total_price += Sub_total_price.get(i);
				Log.d("checkk", "" + menuid + name + quant + tot);
			/*sendDataUser gc[i] = new sendDataUser();
			gc[i].execute();*/


				//Result = getRequest1(custmid, Name, Lname, Alamat, Addr1, Kota, Provinsi, Email, Name2, Phone, name, menuid, quant, tot);
				Result = getRequest1(orderid,Email,Phone, name, menuid, quant, tot);

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// if finish, dismis progress dialog and show toast message
			/*if (dialog.isShowing()){
				dialog.dismiss();
			}*/
			try {
				if ((this.dialog != null) && this.dialog.isShowing()) {
					this.dialog.dismiss();
				}
			} catch (final IllegalArgumentException e) {
				// Handle or log or ignore
			} catch (final Exception e) {
				// Handle or log or ignore
			} finally {
				this.dialog = null;
			}
			resultAlert(Result);


		}
	}
    
    // asynctask class to send data to server in background
    public class sendData extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;
		
		// show progress dialog
		@Override
		 protected void onPreExecute() {
		  // TODO Auto-generated method stub
			 dialog= ProgressDialog.show(ActivityCheckout.this, "", 
	                 getString(R.string.sending_alert), true);
		  	
		 }

		 @Override
		 protected Void doInBackground(Void... params) {
		  // TODO Auto-generated method stub
			 // send data to server and store result to variable
			 Result = getRequest(Name, Lname, Alamat, Kota, Provinsi, Email, Name2, Date_n_Time, Phone, OrderList, Comment);
		  return null;
		 }

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// if finish, dismis progress dialog and show toast message
			dialog.dismiss();
			resultAlert(Result);
			
			
		}
	}

    // method to show toast message
    public void resultAlert(String HasilProses){
		//HasilProses="OK";
		Log.d("HasilProses", HasilProses);
		if(HasilProses.trim().equalsIgnoreCase("OK")){
			Toast.makeText(ActivityCheckout.this, R.string.ok_alert, Toast.LENGTH_SHORT).show();
			Intent i = new Intent(ActivityCheckout.this, ordertrack.class);
			startActivity(i);
			overridePendingTransition (R.anim.open_next, R.anim.close_next);
			finish();
		}else if(HasilProses.trim().equalsIgnoreCase("Failed")){
			Toast.makeText(ActivityCheckout.this, R.string.failed_alert, Toast.LENGTH_SHORT).show();
		}else{
			Log.d("HasilProses", HasilProses);
		}
	}
	public String  getRequest1(String orderid,String email,String phone, String Menu_name,int Menu_ID,int Quantity,double Total_price){
		String result = "";

		HttpClient client = new DefaultHttpClient();
		//custid=mSessionManager.getUserId();
		HttpPost request = new HttpPost(Constant.SendItemDataAPI);
	Log.d("valuees",""+orderid+" "+phone+" "+Menu_name+" "+quant+" "+tot);
		try{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
			nameValuePairs.add(new BasicNameValuePair("orderid",orderid));
			/*nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("lname", Lname));
			nameValuePairs.add(new BasicNameValuePair("alamat", alamat));//addr1
			nameValuePairs.add(new BasicNameValuePair("addr1", Addr1));//addr2
			nameValuePairs.add(new BasicNameValuePair("kota", kota));//city
			nameValuePairs.add(new BasicNameValuePair("provinsi", provinsi));//post code*/
			nameValuePairs.add(new BasicNameValuePair("email", email));
			//nameValuePairs.add(new BasicNameValuePair("name2", name2));
			//nameValuePairs.add(new BasicNameValuePair("date_n_time", date_n_time));
			nameValuePairs.add(new BasicNameValuePair("phone", phone));
			nameValuePairs.add(new BasicNameValuePair("Menu_Name", Menu_name));
			nameValuePairs.add(new BasicNameValuePair("Menu_id", Integer.toString(Menu_ID)));
			nameValuePairs.add(new BasicNameValuePair("Quantity", Integer.toString(Quantity)));
			nameValuePairs.add(new BasicNameValuePair("Total_Price", Double.toString(Total_price)));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			HttpResponse response = client.execute(request);
			result = request(response);
		}catch(Exception ex){
			result = "Unable to connect.";
		}
		Log.d("resulty",""+result);
		return result;
	}
	public String  getRequest2(double disc,String custid,String name, String Lname,String alamat, String Addr1,String kota, String provinsi, String email, String name2, String phone,double Total_price,double Total_pricee,String date,String orderlst){
		String result = "";

		HttpClient client = new DefaultHttpClient();
		custid=mSessionManager.getUserId();
		HttpPost request = new HttpPost(Constant.SendDataAPI);
		Log.d("rewardp",""+disc);
		Log.d("valuees1",""+""+apply+""+" "+disc+" "+custid+" "+name+" "+Lname+" "+alamat+" "+Addr1+" "+kota+" "+provinsi+" "+phone+" "+Total_price+" "+Total_pricee+" "+date);
		try{
			List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>(16);
			nameValuePairs1.add(new BasicNameValuePair("custid",custid));
			nameValuePairs1.add(new BasicNameValuePair("name", name));
			nameValuePairs1.add(new BasicNameValuePair("lname", Lname));
			nameValuePairs1.add(new BasicNameValuePair("alamat", alamat));//addr1
			nameValuePairs1.add(new BasicNameValuePair("addr1", Addr1));//addr2
			nameValuePairs1.add(new BasicNameValuePair("kota", kota));//city
			nameValuePairs1.add(new BasicNameValuePair("provinsi", provinsi));//post code
			nameValuePairs1.add(new BasicNameValuePair("email", email));
			nameValuePairs1.add(new BasicNameValuePair("name2", name2));
			nameValuePairs1.add(new BasicNameValuePair("order_list", orderlst));
			nameValuePairs1.add(new BasicNameValuePair("date_n_time", date));
			nameValuePairs1.add(new BasicNameValuePair("phone", phone));
			nameValuePairs1.add(new BasicNameValuePair("apply", String.valueOf(apply)));
			//nameValuePairs1.add(new BasicNameValuePair("couponid", String.valueOf(couponid)));
			nameValuePairs1.add(new BasicNameValuePair("discount", Double.toString(disc)));
			nameValuePairs1.add(new BasicNameValuePair("Total_Price", Double.toString(Total_price)));
			nameValuePairs1.add(new BasicNameValuePair("Total_Pricee", Double.toString(Total_pricee)));
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs1,HTTP.UTF_8));
			HttpResponse response = client.execute(request);
			Log.d("orderidresponse",""+response);
			result = request(response);
		}catch(Exception ex){
			result = "Unable to connect.";
		}
		Log.d("orderid",""+result);
		return result;
	}
    // method to post data to server
	public String getRequest(String name, String Lname,String alamat, String kota, String provinsi, String email, String name2, String date_n_time, String phone, String orderlist, String comment){
		String result = "";
		
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Constant.SendDataAPI);
        
        try{
        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
        	nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("lname", Lname));
        	nameValuePairs.add(new BasicNameValuePair("alamat", alamat));
        	nameValuePairs.add(new BasicNameValuePair("kota", kota));
        	nameValuePairs.add(new BasicNameValuePair("provinsi", provinsi));
        	nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("name2", name2));
            nameValuePairs.add(new BasicNameValuePair("date_n_time", date_n_time));
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("order_list", orderlist));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
        	HttpResponse response = client.execute(request);
            result = request(response);
        }catch(Exception ex){
        	result = "Unable to connect.";
        }
        return result;
     }

	public static String request(HttpResponse response){
	    String result = "";
	    try{
	        InputStream in = response.getEntity().getContent();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        StringBuilder str = new StringBuilder();
	        String line = null;
	        while((line = reader.readLine()) != null){
	            str.append(line + "\n");
	        }
			Log.d("REturnvalue",""+str);
	        in.close();
	        result = str.toString();
	    }catch(Exception ex){
	        result = "Error";
	    }
	    return result;
	}

	// method to get data from database
    public void getDataFromDatabase(){
    	
    	data = dbhelper.getAllData();

    	double Order_price = 0;
    	double Total_price = 0;
    	double tax = 0;
    	
    	// store all data to variables
    	for(int i=0;i<data.size();i++){
    		ArrayList<Object> row = data.get(i);
    		
    		String Menu_name = row.get(1).toString();
    		String Quantity = row.get(2).toString();
    		double Sub_total_price = Double.parseDouble(formatData.format(Double.parseDouble(row.get(3).toString())));
    		Order_price += Sub_total_price;
    		//Total_price1+=Order_price;
    		// calculate order price
    		//OrderList += (Quantity+" "+Menu_name+" "+Sub_total_price+" "+Currency+",\n");
			OrderList += ("Item Name : "+Menu_name+" "+"Quantity: "+Quantity+" Total Amount :"+String.format( "%.2f",Sub_total_price)+",\n");
    	}
    	
    	if(OrderList.equalsIgnoreCase("")){
    		OrderList += getString(R.string.no_order_menu);
    	}
    	
    	tax = Double.parseDouble(formatData.format(Order_price *(Tax /100)));
    	Total_price = Double.parseDouble(formatData.format(Order_price - tax));
		Total_price1 = Double.parseDouble(formatData.format(Order_price));
Log.d("Total_price1", "" + Total_price1);
		OrderList += "\nOrder: "+String.format( "%.2f",Order_price)+" "+"Rs"+
    			//"\nTax: "+Tax+"%: "+tax+" "+Currency+
    			"\nTotal: "+String.format( "%.2f", Total_price )+" "+"Rs";
    	edtOrderList.setText(OrderList);
    }
    
    // method to format date
    private static String pad(int c) {
        if (c >= 10){
             return String.valueOf(c);
        }else{
        	return "0" + String.valueOf(c);
        }
    }

    // when back button pressed close database and back to previous page
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	dbhelper.close();
    	finish();
    	overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
	public class GetTimeFromNetwork extends AsyncTask<String, Void, String> {

		ProgressDialog progressDialog;
		NTPUDPClient timeClient;
		InetAddress inetAddress;
		TimeInfo timeInfo;
		//   Date time;
		GetTimeFromNetwork(){
			if(!prgLoading.isShown()){
				prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
			}
		}
		/*@Override
		protected void onPreExecute() {

			progressDialog = new ProgressDialog(ActivityCheckout.this);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			Log.d("pre execute", "yes");

		}*/


		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d("do in back", "Executing");
			parseJSONDataTax();

			/*try {
				timeClient = new NTPUDPClient();
				inetAddress = InetAddress.getByName(TIME_SERVER);
				timeInfo = timeClient.getTime(inetAddress);
				//long returnTime = timeInfo.getReturnTime();   //local device time
				long returnTime = timeInfo.getMessage().getTransmitTimeStamp()
						.getTime(); //server time
				time = new Date(returnTime);
				int timeTemp = time.getHours();
				int date = time.getDate();
				Log.d("timeee", time.toString());

				String sendHours = String.valueOf(timeTemp);
				String senddate =String.valueOf(date);
				String res=sendHours+" "+senddate;
				Log.d("timeee1", sendHours);
				Log.d("dateee", senddate);
				*//*ArrayList<Object> dataList = new ArrayList<Object>();

				dataList.add(senddate);
				dataList.add(sendHours);*//*
				Log.d("hour", sendHours);
				return res;
				//return  dataList;
			} catch (Exception e) {
				// TODO: handle exception
				Log.e("error", e.getMessage());
			}*/
			return servertime;
		}

		@Override
		protected void onPostExecute(String result) {
			//Log.d("time1", result);
			super.onPostExecute(result);
			prgLoading.setVisibility(8);
			//timeShow.setText(result);
		Boolean time=	takeOrder(Integer.valueOf(result));
			if(time == false){
				/*finish();
				Intent checkoutIntent = new Intent(getApplicationContext(), ActivityCheckout.class);
				startActivity(checkoutIntent);*/


			}
			else{
				insert1();
			}


		}
	}
	public boolean selfPermissionGranted(String permission) {
		// For Android < Android M, self permissions are always granted.
		boolean result = true;
		int targetSdkVersion=0;

		try {
			final PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0);
			targetSdkVersion = info.applicationInfo.targetSdkVersion;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		if (Build.VERSION.SDK_INT >= 23) {

			if (targetSdkVersion >= 23) {
				// targetSdkVersion >= Android M, we can
				// use Context#checkSelfPermission
				//result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
			} else {
				// targetSdkVersion < Android M, we have to use PermissionChecker
				//result = PermissionChecker.checkSelfPermission(getApplicationContext(), permission) == PermissionChecker.PERMISSION_GRANTED;
			}
		}

		return result;
	}
	public boolean takeOrder(int endTime) {

		/*String[] ary = endTime.split(" ");

		time1 = ary[0];
		date=ary[1];
		Log.d("timeee11", date);
		Log.d("dateee1", time1);

		int time = Integer.parseInt(time1);*/
		//int time = Integer.parseInt(servertime);
		int time = endTime;
		Log.d("mMonth",""+mMonth+ " "+month+" "+mDay+" "+day+" "+time);
		if((mMonth+1) < month){
			Toast.makeText(ActivityCheckout.this,"Please select a later date for delivery",Toast.LENGTH_LONG).show();
			final AlertDialog.Builder alertd = new AlertDialog.Builder(this);
			alertd.setMessage("Please select a later date for delivery");
			alertd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (alertDialog != null && alertDialog.isShowing()) {
						alertDialog.dismiss();
					}
					Intent checkoutIntent = new Intent(getApplicationContext(), ActivityCheckout.class);
					startActivity(checkoutIntent);


				}
			});

			alertd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelDialog();
				}
			});

			alertDialog = alertd.create();
			alertDialog.show();
			sclDetail.setVisibility(0);

			return false;


		}
		else if(mDay <= day){
			Toast.makeText(ActivityCheckout.this,"Please select a later date for delivery",Toast.LENGTH_LONG).show();
			final AlertDialog.Builder alertd = new AlertDialog.Builder(this);
			alertd.setMessage("Please select a later date for delivery");
			alertd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (alertDialog != null && alertDialog.isShowing()) {
						alertDialog.dismiss();
					}
					Intent checkoutIntent = new Intent(getApplicationContext(), ActivityCheckout.class);
					startActivity(checkoutIntent);


				}
			});

			alertd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelDialog();
				}
			});

			alertDialog = alertd.create();
			alertDialog.show();
			sclDetail.setVisibility(0);

			return false;


		}
		else if (((day+1)== mDay)&&(time >= 18)) {
			Toast.makeText(ActivityCheckout.this, " We have closed orders for the day", Toast.LENGTH_LONG).show();
			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

			alertDialogBuilder.setMessage("Sorry, we have closed orders for the day. Please call/whatsapp 96638 93077 to check availability");

			alertDialogBuilder.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					if (alertDialog != null && alertDialog.isShowing()) {
						alertDialog.dismiss();
					}
					//Toast.makeText(OrderTimeCheck.this, "You clicked yes button", Toast.LENGTH_LONG).show();
					Intent phoneIntent = new Intent(Intent.ACTION_CALL);
					phoneIntent.setData(Uri.parse("tel:+919663893077"));
					/*if (ActivityCompat.checkSelfPermission(ActivityCheckout.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}*/
					startActivity(phoneIntent);
					/*Intent loginIntent = new Intent(getApplicationContext(), ActivityCart.class);
					loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(loginIntent);
					finish();*/
				}
			});

			alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelDialog();
				}
			});

			alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			sclDetail.setVisibility(0);
			return false;

		}else{
			/*DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getSupportFragmentManager(), TIME_DIALOG_ID);*/
			/*btnTime.setText(time1);
			btnDate.setText(date);*/
			sclDetail.setVisibility(0);
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		try {
			if (alertDialog != null && alertDialog.isShowing()) {
				alertDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	public void cancelDialog() {
		alertDialog.dismiss();
	}
    @Override
	public void onConfigurationChanged(final Configuration newConfig)
	{
		// Ignore orientation change to keep activity from restarting
		super.onConfigurationChanged(newConfig);
	}
}
