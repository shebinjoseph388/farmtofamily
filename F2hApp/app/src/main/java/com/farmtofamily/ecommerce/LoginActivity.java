package com.farmtofamily.ecommerce;

        import android.app.AlertDialog;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;

        import android.content.Intent;
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

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    String JSON_STRING;
    EditText resetEmail, resetPhone;
    SessionManager mSessionManager;

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;


    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.link_skip)TextView _skipLink;
    @Bind(R.id.forgot_password)TextView _forgotPasswordLink;
   /* @Bind(R.id.password_reset_email)EditText _passwordResetEmail;
    @Bind(R.id.password_reset_phone)EditText _passwordResetPhone;*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSessionManager = SessionManager.getInstance(getApplicationContext());
        if (UserAuth.isUserLoggedIn()) {
            callLogin();

            return;
        }

        ButterKnife.bind(this);
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
           }
        }
        resetEmail = (EditText) findViewById(R.id.password_reset_email);
        resetPhone = (EditText) findViewById(R.id.password_reset_phone);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
        // Skip Register code will follow here
        _skipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.forgot_password_layout, null);

                dialogBuilder.setView(dialogView);

         //       final EditText resetEmail = (EditText) dialogView.findViewById(R.id.reset_pass_email);
//                final Button resetBtn = (Button) findViewById(R.id.reset_pass_btn);

                dialogBuilder.setTitle("Password Reset");
                dialogBuilder.setMessage("Enter registered email id and Phone number");
                dialogBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do something with edt.getText().toString();
                        String resetEmails = resetEmail.getText().toString();
                        String resetPhones = resetPhone.getText().toString();
                    }
                });
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();

               */

                startActivity(new Intent(LoginActivity.this, ResetPassword.class));
            }
        });
    }
    public void callLogin() {
        Toast.makeText(getApplicationContext(), "Please Register with us", Toast.LENGTH_SHORT);
        Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(true);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        LoginAsync task = new LoginAsync();
        task.execute(email, password);



       /* new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                // this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        this.finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

        return valid;
    }

    public class LoginAsync extends AsyncTask<String,Void,String> {

        String json_getUrl;

        @Override
        protected void onPreExecute() {
            json_getUrl = Constant.LoginAPI;
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d("line_60",json_getUrl);
            try {
                String email = params[0];
                String password = params[1];

                URL loginURL = new URL(json_getUrl);
                Log.d("loginURL","loginURL reached");
                HttpURLConnection httpURLConnection = (HttpURLConnection) loginURL.openConnection();
                Log.d("httpconn","httpconn reached");

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(OS,"UTF-8"));

                String data = URLEncoder.encode("email","UTF-8") + "=" + URLEncoder.encode(email,"UTF-8")+"&"+
                        URLEncoder.encode("password","UTF-8") + "=" + URLEncoder.encode(password,"UTF-8");

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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            Log.d("result",""+result);
            JSONObject jsonObject;
            JSONArray jsonArray;
            UserDbDTO u= new UserDbDTO();

            if(!result.equals("no records found")) {
                try{

                jsonObject = new JSONObject(result);
                jsonArray = jsonObject.getJSONArray("profile_details");
                    Log.d("jsonArrayy", "" +jsonArray);

                    for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject post = jsonArray.optJSONObject(i);
                    u = new UserDbDTO();

                    u.setFname(post.getString("firstname"));
                    Log.d("setFname", "" +post.getString("firstname"));
                    u.setLname(post.getString("lastname"));
                    u.setEmail(post.getString("email"));
                    u.setPhone(post.getString("phone"));
                    u.setAddr1(post.getString("addr1"));
                    u.setAddr2(post.getString("addr2"));
                        u.setApart(post.getString("apart"));
                    u.setCity(post.getString("city"));
                    u.setCode(post.getString("post_code"));
                    u.setUserid(post.getString("cust_id"));
                } } catch (JSONException e) {
                    e.printStackTrace();
                }
                UserAuth userAuth = new UserAuth();
                userAuth.saveAuthenticationInfo(u, getApplicationContext());

               // mSessionManager=SessionManager.getInstance(getApplicationContext());
                Log.d("SessionFname", "" + u.getFname());
                mSessionManager.setUserFName(u.getFname());

                Intent dash = new Intent(getApplicationContext(), MainActivity.class);
                dash.putExtra("result",result);
                startActivity(dash);
                finish();

            }else{
                Toast.makeText(LoginActivity.this,"Wrong Email/Password",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
