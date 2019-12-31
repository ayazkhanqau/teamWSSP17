package com.zeeroapps.wssp.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.ForgotPassword;
import com.zeeroapps.wssp.R;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;
import com.zeeroapps.wssp.utils.SHA1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity implements View.OnClickListener {

    RelativeLayout mainLayout;
    EditText etPhone, etPass;
    Button btnLogin, btnHelp;
    AVLoadingIndicatorView avi;
    SharedPreferences sp;
    SharedPreferences.Editor spEdit;
    TextView register_text;

    private static final String TAG = "MyApp";
    private String tag_json_obj = "JSON_OBJECT";
    private String token;

    String passwordSHA1;
    private String phoneNo, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = this.getSharedPreferences(getString(R.string.sp), this.MODE_PRIVATE);
        spEdit = sp.edit();
        Fabric.with(this, new Crashlytics());
        initUIControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String scrName = "LOGIN SCREEN";
    }

    private void initUIControls() {
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPass = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnHelp = (Button) findViewById(R.id.btnHelp);
        avi = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        register_text = (TextView) findViewById(R.id.register_text);
        register_text.setPaintFlags(register_text.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        avi.hide();

        btnLogin.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
    }

    public void validateFields(){
        phoneNo = etPhone.getText().toString();
        password = etPass.getText().toString();

        if (TextUtils.isEmpty(phoneNo) || phoneNo.length() < 11){
            etPhone.requestFocus();
            etPhone.setError("Enter valid phone number!");
            return;
        }else if (password.length() < 5){
            etPass.requestFocus();
            etPass.setError("Password must be 5 characters long!");
            return;
        }
        avi.show();
        loginWS();
    }

    @Override
    public void onClick(View view) {
        int vID = view.getId();

        switch (vID) {
            case R.id.btnLogin:
//                Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
//                startActivity(intent);
//                finish();
                validateFields();
                break;
            case R.id.btnHelp:
                Intent intent = new Intent(LoginActivity.this, HelpActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void forgotPassword(View v){



       /* Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"admin@wssp.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Forgot my account password!");
        i.putExtra(Intent.EXTRA_TEXT   , "Kindly create a new password for me.");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LoginActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
*/
       /* String mailto = "mailto:ebtihaj@codeforpakistan.org" +
   "?cc=" + "nazimdin@codeforpakistan.org" +
   "&subject=" + Uri.encode("Forgot my WSSP account password!") +
   "&body=" + Uri.encode("Kindly create a new password for me.");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }*/

       Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
       startActivity(intent);

    }

    public void gotoRegister(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    public void loginWS() {
        try {
            passwordSHA1 = SHA1.encrypt(etPass.getText().toString());
            //Log.e(TAG, "loginWS: " + passwordSHA1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, response.toString());

                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    String status = jObj.getString("status");

                    Snackbar.make(mainLayout, status, Snackbar.LENGTH_LONG).show();
                    if (status.equals("Success")) {

                        int roll = Integer.parseInt(jObj.getString("roll"));
//                        //Log.d("ayaz",roll+"");
//                        if (roll != 0) {
//                            Snackbar.make(mainLayout, "Incorrect username or password!", Snackbar.LENGTH_LONG).show();
//                            avi.hide();
//                            return;
//                        }
                        getMemberDetailsWS();


                    }else{
                        Snackbar.make(mainLayout, "Incorrect username or password!", Snackbar.LENGTH_LONG).show();
                        avi.hide();
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(mainLayout, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mainLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }
                avi.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber", phoneNo);
                params.put("password", password);
                params.put("token_id", sp.getString("FB_TOKEN", null));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);
    }

    public void getMemberDetailsWS() {
        //Log.e("member details", "working" );
        avi.show();
        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.URL_MEMBERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
          //Log.e(TAG, response.toString());

                try {
                    JSONArray jArr = new JSONArray(response);
                    JSONObject jObj = jArr.getJSONObject(0);

                    String status = jObj.getString("status");
                    if (status.equals("Success")) {

                        if (jObj.toString().toLowerCase().contains("account_id")) {
                            Snackbar.make(mainLayout, "Success!", Snackbar.LENGTH_LONG).show();
                            spEdit.putString(getString(R.string.spUID), jObj.getString("account_id"));
                            spEdit.putString(getString(R.string.spUMobile), jObj.getString("mobilenumber"));
                            spEdit.putString(getString(R.string.spUName), jObj.getString("fullname"));
                            spEdit.putString(getString(R.string.spUEmail), jObj.getString("emailad"));
                            spEdit.putString(getString(R.string.spUPic), jObj.getString("profile_image"));
                            spEdit.putString(getString(R.string.spUC), jObj.getString("uc_id"));
                            spEdit.putString(getString(R.string.spNC), jObj.getString("nc_id"));
                            spEdit.commit();

                            Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Snackbar.make(mainLayout, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(mainLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }
                avi.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber", etPhone.getText().toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}