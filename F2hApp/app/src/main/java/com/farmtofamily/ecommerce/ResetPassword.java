package com.farmtofamily.ecommerce;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import android.widget.Toast;

public class ResetPassword extends AppCompatActivity {

    EditText resetEmail, resetPhone;
    Button reset, cancel;
    String JSON_STRING;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_layout);

        resetEmail = (EditText) findViewById(R.id.password_reset_email);
        resetPhone  = (EditText) findViewById(R.id.password_reset_phone);
        reset = (Button) findViewById(R.id.reset_btn);
        cancel = (Button) findViewById(R.id.reset_cancel_btn);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = resetEmail.getText().toString().toLowerCase().trim();
                String phone = resetPhone.getText().toString().trim();

                ResetAsync resetTask = new ResetAsync(ResetPassword.this);
                resetTask.execute(email.toLowerCase(),phone);

            }
        });
    }

    public class ResetAsync extends AsyncTask<String,Void,String>{

        Context ctx;
        String resetUrl;
        ResetAsync(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            resetUrl = Constant.ResetAPI;
        }

        @Override
        protected String doInBackground(String... resetFields) {

            try {
                String email = resetFields[0];
                String phone = resetFields[1];

                URL loginURL = new URL(resetUrl);
                Log.d("resetURL","loginURL reached");
                HttpURLConnection httpURLConnection = (HttpURLConnection) loginURL.openConnection();
                Log.d("httpconn","httpconn reached");

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("email","UTF-8") + "=" + URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("phone","UTF-8") + "=" + URLEncoder.encode(phone,"UTF-8");

                BW.write(data);
                BW.flush();
                BW.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();

                Log.d("IS","IS reached");
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS));
                Log.d("ReachedBR","Reached");

                StringBuilder sb = new StringBuilder();
                Log.d("sb","sb created");
                while ((JSON_STRING = BR.readLine())!=null){
                    Log.d("returnnull","Reached");
                    sb.append(JSON_STRING+"\n");
                }
                Log.d("sbres",sb.toString());
                String jsn = sb.toString().trim();
                Log.d("jsn",jsn);
                BR.close();
                IS.close();

                httpURLConnection.disconnect();

                return  jsn;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();}

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result == "reset success"){
                Toast.makeText(getApplicationContext(),"Password has been sent your mail",Toast.LENGTH_LONG);
            }
            finish();


        }
    }
}
