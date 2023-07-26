package com.tournaments.grindbattles.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;

public class SignUpActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextView txt_sign_in, promo_applied_text, txt_show;
    private CardView cv_apply_coupon, cv_fname, cv_lnamme, cv_uname;
    private CountryCodePicker ccp;
    private EditText et_fname, et_lname, et_uname, et_phn, et_email, et_pwd, et_coupon;
    private ImageView img_check_social, img_check, img_close;
    private RelativeLayout lyt_signup;
    private LinearLayout bottom_sheet_number;
    private CoordinatorLayout parent_lyt;
    private ProgressBar progressBar;

    private CardView cv_fb, cv_google, card_submit_number, cv_apply_coupon_social,  cv_phone, cv_email, cv_pass, cv_promo;
    private EditText et_phnone_botom, et_coupon_social;
    private TextView img_remove_coupon, img_remove_coupon_social, promo_applied_text_social,txt_tc, txt_privacy, txt_apply_social;

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

    private static final String TAG = "PhoneAuth";
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;

    private LoginButton btnSignInFB;
    private CallbackManager callbackManager;

    private SignInButton btnSignInGoogle;
    private GoogleApiClient googleApiClient;

    private static final int REQ_CODE =9001;
    private static final String TAGS = "SignInActivity";

    private SessionManager session;
    private ImageView btnFB,btnGoogle;

    private String strName;
    private String strReferralCode, profile_pic, user_id, facebook_id, google_id, facebook_username, google_username;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        strDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        txt_sign_in = (TextView) findViewById(R.id.txt_sign_in);
        cv_apply_coupon = (CardView) findViewById(R.id.cv_apply_coupon);
        cv_fname = (CardView) findViewById(R.id.cv_fname);
        cv_lnamme = (CardView) findViewById(R.id.cv_lname);
        cv_uname = (CardView) findViewById(R.id.cv_uname);
        promo_applied_text= (TextView) findViewById(R.id.promo_applied_text);
        txt_show = (TextView) findViewById(R.id.txt_show);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_uname = (EditText) findViewById(R.id.et_uname);
        et_phn = (EditText) findViewById(R.id.et_phn);
        img_check_social = (ImageView) findViewById(R.id.img_check_social);
        et_email = (EditText) findViewById(R.id.et_email);
        img_check = (ImageView) findViewById(R.id.img_check);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_coupon = (EditText) findViewById(R.id.et_coupon);
        parent_lyt = (CoordinatorLayout) findViewById(R.id.parent_lyt);
        lyt_signup = (RelativeLayout) findViewById(R.id.lyt_signup);
        img_remove_coupon = (TextView) findViewById(R.id.img_remove_coupon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        bottom_sheet_number = (LinearLayout) findViewById(R.id.bottom_sheet_number);


        cv_fb = (CardView) findViewById(R.id.cv_fb);
        cv_google = (CardView) findViewById(R.id.cv_google);
        txt_tc = (TextView) findViewById(R.id.txt_tc);
        txt_privacy = (TextView) findViewById(R.id.txt_privacy);
        cv_phone = (CardView) findViewById(R.id.cv_phone);
        cv_email = (CardView) findViewById(R.id.cv_email);
        cv_pass = (CardView) findViewById(R.id.cv_pass);
        cv_promo = (CardView) findViewById(R.id.cv_promo);
        btnSignInFB = (LoginButton) findViewById(R.id.btnSignInFB);
        btnSignInGoogle = (SignInButton) findViewById(R.id.btnSignInGoogle);

        txt_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_pwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txt_show.setText("Show");
                    et_pwd.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    txt_show.setText("Hide");
                    et_pwd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }
                et_pwd.setSelection(et_pwd.getText().length());
            }
        });

        txt_tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,TermsConditionsActivity.class);
                startActivity(intent);
            }
        });

        txt_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this,PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });

        lyt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lyt_signup.setEnabled(false);
                verifyData();
            }
        });

        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cv_apply_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPromoCode("regular");
            }
        });

        img_remove_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_apply_coupon.setVisibility(View.VISIBLE);
                img_remove_coupon.setVisibility(View.GONE);
                et_coupon.setText("");
                promocode = "";
            }
        });


        cv_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.cv_fb){
                    if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                        btnSignInFB.performClick();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please check your connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnSignInFB.setReadPermissions(Arrays.asList("email", "public_profile"));
        btnSignInFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                AccessToken accessToken = loginResult.getAccessToken();
                useLoginInformation(accessToken);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });


        cv_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                    try {
                        if (googleApiClient != null && googleApiClient.isConnected()) {
                            googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {

                                @Override
                                public void onResult(Status status) {
                                    googleApiClient.disconnect();
                                    signIn();
                                }
                            });
                        }
                        else {
                            signIn();
                        }
                    } catch (Exception e) {
                        Log.d("DISCONNECT ERROR", e.toString());
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please check your connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */,0, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mBehavior = BottomSheetBehavior.from(bottom_sheet_number);

    }

    private void useLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                facebook_id = (accessToken.getUserId());
                facebook_username = (accessToken.getUserId());

                try {
                    if (!object.getString("email").equals("null")){
                        eMail = object.getString("email");
                    }
                    else {
                        eMail = facebook_username+"@demo.com";
                    }
                } catch (JSONException | NullPointerException  e) {
                    e.printStackTrace();
                    eMail = facebook_username+"@demo.com";
                }

                try {
                    if (!object.getString("name").equals("null")){
                        strName = object.getString("name");
                    }
                    else {
                        strName = "Guest User";
                    }
                } catch (JSONException | NullPointerException  e) {
                    e.printStackTrace();
                    strName = "Guest User";
                }

                setProfileToView(facebook_id,facebook_username,strName,eMail);
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    private void verifyPromoCode(String tag) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (tag.equals("regular")) {
            this.promocode = this.et_coupon.getText().toString().trim();
            if (!this.promocode.isEmpty()) {
                if (new ExtraOperations().haveNetworkConnection(this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    Uri.Builder builder = Uri.parse(Constant.VERIFY_REFER_URL).buildUpon();
                    builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                    builder.appendQueryParameter("refer", promocode);
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
                                    try {
                                        cv_apply_coupon.setVisibility(View.VISIBLE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    try {
                                        img_remove_coupon.setVisibility(View.GONE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    et_coupon.setText("");
                                    promocode = "";
                                    Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                                }
                                else if (success.equals("1")) {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        cv_apply_coupon.setVisibility(View.GONE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    try {
                                        img_remove_coupon.setVisibility(View.VISIBLE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                try {
                                    cv_apply_coupon.setVisibility(View.VISIBLE);
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                                try {
                                    img_remove_coupon.setVisibility(View.GONE);
                                }catch (NullPointerException e2){
                                    e2.printStackTrace();
                                }
                                et_coupon.setText("");
                                promocode = "";
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            try {
                                cv_apply_coupon.setVisibility(View.VISIBLE);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try {
                                img_remove_coupon.setVisibility(View.GONE);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            et_coupon.setText("");
                            promocode = "";
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
                } else {
                    Snackbar.make(parent_lyt,"No internet connection",Snackbar.LENGTH_LONG).show();
                }
            }
            else {
                Snackbar.make(parent_lyt,"Please enter valid promo code.",Snackbar.LENGTH_LONG).show();
            }
        }
        else if (tag.equals("social")) {
            this.promocode = this.et_coupon_social.getText().toString().trim();
            if (!this.promocode.isEmpty()) {
                if (new ExtraOperations().haveNetworkConnection(this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    Uri.Builder builder = Uri.parse(Constant.VERIFY_REFER_URL).buildUpon();
                    builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                    builder.appendQueryParameter("refer", promocode);
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
                                    try {
                                        cv_apply_coupon_social.setVisibility(View.VISIBLE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    try {
                                        img_remove_coupon_social.setVisibility(View.GONE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    et_coupon_social.setText("");
                                    promocode = "";
                                    Snackbar snackbar = Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG);
                                    View view = snackbar.getView();
                                    CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    view.setLayoutParams(params);
                                    snackbar.show();
                                }
                                else if (success.equals("1")) {
                                    progressBar.setVisibility(View.GONE);
                                    try {
                                        cv_apply_coupon_social.setVisibility(View.GONE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    try {
                                        img_remove_coupon_social.setVisibility(View.VISIBLE);
                                    }catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                    Snackbar snackbar = Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG);
                                    View view = snackbar.getView();
                                    CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                                    params.gravity = Gravity.TOP;
                                    view.setLayoutParams(params);
                                    snackbar.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                try {
                                    cv_apply_coupon_social.setVisibility(View.VISIBLE);
                                }catch (NullPointerException e1){
                                    e1.printStackTrace();
                                }
                                try {
                                    img_remove_coupon_social.setVisibility(View.GONE);
                                }catch (NullPointerException e2){
                                    e2.printStackTrace();
                                }
                                et_coupon_social.setText("");
                                promocode = "";
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            try {
                                cv_apply_coupon_social.setVisibility(View.VISIBLE);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            try {
                                img_remove_coupon_social.setVisibility(View.GONE);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            et_coupon_social.setText("");
                            promocode = "";
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
                } else {
                    Snackbar snackbar = Snackbar.make(parent_lyt,"No internet connection",Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();
                }
            }
            else {
                Snackbar snackbar = Snackbar.make(parent_lyt,"Please enter valid promo code.",Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                params.gravity = Gravity.TOP;
                view.setLayoutParams(params);
                snackbar.show();
            }
        }

    }

    public boolean validatefirstname() {
        if (this.firstname.isEmpty()) {
            Snackbar.make(parent_lyt,"Please enter valid first name.",Snackbar.LENGTH_LONG).show();
            return false;
        } else if (this.firstname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            Snackbar.make(parent_lyt,"Special character are not allow in first name.",Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean validatelastname() {
        if (this.lastname.isEmpty()) {
            Snackbar.make(parent_lyt,"Please enter valid last name.",Snackbar.LENGTH_LONG).show();
            return false;
        } else if (this.lastname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            Snackbar.make(parent_lyt,"Special character are not allow in last name.",Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean isvalideemail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public boolean validateemail() {
        if (!this.eMail.isEmpty()) {
            if (isvalideemail(this.eMail)) {
                return true;
            }
        }
        Snackbar.make(parent_lyt,"Please enter valid email address.",Snackbar.LENGTH_LONG).show();
        return false;
    }

    public boolean validateusername() {
        if (!this.username.isEmpty()) {
            return true;
        } else {
            Snackbar.make(parent_lyt,"Please enter valid Username.",Snackbar.LENGTH_LONG).show();
            return false;
        }
    }


    public boolean validatepassword() {
        if (this.password.isEmpty()) {
            Snackbar.make(parent_lyt,"Please enter a valid password.",Snackbar.LENGTH_LONG).show();
        }
        else if (this.password.length() >= 8 && this.password.length() <= 20) {
            return true;
        }
        Snackbar.make(parent_lyt,"Password length must be 8-20 characters.",Snackbar.LENGTH_LONG).show();
        return false;
    }


    public boolean validatepromocode() {
        if (!this.promocode.isEmpty()) {
            return true;
        }
        Snackbar.make(parent_lyt,"Please enter valid promo code.",Snackbar.LENGTH_LONG).show();
        return false;
    }


    public boolean validatemobilenumber() {
        if (this.mobileNumber.isEmpty()) {
            Snackbar.make(parent_lyt,"Please enter a valid mobile number.",Snackbar.LENGTH_LONG).show();
        }
        else if (this.mobileNumber.length() >= 5 && this.mobileNumber.length() <= 15) {
            return true;
        }
        Snackbar.make(parent_lyt,"Mobile number length must be 5-15 digits.",Snackbar.LENGTH_LONG).show();
        return false;
    }

    public boolean validatecountrycode() {
        if (this.countryCode.length() >= 1) {
            return true;
        }
        Snackbar.make(parent_lyt,"Please pick your country code.",Snackbar.LENGTH_LONG).show();
        return false;
    }

    private void verifyData() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        this.firstname = this.et_fname.getText().toString().trim();
        this.lastname = this.et_lname.getText().toString().trim();
        this.username  = this.et_uname.getText().toString().trim();
        this.eMail = this.et_email.getText().toString().trim();
        this.mobileNumber = this.et_phn.getText().toString().trim();
        this.countryCode = this.ccp.getSelectedCountryCode().toString().trim();
        this.password = this.et_pwd.getText().toString().trim();
        this.promocode = this.et_coupon.getText().toString().trim();
        strCodePhone=countryCode+mobileNumber;

        if (validatefirstname() && validatelastname() && validateusername() && validateemail() && validatecountrycode() && validatemobilenumber() && validatepassword()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.VERIFY_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                builder.appendQueryParameter("username", username);
                builder.appendQueryParameter("email", eMail);
                builder.appendQueryParameter("mobile", mobileNumber);
                builder.appendQueryParameter("device_id",strDeviceID);
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
                                lyt_signup.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                disconnectFromFacebook();
                                Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else if (success.equals("1")) {
                                lyt_signup.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                /*Intent intent = new Intent(SignUpActivity.this,OTPActivity.class);
                                intent.putExtra("fname", firstname);
                                intent.putExtra("lname", lastname);
                                intent.putExtra("uname", username);
                                intent.putExtra("email", eMail);
                                intent.putExtra("ccode", "+"+countryCode);
                                intent.putExtra("phone", mobileNumber);
                                intent.putExtra("pass", password);
                                intent.putExtra("rcode", promocode);
                                intent.putExtra("device",strDeviceID);
                                intent.putExtra("tag","regulerSignUp");
                                startActivity(intent);*/
                                submitData();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            lyt_signup.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        lyt_signup.setEnabled(true);
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
            } else {
                lyt_signup.setEnabled(true);
                Snackbar.make(parent_lyt,"No internet connection",Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            lyt_signup.setEnabled(true);
        }
    }


    private void setProfileToView(String facebook_id, String facebook_username, String strName, String eMail) {
        try{
            String[] resArray1 = eMail.split("@");
            username = resArray1[0];
        }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }

        try{
            String[] resArray2 = strName.split(" ");
            firstname = resArray2[0];
            lastname = resArray2[1];
        }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }

        showBottomSheetDialog(firstname, lastname, username, eMail, facebook_id);
        //signUpFacebook(strUsername, facebook_id);
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    public void disconnectFromGoogle() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        disconnectFromFacebook();
        //disconnectFromGoogle();
    }

    public void handleResult(GoogleSignInResult result){
        try {
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();

                assert account != null;
                google_id = account.getId();
                google_username = account.getEmail();
                strName = account.getDisplayName();
                eMail = account.getEmail();

                try {
                    if (eMail != null){
                        String[] resArray1 = eMail.split("@");
                        username = resArray1[0];
                    } else {
                        username = google_id;
                    }
                } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                    username = google_id;
                }

                try {
                    if (strName != null){
                        String[] resArray2 = strName.split(" ");
                        firstname = resArray2[0];
                        lastname = resArray2[1];
                    } else {
                        firstname = "Guest";
                        lastname = "User";
                    }
                } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                    firstname = "Guest";
                    lastname = "User";
                }

                updateUI(true,firstname, lastname, username, eMail, google_id);
            }
            else {
                updateUI(false,firstname, lastname, username, eMail, google_id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false, firstname, lastname, username, eMail, google_id);
            }
        });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false, firstname, lastname, username, eMail, google_id);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void updateUI(boolean isLogin, String strFName, String strLName, String strUsername, String strEmail, String google_id){
        if (isLogin){
            showBottomSheetDialog(strFName, strLName, strUsername, strEmail, google_id);
            //signUpGoogle(strUsername,google_id);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);

        //Google login
        if (requestCode == REQ_CODE){
            GoogleSignInResult result  = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleResult(result);
        }

        super.onActivityResult(requestCode, responseCode, intent);
    }


    private void showBottomSheetDialog(final String strFName, final String strLName, final String strUsername, final String strEmail, final String google_id) {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_phone_number, null);

        et_phnone_botom = (EditText) view.findViewById(R.id.et_phnone_botom);
        et_coupon_social = (EditText) view.findViewById(R.id.et_coupon_social);
        cv_apply_coupon_social = (CardView) view.findViewById(R.id.cv_apply_coupon_social);
        img_remove_coupon_social = (TextView) view.findViewById(R.id.img_remove_coupon_social);
        promo_applied_text_social = (TextView) view.findViewById(R.id.promo_applied_text_social);
        txt_apply_social = (TextView) view.findViewById(R.id.txt_apply_social);
        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);
        card_submit_number = (CardView) view.findViewById(R.id.card_submit_number);

        cv_apply_coupon_social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cv_apply_coupon_social.setVisibility(View.VISIBLE);
                img_remove_coupon_social.setVisibility(View.GONE);
                et_coupon_social.setText("");
                promocode = "";
            }
        });

        txt_apply_social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPromoCode("social");
            }
        });

        (view.findViewById(R.id.img_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        card_submit_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card_submit_number.setEnabled(false);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                firstname = strFName;
                lastname = strLName;
                username  = strUsername;
                eMail = strEmail;
                mobileNumber = et_phnone_botom.getText().toString().trim();
                countryCode = ccp.getSelectedCountryCode().toString().trim();
                password = google_id;
                promocode = et_coupon_social.getText().toString().trim();
                strCodePhone=countryCode+mobileNumber;

                if (!countryCode.isEmpty() && !mobileNumber.isEmpty()) {
                    if (new ExtraOperations().haveNetworkConnection(SignUpActivity.this)) {
                        progressBar.setVisibility(View.VISIBLE);
                        try {
                            mBottomSheetDialog.dismiss();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                        Uri.Builder builder = Uri.parse(Constant.VERIFY_REGISTER_URL).buildUpon();
                        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                        builder.appendQueryParameter("username", username);
                        builder.appendQueryParameter("email", eMail);
                        builder.appendQueryParameter("mobile", mobileNumber);
                        builder.appendQueryParameter("device_id",strDeviceID);
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
                                        card_submit_number.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        disconnectFromFacebook();
                                        Snackbar snackbar = Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG);
                                        View view = snackbar.getView();
                                        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                                        params.gravity = Gravity.TOP;
                                        view.setLayoutParams(params);
                                        snackbar.show();
                                    }
                                    else if (success.equals("1")) {
                                        card_submit_number.setEnabled(true);
                                        submitData();
                                        /*progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(SignUpActivity.this,OTPActivity.class);
                                        intent.putExtra("fname", firstname);
                                        intent.putExtra("lname", lastname);
                                        intent.putExtra("uname", username);
                                        intent.putExtra("email", eMail);
                                        intent.putExtra("ccode", "+"+countryCode);
                                        intent.putExtra("phone", mobileNumber);
                                        intent.putExtra("pass", password);
                                        intent.putExtra("rcode", promocode);
                                        intent.putExtra("device",strDeviceID);
                                        intent.putExtra("tag","regulerSignUp");
                                        startActivity(intent);*/
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    card_submit_number.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                                card_submit_number.setEnabled(true);
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
                    } else {
                        card_submit_number.setEnabled(true);
                        Snackbar snackbar = Snackbar.make(parent_lyt,"No internet connection",Snackbar.LENGTH_LONG);
                        View view = snackbar.getView();
                        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                        params.gravity = Gravity.TOP;
                        view.setLayoutParams(params);
                        snackbar.show();
                    }
                }
                else {
                    card_submit_number.setEnabled(true);
                    Snackbar snackbar = Snackbar.make(parent_lyt,"Mobile Number should be of 8 to 13 Digits",Snackbar.LENGTH_LONG);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    snackbar.show();
                }
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        try {
            mBottomSheetDialog.show();
        }catch (WindowManager.BadTokenException e){
            e.printStackTrace();
        }
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    public void submitData() {
        if (new ExtraOperations().haveNetworkConnection(this)) {
            if (promocode.length() >= 2){
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
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
                                Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                onBackPressed();
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else if (success.equals("2")){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
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
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
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
                                Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
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

}
