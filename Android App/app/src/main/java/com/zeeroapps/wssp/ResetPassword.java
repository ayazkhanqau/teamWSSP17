package com.zeeroapps.wssp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.zeeroapps.wssp.activities.LoginActivity;
import com.zeeroapps.wssp.utils.AppController;
import com.zeeroapps.wssp.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {
    String phoneNo;
    String password1;
    EditText pass, rpass;
    private static final String TAG = "MyApp";
    private String tag_json_obj = "JSON_OBJECT";
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        avi = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        avi.hide();
        pass = findViewById(R.id.password);
        rpass = findViewById(R.id.rpassword);

        Bundle b = getIntent().getExtras();
        assert b != null;
        phoneNo = b.getString("mobno");

        Toast.makeText(this, ""+phoneNo, Toast.LENGTH_SHORT).show();
    }

    public void resetPassword(View view) {

        if (pass.getText().toString().equals("")) {
            pass.requestFocus();
            pass.setError("Enter password...");
            return;
        }

        if (rpass.getText().toString().equals("")) {
            rpass.requestFocus();
            rpass.setError("Enter Confirm password...");
            return;
        }if (pass.getText().toString().length()<5){
            pass.requestFocus();
            pass.setError("Password Length must be 5 characters long...");
            return;
        }
        if (!pass.getText().toString().equals(rpass.getText().toString())) {
            rpass.requestFocus();
            rpass.setError("Password not matched!");

        } else {
            StringRequest jsonReq = new StringRequest(Request.Method.POST, Constants.RESET_PASS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    avi.hide();

                    try {
                        JSONObject jObj = new JSONObject(response.toString());
                        String status = jObj.getString("message");

                        if (status.toLowerCase().contains("changed")) {

                            Toast.makeText(ResetPassword.this, "Password Reset Successfully Login with new Password...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ResetPassword.this, "Error" +
                                    "", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());
                    if (error.toString().contains("NoConnectionError")) {
                        Toast.makeText(ResetPassword.this, "Error in connection!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ResetPassword.this, "Server not responding!", Toast.LENGTH_SHORT).show();
                    }
                    avi.hide();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    try {
                        password1 = SHA1(pass.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mobilenumber", phoneNo);
                    params.put("userPassword", password1);

                    return params;
                }
            };
            AppController.getInstance().addToRequestQueue(jsonReq, tag_json_obj);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}
