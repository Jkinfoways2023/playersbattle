package com.GlaDius.war.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText et_password, et_conf_pwd;
    private RelativeLayout lyt_save_pwd;
    private LinearLayout parent_layout;
    private TextView passwordResetResponse,txt_pshow,txt_cshow;
    private String countryCode, mobileNumber;
    private String newPassword, retypeNewPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        et_password = findViewById(R.id.et_password);
        et_conf_pwd = findViewById(R.id.et_conf_pwd);
        lyt_save_pwd = findViewById(R.id.lyt_save_pwd);
        parent_layout = findViewById(R.id.parent_layout);
        passwordResetResponse = findViewById(R.id.txt_mobile_login);
        txt_pshow = findViewById(R.id.txt_pshow);
        txt_cshow = findViewById(R.id.txt_cshow);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        txt_pshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txt_pshow.setText("Show");
                    et_password.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    txt_pshow.setText("Hide");
                    et_password.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }
                et_password.setSelection(et_password.getText().length());
            }
        });

        txt_cshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_conf_pwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txt_pshow.setText("Show");
                    et_conf_pwd.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    txt_pshow.setText("Hide");
                    et_conf_pwd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }
                et_conf_pwd.setSelection(et_conf_pwd.getText().length());
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            countryCode = extras.getString("ccode");
            mobileNumber = extras.getString("phone");
        }

        lyt_save_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                newPassword = et_password.getText().toString().trim().trim();
                retypeNewPassword = et_conf_pwd.getText().toString().trim();
                if (validatePassword()) {
                    if (new ExtraOperations().haveNetworkConnection(ResetPasswordActivity.this)) {
                        passwordResetResponse.setText("Please wait few seconds...");
                        passwordResetResponse.setTextColor(Color.parseColor("#000000"));
                        passwordResetResponse.setVisibility(View.VISIBLE);
                        Uri.Builder builder = Uri.parse(Constant.RESET_PASSWORD_URL).buildUpon();
                        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                        builder.appendQueryParameter("mobile", mobileNumber);
                        builder.appendQueryParameter("password", newPassword);
                        StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                    String success = jsonObject1.getString("success");
                                    String msg = jsonObject1.getString("msg");

                                    if (success.equals("1")) {
                                        Toast.makeText(ResetPasswordActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ResetPasswordActivity.this,SignInActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        Snackbar.make(parent_layout,""+msg,Snackbar.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    passwordResetResponse.setVisibility(View.GONE);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                passwordResetResponse.setVisibility(View.GONE);
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
                        request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        request.setShouldCache(false);
                        MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
                    } else {
                        Snackbar.make(parent_layout,"Password should be of 4 to 15 Digits/Characters.",Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public boolean validatePassword() {
        if (this.newPassword.length() < 4 || this.newPassword.length() > 15) {
            passwordResetResponse.setText("Enter New Password");
            passwordResetResponse.setTextColor(Color.parseColor("#ff0000"));
            passwordResetResponse.setVisibility(View.VISIBLE);
            Snackbar.make(parent_layout,"Password should be of 4 to 15 Digits/Characters.",Snackbar.LENGTH_LONG).show();
            return false;
        } else if (this.retypeNewPassword.isEmpty()) {
            passwordResetResponse.setText("Re-enter New Password");
            passwordResetResponse.setTextColor(Color.parseColor("#ff0000"));
            passwordResetResponse.setVisibility(View.VISIBLE);
            return false;
        } else if (this.retypeNewPassword.equals(this.newPassword)) {
            passwordResetResponse.setVisibility(View.GONE);
            return true;
        } else {
            passwordResetResponse.setText("New Passwords don't match. Retry!");
            passwordResetResponse.setTextColor(Color.parseColor("#ff0000"));
            passwordResetResponse.setVisibility(View.VISIBLE);
            Snackbar.make(parent_layout,"New Passwords don't match. Retry!",Snackbar.LENGTH_LONG).show();

            return false;
        }
    }


}
