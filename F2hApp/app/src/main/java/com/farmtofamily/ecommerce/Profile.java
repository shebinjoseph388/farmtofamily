package com.farmtofamily.ecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import butterknife.Bind;
import butterknife.ButterKnife;

public class Profile extends AppCompatActivity {
    private static final String TAG = "Profile";
    String JSON_STRING ="";
    ProgressDialog progressDialog;
    SessionManager mSessionManager;
    String custmid;
    String password;
    @Bind(R.id.input_fname_update) EditText _updateFname;
    @Bind(R.id.input_lname_update) EditText _updateLname;
    @Bind(R.id.input_email_update) EditText _updateEmailText;
    @Bind(R.id.input_password_update) EditText _updatePasswordText;
    @Bind(R.id.btn_update) Button _updateButton;
    @Bind(R.id.input_phone_update)EditText _updatePhone;
    @Bind(R.id.addr1_update)EditText _updateaddr1;
    @Bind(R.id.addr2_update)EditText _updateaddr2;
    @Bind(R.id.city_update)EditText _updateCity;
    @Bind(R.id.post_code_update)EditText _updatePostCode;
    @Bind(R.id.btn_edit_update)Button _editUpdate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (!UserAuth.isUserLoggedIn()) {
            // finish();
            callLogin();
            return;
        }
        ButterKnife.bind(this);
        mSessionManager = SessionManager.getInstance(getApplicationContext());

        _updateFname.setText(mSessionManager.getUserFName());
        _updateLname.setText(mSessionManager.getUserLName());
        _updateaddr1.setText(mSessionManager.getUserAddr1());
        _updateaddr2.setText(mSessionManager.getUserAddr2());
        _updateEmailText.setText(mSessionManager.getUserEmailId());
        _updatePhone.setText(mSessionManager.getUserPhone());
        _updateCity.setText(mSessionManager.getUserCity());
        _updatePostCode.setText(mSessionManager.getUserCode());
        custmid = mSessionManager.getUserId();
        _updateLname.setEnabled(false);
        _updateFname.setEnabled(false);
        _updateEmailText.setEnabled(false);
        _updatePasswordText.setEnabled(false);
        _updateButton.setEnabled(false);
        _updatePhone.setEnabled(false);
        _updateaddr1.setEnabled(false);
        _updateaddr2.setEnabled(false);
        _updateCity.setEnabled(false);
        _updatePostCode.setEnabled(false);  

        _editUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _editUpdate.setEnabled(false);
                _updateLname.setEnabled(true);
                _updateFname.setEnabled(true);
                _updateEmailText.setEnabled(true);
                _updatePasswordText.setEnabled(true);
                _updateButton.setEnabled(true);
                _updatePhone.setEnabled(true);
                _updateaddr1.setEnabled(true);
                _updateaddr2.setEnabled(true);
                _updateCity.setEnabled(true);
                _updatePostCode.setEnabled(true);
            }
        });

        progressDialog = new ProgressDialog(Profile.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Account...");

        _updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });


    }
    public void callLogin() {
        Toast.makeText(getApplicationContext(),"Please Register with us",Toast.LENGTH_SHORT);
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    public void update() {
        Log.d(TAG, "Update");

        if (!validate()) {
            Log.d(TAG, "Update");
            onUpdateFailed();
            return;
        }

        _updateButton.setEnabled(false);



        String phone = _updatePhone.getText().toString();
        String fname = _updateFname.getText().toString();
        String lname = _updateLname.getText().toString();
        String email = _updateEmailText.getText().toString();
        String password = _updatePasswordText.getText().toString();
        if(password.equals("")){
            Log.d("passwordd",""+password);
            password="00000000";
        }

        String addr1 = _updateaddr1.getText().toString();
        String addr2 = _updateaddr2.getText().toString();
        String city = _updateCity.getText().toString();
        String postCode = _updatePostCode.getText().toString();


            UpdateBackTask task = new UpdateBackTask(Profile.this);
            task.execute(fname, lname, email.toLowerCase(), phone, password, addr1, addr2, city, postCode);




    }


    public void onSignupSuccess() {
        _updateButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void passwordMatch(){
        Toast.makeText(Profile.this,"Password mis-match",Toast.LENGTH_SHORT).show();
    }

    public void onUpdateFailed() {

        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Plz enter all the fields correctly", Toast.LENGTH_LONG).show();
        _updateButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        Log.d("passwordd","coming");
        String fname = _updateFname.getText().toString();
        String lname = _updateLname.getText().toString();
        String email = _updateEmailText.getText().toString();
        password = _updatePasswordText.getText().toString();
        Log.d("passwordd",""+password);
        if(password.equals("")){
            Log.d("passwordd",""+password);
            password="00000000";
        }

        String phone = _updatePhone.getText().toString();
        String addr1 = _updateaddr1.getText().toString();
        String addr2 = _updateaddr2.getText().toString();
        String city = _updateCity.getText().toString();
        String postCode = _updatePostCode.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            _updateFname.setError("at least 3 characters");
            valid = false;
        } else {
            _updateFname.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            _updateLname.setError("at least 3 characters");
            valid = false;
        } else {
            _updateLname.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _updateEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            _updateEmailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _updatePasswordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _updatePasswordText.setError(null);
        }


        if (phone.isEmpty() || phone.length() != 10) {
            _updatePhone.setError("only 10 digits mobile number");
            valid = false;
        } else {
            _updatePhone.setError(null);
        }

        if (addr1.isEmpty()) {
            _updateaddr1.setError("Field is mandatory!!");
            valid = false;
        } else {
            _updateaddr1.setError(null);
        }

        if (addr2.isEmpty()) {
            _updateaddr2.setError("Field is mandatory!!");
            valid = false;
        } else {
            _updateaddr2.setError(null);
        }

        if (city.isEmpty()) {
            _updateCity.setError("Field is mandatory!!");
            valid = false;
        } else {
            _updateCity.setError(null);
        }

        if (postCode.isEmpty()) {
            _updatePostCode.setError("Postal Code is mandatory!!");
            valid = false;
        } else {
            _updatePostCode.setError(null);
        }

        return valid;
    }


    public class UpdateBackTask extends AsyncTask<String,Void,String> {

        Context ctx;
        String response;
        String line;

        UpdateBackTask(Context ctx){

            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();

        }



        @Override
        protected String doInBackground(String... params) {


        //    String regUrl = "http://www.farmztofamiliez.com/api/registration.php";
             String regUrl = Constant.UpdateAPI;
            custmid = mSessionManager.getUserId();


            String fname = params[0];
            String lname = params[1];
            String email = params[2];
            String phone = params[3];
            String password = params[4];
            String addr1 = params[5];
            String addr2 = params[6];
            String city = params[7];
            String postCode = params[8];

            UserDbDTO u= new UserDbDTO();
            u.setFname(fname);
            Log.d("setFname", "" + lname);
            u.setLname(lname);
            u.setEmail(email);
            u.setPhone(phone);
            u.setAddr1(addr1);
            u.setAddr2(addr2);
            u.setCity(city);
            u.setCode(postCode);
            u.setUserid(custmid);
            UserAuth userAuth = new UserAuth();
            userAuth.saveAuthenticationInfo(u, getApplicationContext());

            try {
                URL regurl = new URL(regUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) regurl.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("fname","UTF-8")+"="+URLEncoder.encode(fname,"UTF-8")+"&"+
                        URLEncoder.encode("lname","UTF-8")+"="+URLEncoder.encode(lname,"UTF-8")+"&"+
                        URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("phone","UTF-8")+"="+URLEncoder.encode(phone,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"+
                        URLEncoder.encode("addr1","UTF-8")+"="+URLEncoder.encode(addr1,"UTF-8")+"&"+
                        URLEncoder.encode("addr2","UTF-8")+"="+URLEncoder.encode(addr2,"UTF-8")+"&"+
                        URLEncoder.encode("city","UTF-8")+"="+URLEncoder.encode(city,"UTF-8")+"&"+
                        URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(custmid,"UTF-8")+"&"+
                        URLEncoder.encode("post_code","UTF-8")+"="+URLEncoder.encode(postCode,"UTF-8");

                Log.d("fname",fname);
                Log.d("lname",lname);
                Log.d("email",email);
                Log.d("phone",phone);
                Log.d("password",password);

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader BR = new BufferedReader(new InputStreamReader(IS));
                StringBuilder sb = new StringBuilder();
                while ((JSON_STRING = BR.readLine())!=null){

                    sb.append(JSON_STRING+"\n");
                    Log.d("returnnull",JSON_STRING);
                }
                IS.close();

                String successRegister = sb.toString().trim();
                Log.d("asd",successRegister);
                return successRegister;
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
            Log.d("result",""+result);

            if(result.equals("error")) {
                progressDialog.dismiss();
                _updateButton.setEnabled(true);
                Toast.makeText(Profile.this,"Something went wrong...",Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();


                finish();
            }
        }
    }
}
