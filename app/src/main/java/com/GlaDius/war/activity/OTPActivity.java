package com.GlaDius.war.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.views.GenericTextWatcher;
import com.GlaDius.war.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class OTPActivity extends AppCompatActivity {

    private int randonnumber;

    private static final String TAG = "PhoneAuth";
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;

    private TextView txt_phone_number, txt_country_code, txt_timer, txt_didnt, txt_request, txt_resend;
    private EditText et_otp1, et_otp2, et_otp3, et_otp4, et_otp5, et_otp6;
    private ScrollView parent_layout;
    private ImageView img_back;
    private CardView card_submit_code;
    private LinearLayout ll_timer;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;

    private String eMail;
    private String firstname;
    private String lastname;
    private String countryCode;
    private String mobileNumber;
    private String password;
    private String promocode;
    private String username;
    private String strCodePhone;
    private String strDeviceID;
    private String strTag;
    private String st_otp1, st_otp2, st_otp3, st_otp4, st_otp5, st_otp6;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        parent_layout = (ScrollView) findViewById(R.id.parent_layout);
        card_submit_code = (CardView) findViewById(R.id.card_submit_code);
        img_back = (ImageView) findViewById(R.id.img_back);
        txt_timer = (TextView) findViewById(R.id.txt_timer);
        txt_didnt = (TextView) findViewById(R.id.txt_didnt);
        txt_request = (TextView) findViewById(R.id.txt_request);
        txt_resend = (TextView) findViewById(R.id.txt_resend);
        txt_phone_number = (TextView) findViewById(R.id.txt_phone_number);
        txt_country_code = (TextView) findViewById(R.id.txt_country_code);
        ll_timer = (LinearLayout) findViewById(R.id.ll_timer);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        et_otp1 = (EditText) findViewById(R.id.et_otp1);
        et_otp2 = (EditText) findViewById(R.id.et_otp2);
        et_otp3 = (EditText) findViewById(R.id.et_otp3);
        et_otp4 = (EditText) findViewById(R.id.et_otp4);
        et_otp5 = (EditText) findViewById(R.id.et_otp5);
        et_otp6 = (EditText) findViewById(R.id.et_otp6);

        et_otp1.addTextChangedListener(new GenericTextWatcher(et_otp2, et_otp1));
        et_otp2.addTextChangedListener(new GenericTextWatcher(et_otp3, et_otp1));
        et_otp3.addTextChangedListener(new GenericTextWatcher(et_otp4, et_otp2));
        et_otp4.addTextChangedListener(new GenericTextWatcher(et_otp5, et_otp3));
        et_otp5.addTextChangedListener(new GenericTextWatcher(et_otp6, et_otp4));
        et_otp6.addTextChangedListener(new GenericTextWatcher(et_otp6, et_otp5));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            strTag = extras.getString("tag");
            if (strTag != null) {
                if (strTag.equals("regulerSignUp")){
                    firstname = extras.getString("fname");
                    lastname = extras.getString("lname");
                    username = extras.getString("uname");
                    eMail = extras.getString("email");
                    countryCode = extras.getString("ccode");
                    mobileNumber = extras.getString("phone");
                    password = extras.getString("pass");
                    promocode = extras.getString("rcode");
                    strDeviceID = extras.getString("device");

                    txt_country_code.setText(countryCode);
                    txt_phone_number.setText(mobileNumber);

                    String phoneNumber = countryCode+mobileNumber;
                    initialsendotp();
                }
                else if (strTag.equals("forgetPassword")) {
                    countryCode = extras.getString("ccode");
                    mobileNumber = extras.getString("phone");

                    String phoneNumber = countryCode+mobileNumber;
                    setUpVerificationCallbacks();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            this,               // Activity (for callback binding)
                            verificationCallbacks);
                }
            }

        }

        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long mins = millisUntilFinished / 60000;
                long secs = millisUntilFinished % 60000 / 1000;
                final String display = String.format("%02d:%02d", mins, secs);

                txt_timer.setText(display);
                txt_timer.setVisibility(View.VISIBLE);
                txt_didnt.setVisibility(View.GONE);
                ll_timer.setVisibility(View.GONE);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                txt_timer.setVisibility(View.GONE);
                txt_didnt.setVisibility(View.VISIBLE);
                ll_timer.setVisibility(View.VISIBLE);
            }

        }.start();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txt_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strTag.equals("forgetPassword")) {
                    resendVerificationCode(resendToken);
                }
                else {
                    initialsendotp();

                    if (countDownTimer != null){
                        countDownTimer.cancel();
                    }
                    countDownTimer = new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            long mins = millisUntilFinished / 60000;
                            long secs = millisUntilFinished % 60000 / 1000;
                            final String display = String.format("%02d:%02d", mins, secs);

                            txt_timer.setText(display);
                            txt_timer.setVisibility(View.VISIBLE);
                            txt_didnt.setVisibility(View.GONE);
                            ll_timer.setVisibility(View.GONE);
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            txt_timer.setVisibility(View.GONE);
                            txt_didnt.setVisibility(View.VISIBLE);
                            ll_timer.setVisibility(View.VISIBLE);
                        }

                    }.start();
                }
            }
        });

        card_submit_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strTag.equals("forgetPassword")) {
                    verifyCodeForget();
                }
                else {
                    verifyCode();
                }
            }
        });


    }

    public void verifyCodeForget() {
        try {
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        st_otp1 = et_otp1.getText().toString().trim();
        st_otp2 = et_otp2.getText().toString().trim();
        st_otp3 = et_otp3.getText().toString().trim();
        st_otp4 = et_otp4.getText().toString().trim();
        st_otp5 = et_otp5.getText().toString().trim();
        st_otp6 = et_otp6.getText().toString().trim();

        if (!st_otp1.isEmpty() && !st_otp2.isEmpty() && !st_otp3.isEmpty() && !st_otp4.isEmpty() && !st_otp5.isEmpty() && !st_otp6.isEmpty()){
            try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId, st_otp1+st_otp2+st_otp3+st_otp4+st_otp5+st_otp6);
                signInWithPhoneAuthCredential(credential);
            }catch (IllegalArgumentException | NullPointerException e){
                e.printStackTrace();
            }
        }
        else {
            Snackbar.make(parent_layout,"verification code entered was invalid.", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setUpVerificationCallbacks() {
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.d(TAG, "Invalid credential: " + e.getLocalizedMessage());
                    Snackbar.make(parent_layout,"Invalid OTP", Snackbar.LENGTH_LONG).show();

                    et_otp1.setText("");
                    et_otp2.setText("");
                    et_otp3.setText("");
                    et_otp4.setText("");
                    et_otp5.setText("");
                    et_otp6.setText("");

                    try {
                        onBackPressed();
                    }catch (NullPointerException exp){
                        exp.printStackTrace();
                    }
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // SMS quota exceeded
                    Log.d(TAG, "SMS Quota exceeded.");
                    Snackbar.make(parent_layout,"SMS Quota exceeded.", Snackbar.LENGTH_LONG).show();

                    et_otp1.setText("");
                    et_otp2.setText("");
                    et_otp3.setText("");
                    et_otp4.setText("");
                    et_otp5.setText("");
                    et_otp6.setText("");

                    try {
                        onBackPressed();
                    }catch (NullPointerException exp){
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                resendToken = token;

            }
        };
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    et_otp1.setText("");
                    et_otp2.setText("");
                    et_otp3.setText("");
                    et_otp4.setText("");
                    et_otp5.setText("");
                    et_otp6.setText("");

                    Intent intent = new Intent(OTPActivity.this,ResetPasswordActivity.class);
                    intent.putExtra("ccode", "+"+countryCode);
                    intent.putExtra("phone", mobileNumber);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Snackbar.make(parent_layout,"verification code entered was invalid.", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    void initialsendotp(){
        try {
            // Construct data
            Random random = new Random();
            randonnumber=random.nextInt(999999);
            String user = "user=" + "PLYR_BLCK-WAR";
            String pass = "&pass=" + "123456";
            String sender = "&sender=" + "PWBWOL";
            String numbers = "&phone=" +mobileNumber;
            String message = "&text=" + "Hi,\nWelcome to PLAYERWAR OFFICIAL App.\nYour verification code is: " +randonnumber;
            String priority = "&priority=" +"ndnd";
            String stype = "&stype=" +"normal";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("http://bhashsms.com/api/sendmsg.php?").openConnection();
            String data = user + pass + sender + numbers + message + priority + stype;
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            System.out.println("Error SMS "+e);
            progressBar.setVisibility(View.GONE);
        }
    }


    public boolean validatepromocode() {
        if (!this.promocode.isEmpty()) {
            return true;
        }
        Snackbar.make(parent_layout,"Please enter valid promo code.",Snackbar.LENGTH_LONG).show();
        return false;
    }

    public void verifyCode() {
        try {
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        st_otp1 = et_otp1.getText().toString().trim();
        st_otp2 = et_otp2.getText().toString().trim();
        st_otp3 = et_otp3.getText().toString().trim();
        st_otp4 = et_otp4.getText().toString().trim();
        st_otp5 = et_otp5.getText().toString().trim();
        st_otp6 = et_otp6.getText().toString().trim();

        String strOTP = st_otp1+st_otp2+st_otp3+st_otp4+st_otp5+st_otp6;
        if(strOTP.equals(String.valueOf(randonnumber))) {
            if (strTag.equals("regulerSignUp")) {
                submitData();
            }
            else if (strTag.equals("forgetPassword")) {
                Intent intent = new Intent(OTPActivity.this,ResetPasswordActivity.class);
                intent.putExtra("ccode", "+"+countryCode);
                intent.putExtra("phone", mobileNumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
        else{
            Snackbar.make(parent_layout,"verification code entered was invalid.",Snackbar.LENGTH_LONG).show();
        }
    }


    public void submitData() {
        if (new ExtraOperations().haveNetworkConnection(this)) {
            if (promocode.length() >= 2){
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("fname",firstname);
                builder.appendQueryParameter("lname",lastname);
                builder.appendQueryParameter("username",username);
                builder.appendQueryParameter("password",password);
                builder.appendQueryParameter("email",eMail);
                builder.appendQueryParameter("country_code",countryCode);
                builder.appendQueryParameter("mobile",mobileNumber);
                builder.appendQueryParameter("device_id",strDeviceID);
                builder.appendQueryParameter("referer",promocode);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("0")) {
                                onBackPressed();
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(parent_layout,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                onBackPressed();
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(parent_layout,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else if (success.equals("2")){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(OTPActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                                //session.createLoginSession(jsonObject1.getString("data"),null,firstname,lastname,username,password,eMail,countryCode,mobileNumber);
                                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("fname",firstname);
                builder.appendQueryParameter("lname",lastname);
                builder.appendQueryParameter("username",username);
                builder.appendQueryParameter("password",password);
                builder.appendQueryParameter("email",eMail);
                builder.appendQueryParameter("country_code",countryCode);
                builder.appendQueryParameter("mobile",mobileNumber);
                builder.appendQueryParameter("device_id",strDeviceID);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("0")) {
                                onBackPressed();
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(parent_layout,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(OTPActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                                //session.createLoginSession(jsonObject1.getString("data"),null,firstname,lastname,username,password,eMail,countryCode,mobileNumber);
                                Intent intent = new Intent(getApplicationContext(),SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
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
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void resendVerificationCode(PhoneAuthProvider.ForceResendingToken token) {
        String phoneNumber = countryCode+mobileNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,          // Phone number to verify
                60,                  // Timeout duration
                TimeUnit.SECONDS,       // Unit of timeout
                OTPActivity.this,            // Activity (for callback binding)
                verificationCallbacks,  // OnVerificationStateChangedCallbacks
                token);                 // ForceResendingToken from callbacks
    }


}
