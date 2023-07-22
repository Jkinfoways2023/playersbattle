package com.GlaDius.war.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.GlaDius.war.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ForgetPasswordActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private CardView cv_phone;
    private LinearLayout parent_layout, phoneLL, emailLL;
    private EditText et_phn, et_email;
    private RelativeLayout lyt_send;
    private ImageView img_back;
    private String mobileNumber, countryCode,emailId;
    private ProgressBar progressBar;
    private TextView txt_reset;
    int check = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        cv_phone = (CardView) findViewById(R.id.cv_phone);
        parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
        phoneLL = (LinearLayout) findViewById(R.id.phoneLL);
        emailLL = (LinearLayout) findViewById(R.id.emailLL);
        txt_reset = (TextView) findViewById(R.id.txt_reset);
        et_phn = (EditText) findViewById(R.id.et_phn);
        et_email = (EditText) findViewById(R.id.et_email);
        lyt_send = (RelativeLayout) findViewById(R.id.lyt_send);
        img_back= (ImageView) findViewById(R.id.img_back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lyt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                mobileNumber = et_phn.getText().toString().trim();
                emailId = et_email.getText().toString().trim();
                countryCode = ccp.getSelectedCountryCode().toString().trim();


               if(check == 1){
                    if (validatecountrycode() && validatemobilenumber()) {
                        verifyMobile("+"+countryCode, mobileNumber);
                    }
                }
                else {
                    if (validateemail()) {
                        verifyEmail(emailId);
                    }
                }

            }
        });



        txt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check == 1){
                    phoneLL.setVisibility(View.GONE);
                    emailLL.setVisibility(View.VISIBLE);
                    check = 0;
                    txt_reset.setText("Reset Password By Phone");
                }else{
                    phoneLL.setVisibility(View.VISIBLE);
                    emailLL.setVisibility(View.GONE);
                    check = 1;
                    txt_reset.setText("Reset Password By Email");
                }
            }
        });
    }

    private void verifyEmail(String emailId) {
        progressBar.setVisibility(View.VISIBLE);
        Uri.Builder builder = Uri.parse(Constant.FORGET_PASSWORD_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("email",emailId);
        StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("result");
                    JSONObject jsonObject =jsonArray.getJSONObject(0);

                    String success = jsonObject.getString("success");
                    String message = jsonObject.getString("msg");

                    if (success.equals("0")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_LONG).show();
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_LONG).show();
                        try {
                            onBackPressed();
                        }catch (NullPointerException | IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                //parameters.put("email",strEmail);
                return parameters;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
    }


    public boolean validatemobilenumber() {
        if (this.mobileNumber.length() >= 8 && this.mobileNumber.length() <= 13) {
            return true;
        }
        Snackbar.make(parent_layout,"Mobile Number should be of 8 to 13 Digits", Snackbar.LENGTH_LONG).show();
        return false;
    }

    public boolean validatecountrycode() {
        if (this.countryCode.length() >= 1) {
            return true;
        }
        Snackbar.make(parent_layout,"Pick your country code", Snackbar.LENGTH_LONG).show();
        return false;
    }

    public boolean isvalideemail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public boolean validateemail() {
        if (!this.emailId.isEmpty()) {
            if (isvalideemail(this.emailId)) {
                return true;
            }
        }
        Snackbar.make(parent_layout,"Please enter valid email address.", Snackbar.LENGTH_LONG).show();
        return false;
    }

    private void verifyMobile(final String countryCode, final String mobileNumber) {
        if (new ExtraOperations().haveNetworkConnection(this)) {
            progressBar.setVisibility(View.VISIBLE);
            Uri.Builder builder = Uri.parse(Constant.VERIFY_MOBILE_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("mobile", mobileNumber);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");
                        String msg = jsonObject1.getString("msg");
                        if (success.equals("0")) {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(parent_layout,""+msg, Snackbar.LENGTH_LONG).show();
                        }
                        else if (success.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(ForgetPasswordActivity.this,OTPActivity.class);
                            intent.putExtra("ccode", countryCode);
                            intent.putExtra("phone", mobileNumber);
                            intent.putExtra("tag","forgetPassword");
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                    return parameters;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
        } else {
            Snackbar.make(parent_layout,"No internet connection", Snackbar.LENGTH_LONG).show();
        }
    }

}
