package com.zeeroapps.wssp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ForgotPassword extends AppCompatActivity {
    private String tag_json_obj = "JSON_OBJECT";
    EditText phoneno, codetext;
    Button btnSend, btnResend, btnVerify;
    TextInputLayout tv;
    String phoneNumber;
    public String phoneVerificationId;
    LinearLayout layout;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth fbAuth;

    private ProgressDialog loadingBar;

    private String mVerificationId, mVerificationId2;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        phoneno = (EditText) findViewById(R.id.etPhone);
        codetext = (EditText) findViewById(R.id.etPhone1);

        tv = (TextInputLayout) findViewById(R.id.textViewEd);

        loadingBar = new ProgressDialog(this);

        btnSend = (Button) findViewById(R.id.buttonSend);
        btnResend = (Button) findViewById(R.id.buttonResend);
        btnVerify = (Button) findViewById(R.id.btn_verify);
        layout = findViewById(R.id.codeEnter);

        fbAuth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSend.setVisibility(View.GONE);
                btnResend.setVisibility(View.VISIBLE);

                phoneNumber = phoneno.getText().toString();

                //Log.d("ayaz:" , phoneNumber);

                getMemberDetailsWS(phoneNumber);

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(ForgotPassword.this, "Please enter your phone number first...", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    String phone = phoneNumber.substring(1, 11);
                    String number = "+92" + phone;
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, ForgotPassword.this, callbacks);
                }
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getMemberDetailsWS(phoneNumber);

                    loadingBar.setTitle("Phone Verification");
                    loadingBar.setMessage("Please wait, while we are authenticating using your phone...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    String phone = phoneNumber.substring(1, 11);
                    String resendNumber = "+92" + phone;
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(resendNumber, 60, TimeUnit.SECONDS, ForgotPassword.this, callbacks);

            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(ForgotPassword.this, "Invalid Phone Number, Please enter correct phone number ...", Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
//            InputUserPhoneNumber.setVisibility(View.VISIBLE);
//            SendVerificationCodeButton.setVisibility(View.VISIBLE);
//            InputUserVerificationCode.setVisibility(View.INVISIBLE);
//            VerifyButton.setVisibility(View.INVISIBLE);

                //resend code here
//            linear_resend_code.setVisibility(View.VISIBLE);
//            linear_send_code.setVisibility(View.GONE);
//            linear_sendVerify_code.setVisibility(View.GONE);
            }

            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                phoneno.setVisibility(View.GONE);
                btnSend.setVisibility(View.GONE);
                btnResend.setVisibility(View.VISIBLE);
                tv.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                if (Constants.CHECK_MOB_NUM == 0) {
                    Toast.makeText(ForgotPassword.this, "Code has been sent, please check and verify...", Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();
                }

//            InputUserPhoneNumber.setVisibility(View.INVISIBLE);
//            SendVerificationCodeButton.setVisibility(View.INVISIBLE);

//            InputUserVerificationCode.setVisibility(View.VISIBLE);
//            VerifyButton.setVisibility(View.VISIBLE);

                //linear_resend_code.setVisibility(View.GONE);

            }
        };

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                InputUserPhoneNumber.setVisibility(View.INVISIBLE);
//                SendVerificationCodeButton.setVisibility(View.INVISIBLE);
                String verificationCode = codetext.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(ForgotPassword.this, "Please write verification code first...", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Verification Code");
                    loadingBar.setMessage("Please wait, while we are verifying verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential authCredential) {
        Intent intent = new Intent(ForgotPassword.this, ResetPassword.class);
        intent.putExtra("mobno",phoneNumber);
        startActivity(intent);
    }


    public void getMemberDetailsWS(final String mobNumber) {

        Log.e("member details", "working" );
//        avi.show();
        StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.URL_MEMBERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.e(TAG, response.toString());

                try {
                    Constants.CHECK_MOB_NUM = 0;
                    JSONArray jArr = new JSONArray(response);
                    JSONObject jObj = jArr.getJSONObject(0);
                    String accountId = jObj.getString("account_id");

                    Log.d("ayaz","account id: "+accountId);

//                        Snackbar.make(mainLayout, "Success!", Snackbar.LENGTH_LONG).show();
//                        spEdit.putString(getString(R.string.spUID), jObj.getString("account_id"));
//                        spEdit.putString(getString(R.string.spUMobile), jObj.getString("mobilenumber"));
//                        spEdit.putString(getString(R.string.spUName), jObj.getString("fullname"));
//                        spEdit.putString(getString(R.string.spUEmail), jObj.getString("emailad"));
//                        spEdit.putString(getString(R.string.spUPic), jObj.getString("profile_image"));
//                        spEdit.putString(getString(R.string.spUC), jObj.getString("uc_id"));
//                        spEdit.putString(getString(R.string.spNC), jObj.getString("nc_id"));
//                        spEdit.commit();

//                        Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
//                        startActivity(intent);
//                        finish();



                } catch (JSONException e) {
                    e.printStackTrace();
                    Constants.CHECK_MOB_NUM = 1;

                    Toast.makeText(ForgotPassword.this, ""+Constants.CHECK_MOB_NUM, Toast.LENGTH_SHORT).show();
                    if(Constants.CHECK_MOB_NUM ==1){
                        exitThis();
                    }
                }
                //avi.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.toString());
                if (error.toString().contains("NoConnectionError")) {
                    Toast.makeText(ForgotPassword.this, "Error in connection!", Toast.LENGTH_SHORT).show();
                    //Snackbar.make(mainLayout, "Error in connection!", Snackbar.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPassword.this, "Server not responding!", Toast.LENGTH_SHORT).show();
                    //Snackbar.make(mainLayout, "Server not responding!", Snackbar.LENGTH_LONG).show();
                }

                //avi.hide();t
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobilenumber", phoneno.getText().toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);

    }

    public void exitThis(){
        Toast.makeText(this, "Record not found in Database...", Toast.LENGTH_SHORT).show();
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }
}
