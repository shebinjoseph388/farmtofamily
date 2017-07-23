package com.farmtofamily.ecommerce;

/**
 * Created by shebin on 9/16/2016.
 */



        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.AsyncTask;

        import android.provider.Settings;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLEncoder;
        import java.util.ArrayList;

public class ordertrack extends AppCompatActivity {

    private ArrayList<MyPojo> arrayList = new ArrayList<MyPojo>();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public  RecyclerView.Adapter adapter;
    ProgressDialog progressDialog;
    String custmid;
    SessionManager mSessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_layout);
       /* ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Order History");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);*/
        if (!NetworkUtils.isActiveNetworkAvailable(this)) {
            if((Constant.isNetworkAvailable(this))== false){
                new AlertDialog.Builder(this)
                        .setTitle("Network Error")
                        .setMessage("Please check network connection")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // whatever...
                                dialog.dismiss();
                                startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                            }
                        }).create().show();
                finish();
                return;
            }
        }
        if (!UserAuth.isUserLoggedIn()) {
            // finish();
            callLogin();
            return;
        }
        mSessionManager = SessionManager.getInstance(getApplicationContext());

        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);


        OrderStatusTask orderStatusTask = new OrderStatusTask(getApplicationContext());
        orderStatusTask.execute();



    }
    public void callLogin() {
        Toast.makeText(getApplicationContext(), "Please Register with us", Toast.LENGTH_SHORT);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    public class OrderStatusTask extends AsyncTask<String,Void,String> {

        Context context;

        String JSON_STRING;
        MyPojo myPojo = null;
        String json_getUrl;

        public OrderStatusTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            json_getUrl = Constant.TrackAPI;
            custmid = mSessionManager.getUserId();
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();


        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("line_85",json_getUrl);


            try {
                URL orderResponse = new URL(json_getUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) orderResponse.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));
               // String data = URLEncoder.encode("email","UTF-8") + "=" + URLEncoder.encode(email,"UTF-8")+"&"+
                     //   URLEncoder.encode("password","UTF-8") + "=" + URLEncoder.encode(password,"UTF-8");
                String data =URLEncoder.encode("post_code","UTF-8")+"="+URLEncoder.encode(custmid,"UTF-8");

                Log.d("fname",custmid);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();

                Log.d("IS","IS reached");
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS));
                Log.d("BR","BR reached");
                StringBuilder sb = new StringBuilder();

                while ((JSON_STRING = BR.readLine())!=null){
                    Log.d("JSON_STRING",""+JSON_STRING);
                    sb.append(JSON_STRING+"\n");
                }

                String jsn = sb.toString().trim();
                Log.d("jsn",jsn);

                BR.close();
                IS.close();

                httpURLConnection.disconnect();


                return  jsn;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
           // Log.d("onpre_res",result);
            JSONArray jsonArray = null;
            JSONObject jsonObject = null;
            JSONObject JO = null;

            try {
                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray("data");
                arrayList = new ArrayList<MyPojo>();
               /* for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.optJSONObject(i);
                   // JSONObject menu = post.getJSONObject("Menu");
                    MyPojo item = new MyPojo();
                    Log.d("Image1",post.getString("status"));
                    Log.d("Image2",post.getString("total"));
                    Log.d("Image3",post.getString("date"));
                    Log.d("Image4",post.getString("list"));
                   // Log.d("Image5",menu.getString("status_name"));
                    *//*item.setProd_image(post.optString("prod_image"));
                    item.setProd_name(post.optString("prod_name"));
                    item.setProd_price(post.optString("prod_price"));
                    item.setQuantity(post.optString("quantity"));
                    item.setStatus_name(post.getString("status_name"));*//*
                   // item.setProd_image(menu.getString("image"));
                    item.setProd_name(post.getString("list"));
                    //item.setProd_price(menu.getString("price"));
                    item.setOrder_price(post.getString("total"));
                   // item.setQuantity(menu.getString("quantity"));
                    item.setStatus_name(post.getString("status"));
                    item.setOdid(post.getString("id"));
                    item.setDate(post.getString("date"));
                    arrayList.add(item);
                }*/
               /* for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.optJSONObject(i);
                    JSONObject menu = post.getJSONObject("Menu");
                    MyPojo item = new MyPojo();
Log.d("Image1",menu.getString("image"));
                    Log.d("Image2",menu.getString("list"));
                    Log.d("Image3",menu.getString("price"));
                    Log.d("Image4",menu.getString("quantity"));
                    Log.d("Image5",menu.getString("status_name"));
                    *//*item.setProd_image(post.optString("prod_image"));
                    item.setProd_name(post.optString("prod_name"));
                    item.setProd_price(post.optString("prod_price"));
                    item.setQuantity(post.optString("quantity"));
                    item.setStatus_name(post.getString("status_name"));*//*
                    item.setProd_image(menu.getString("image"));
                    item.setProd_name(menu.getString("name"));
                    item.setProd_price(menu.getString("price"));
                    item.setOrder_price(menu.getString("tot_amt"));
                    item.setQuantity(menu.getString("quantity"));
                    item.setStatus_name(menu.getString("status_name"));
                    item.setOdid(menu.getString("order_id"));
                    item.setDate(menu.getString("date"));
                    arrayList.add(item);
                }*/
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.optJSONObject(i);
                    JSONObject menu = post.getJSONObject("Menu");
                    MyPojo item = new MyPojo();

                    Log.d("Image1",menu.getString("status_name"));
                    Log.d("Image2",menu.getString("tot_amt"));
                    Log.d("Image3",menu.getString("order_id"));
                    Log.d("Image4", menu.getString("date"));
                    Log.d("Image5", menu.getString("list"));
                    item.setProd_name(menu.optString("list"));

                    item.setStatus_name(menu.getString("status_name"));



                    item.setOrder_price(menu.getString("tot_amt"));


                    item.setOdid(menu.getString("order_id"));
                    item.setDate(menu.getString("date"));
                    arrayList.add(item);
                }
                adapter = new OrderRecyclerAdapter(getApplicationContext(),arrayList);
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

           /* int count=0;
            while (count < jsonArray.length())
            {

                try {
                    JO = jsonArray.getJSONObject(count);
                    myPojo = new MyPojo(JO.getString("image"),JO.getString("name"),
                            JO.getString("price"),JO.getString("quantity"),JO.getString("status_name"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                count++;

            }
*/

        }
    }

}
