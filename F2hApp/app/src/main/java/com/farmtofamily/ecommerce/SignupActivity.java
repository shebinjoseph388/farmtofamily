package com.farmtofamily.ecommerce;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
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

        import butterknife.ButterKnife;
        import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    String JSON_STRING ="";
    ProgressDialog progressDialog;

    @Bind(R.id.input_fname) EditText _fnameText;
    @Bind(R.id.input_lname) EditText _lnameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_re_password)EditText _re_passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    @Bind(R.id.input_phone)EditText _phone;
    @Bind(R.id.addr1)EditText _addr1;
    @Bind(R.id.addr2)EditText _addr2;
    @Bind(R.id.apart)EditText _apart;
    @Bind(R.id.city)EditText _city;
    @Bind(R.id.post_code)EditText _postCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);



        String phone = _phone.getText().toString();
        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String rePassword = _re_passwordText.getText().toString();
        String addr1 = _addr1.getText().toString();
        String addr2 = _addr2.getText().toString();
        String apart = _apart.getText().toString();
        String city = _city.getText().toString();
        String postCode = _postCode.getText().toString();

        if(password.equals(rePassword)) {
            RegisterBackTask task = new RegisterBackTask(SignupActivity.this);
            task.execute(fname, lname, email.toLowerCase(), phone, password, addr1, addr2,apart, city, postCode);

        }else {
            progressDialog.dismiss();
            passwordMatch();
            _signupButton.setEnabled(true);
        }

       /* new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void passwordMatch(){
        Toast.makeText(SignupActivity.this,"Password mis-match",Toast.LENGTH_SHORT).show();
    }

    public void onSignupFailed() {

        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Plz enter all the fields correctly", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String fname = _fnameText.getText().toString();
        String lname = _lnameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String rePassword = _re_passwordText.getText().toString();
        String phone = _phone.getText().toString();
        String addr1 = _addr1.getText().toString();
        String addr2 = _addr2.getText().toString();
        String apart = _apart.getText().toString();
        String city = _city.getText().toString();
        String postCode = _postCode.getText().toString();

        if (fname.isEmpty() || fname.length() < 3) {
            _fnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _fnameText.setError(null);
        }

        if (lname.isEmpty() || lname.length() < 3) {
            _lnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _lnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (rePassword.isEmpty() || rePassword.length() < 4 || rePassword.length() > 10) {
            _re_passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _re_passwordText.setError(null);
        }

        if (phone.isEmpty() || phone.length() != 10) {
            _phone.setError("only 10 digits mobile number");
            valid = false;
        } else {
            _phone.setError(null);
        }

        if (addr1.isEmpty()) {
            _addr1.setError("Field is mandatory!!");
            valid = false;
        } else {
            _addr1.setError(null);
        }

        if (addr2.isEmpty()) {
            _addr2.setError("Field is mandatory!!");
            valid = false;
        } else {
            _addr2.setError(null);
        } if (apart.isEmpty()) {
            _apart.setError("Field is mandatory!!");
            valid = false;
        } else {
            _apart.setError(null);
        }

        if (city.isEmpty()) {
            _city.setError("Field is mandatory!!");
            valid = false;
        } else {
            _city.setError(null);
        }

        if (postCode.isEmpty()) {
            _postCode.setError("Postal Code is mandatory!!");
            valid = false;
        } else {
            _postCode.setError(null);
        }

        return valid;
    }


    public class RegisterBackTask extends AsyncTask<String,Void,String> {

        Context ctx;
        String response;
        String line;

        RegisterBackTask(Context ctx){

            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();

        }



        @Override
        protected String doInBackground(String... params) {


            String regUrl = Constant.RegAPI;




            String fname = params[0];
            String lname = params[1];
            String email = params[2];
            String phone = params[3];
            String password = params[4];
            String addr1 = params[5];
            String addr2 = params[6];
            String apart = params[7];
            String city = params[8];
            String postCode = params[9];


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
                        URLEncoder.encode("apart","UTF-8")+"="+URLEncoder.encode(apart,"UTF-8")+"&"+
                        URLEncoder.encode("city","UTF-8")+"="+URLEncoder.encode(city,"UTF-8")+"&"+
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

            if(result.equals("You are already registered!!")) {
                progressDialog.dismiss();
                _signupButton.setEnabled(true);
                Toast.makeText(SignupActivity.this,"You are already registered!!",Toast.LENGTH_SHORT).show();
                //    onSignupFailed();
            }else{
                Toast.makeText(ctx, "You can Login now", Toast.LENGTH_LONG).show();

                finish();
            }
        }
    }
}