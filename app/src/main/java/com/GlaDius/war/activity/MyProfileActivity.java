package com.GlaDius.war.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.GlaDius.war.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import androidx.core.internal.view.SupportMenu;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import de.hdodenhof.circleimageview.CircleImageView;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText fname;
    private EditText lname;
    private EditText uname;
    private EditText mNumber;
    private EditText newPass;
    private EditText oldPass;
    private EditText retypeNewPass;
    private EditText eMail;
    private EditText countryCode;
    private TextView dobEt;

    private RadioGroup genderRg;
    private RadioButton maleRb;
    private RadioButton femaleRb;

    private Button saveButton;
    private Button resetPassButton;

    private TextView successText;
    private TextView successTextPassword;
    private TextView verifyNumber;

    private String id;
    private String firstname;
    private String lastname;
    private String username;
    private String oldPassword;
    private String newPassword;
    private String retypeNewPassword;
    private String mnumber;
    private String email;
    private String dobSt;
    private String genderSt;
    private String profileSt;
    private String ccode;
    String token;

    private SessionManager session;

    private CircleImageView profileIv;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        session = new SessionManager(getApplicationContext());

        initToolbar();
        initView();
        initListener();
        initPreference();

        loadProfile();

        this.uname.setText(this.username);
        this.eMail.setText(this.email);
        this.countryCode.setText(this.ccode);
        this.mNumber.setText(this.mnumber);

    }

    private void initToolbar() {
        ImageView backPress = (ImageView) findViewById(R.id.backPress);

       backPress.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
    }

    private void initListener() {
        this.dobEt.setOnClickListener(this);
        this.saveButton.setOnClickListener(this);
        this.resetPassButton.setOnClickListener(this);
        this.profileIv.setOnClickListener(this);
    }

    private void initPreference() {
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        ccode = user.get(SessionManager.KEY_CODE);
        mnumber = user.get(SessionManager.KEY_MOBILE);
        profileSt = user.get(SessionManager.KEY_PROFILE);
    }

    private void initView() {
        this.profileIv = (CircleImageView) findViewById(R.id.profileIv);
        this.fname = (EditText) findViewById(R.id.firstname);
        this.lname = (EditText) findViewById(R.id.lastname);
        this.uname = (EditText) findViewById(R.id.username);
        this.eMail = (EditText) findViewById(R.id.email);
        this.mNumber = (EditText) findViewById(R.id.mobileNumber);
        this.countryCode = (EditText) findViewById(R.id.countryCode);
        this.dobEt = (TextView) findViewById(R.id.dobEt);
        this.genderRg = (RadioGroup) findViewById(R.id.genderRg);
        this.maleRb = (RadioButton) findViewById(R.id.maleRb);
        this.femaleRb = (RadioButton) findViewById(R.id.femaleRb);
        this.saveButton = (Button) findViewById(R.id.saveBtn);
        this.oldPass = (EditText) findViewById(R.id.oldpass);
        this.newPass = (EditText) findViewById(R.id.newpass);
        this.retypeNewPass = (EditText) findViewById(R.id.retypeNewPass);
        this.resetPassButton = (Button) findViewById(R.id.changePassBtn);
        //this.successText = (TextView) findViewById(R.id.messageView);
        this.successTextPassword = (TextView) findViewById(R.id.passwordMessageView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id==R.id.dobEt){
            Calendar calendar = Calendar.getInstance();
            int date = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(dayOfMonth);
                    stringBuilder.append("/");
                    stringBuilder.append(month + 1);
                    stringBuilder.append("/");
                    stringBuilder.append(year);
                    dobEt.setText(stringBuilder.toString());
                }
            }, calendar.get(Calendar.YEAR), month, date);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        }
        if (id==R.id.profileIv){
            onSelectImageClick();
        }
        else if (id==R.id.saveBtn){
            this.firstname = this.fname.getText().toString();
            this.lastname = this.lname.getText().toString();
            this.mnumber = this.mNumber.getText().toString();
            this.email = this.eMail.getText().toString();
            this.ccode = this.countryCode.getText().toString();
            this.dobSt = this.dobEt.getText().toString();
            if (this.maleRb.isChecked()) {
                this.genderSt = "m";
            } else if (this.femaleRb.isChecked()) {
                this.genderSt = "f";
            }
            submitProfileUpdateData();
            //Toast.makeText(this, "This feature is disable in demo app", Toast.LENGTH_SHORT).show();
        }
        else if (id==R.id.changePassBtn){
            this.oldPassword = this.oldPass.getText().toString();
            this.newPassword = this.newPass.getText().toString();
            this.retypeNewPassword = this.retypeNewPass.getText().toString();
            submitUpdatePasswordData();
            //Toast.makeText(this, "This feature is disable in demo app", Toast.LENGTH_SHORT).show();
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (bmp != null){
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        }
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void loadProfile() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.GET_PROFILE_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("id", id);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            firstname = jsonObject1.getString("fname");
                            lastname = jsonObject1.getString("lname");
                            profileSt = jsonObject1.getString("user_profile");
                            genderSt = jsonObject1.getString("gender");
                            dobSt = jsonObject1.getString("dob");

                            fname.setText(firstname);
                            lname.setText(lastname);
                            try {
                                if (!dobSt.equals("null")) {
                                    dobEt.setText(dobSt);
                                }
                                else {
                                    dobEt.setText("");
                                }
                                try {
                                    if (genderSt.equals("m")) {
                                        maleRb.setChecked(true);
                                    }
                                    else if (genderSt.equals("f")) {
                                        femaleRb.setChecked(true);
                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }

                            if (!profileSt.equals("null")){
                                Glide.with(getApplicationContext()).load(Config.FILE_PATH_URL+profileSt)
                                        .apply(new RequestOptions().override(120,120))
                                        .apply(new RequestOptions().placeholder(R.drawable.profile).error(R.drawable.profile))
                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                        .apply(RequestOptions.skipMemoryCacheOf(true))
                                        .into(profileIv);
                            }

                            session.createLoginSession(id,profileSt,firstname,lastname,username,session.getUserDetails().get(SessionManager.KEY_PASSWORD),email,countryCode.getText().toString(),session.getUserDetails().get(SessionManager.KEY_MOBILE),session.getUserDetails().get(SessionManager.ACCESS_TOKEN));
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
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
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void submitProfileUpdateData() {
        if (validateFirstName() && validateLastName() && validateEmail()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                Uri.Builder builder = Uri.parse(Constant.UPDATE_PROFILE_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                builder.appendQueryParameter("id",id);
                builder.appendQueryParameter("fname",firstname);
                builder.appendQueryParameter("lname",lastname);
                builder.appendQueryParameter("gender",genderSt);
                builder.appendQueryParameter("dob",dobSt);
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
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                session.createLoginSession(id,profileSt,firstname,lastname,username,token,email,ccode,mnumber,session.getUserDetails().get(SessionManager.ACCESS_TOKEN));
                                Intent intent = new Intent(MyProfileActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else if (success.equals("2")) {
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validateFirstName() {
        if (this.firstname.isEmpty()) {
            this.fname.setError("Enter First Name");
            return false;
        } else if (this.firstname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            this.fname.setError("Enter valid first name");
            return false;
        }
    }

    public boolean validateLastName() {
        if (this.lastname.isEmpty()) {
            this.lname.setError("Enter Last Name");
            return false;
        } else if (this.lastname.matches("[a-zA-Z]*")) {
            return true;
        } else {
            this.lname.setError("Enter valid last name");
            return false;
        }
    }

    public boolean validateMobileNumber() {
        if (this.mnumber.length() == 10) {
            return true;
        }
        this.mNumber.setError("Mobile Number should be of 10 Digits");
        return false;
    }

    public boolean isvalideEmail(String str) {
        return Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public boolean validateEmail() {
        email = this.eMail.getText().toString();
        if (!email.isEmpty()) {
            if (isvalideEmail(email)) {
                return true;
            }
        }
        this.eMail.setError("Enter Valid Email Address");
        return false;
    }

    public void submitUpdatePasswordData() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (validatePassword()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                Uri.Builder builder = Uri.parse(Constant.UPDATE_PROFILE_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                builder.appendQueryParameter("id", id);
                builder.appendQueryParameter("password", newPassword);
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
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                            else  if (success.equals("1")) {
                                session.createLoginSession(id,profileSt,firstname,lastname,username,newPassword,email,ccode,mnumber,session.getUserDetails().get(SessionManager.ACCESS_TOKEN));
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                                try {
                                    Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }
                            else if (success.equals("2")) {
                                Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
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
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean validatePassword() {
        if (this.oldPassword.isEmpty()) {
            this.oldPass.setError("Enter Password");
            return false;
        } else if (this.newPassword.length() < 8 || this.newPassword.length() > 20) {
            this.newPass.setError("Password should be of 8 to 20 Digits/Characters");
            return false;
        } else if (this.retypeNewPassword.isEmpty()) {
            this.retypeNewPass.setError("Re-enter New Password");
            return false;
        } else if (!this.oldPassword.equals(this.token)) {
            this.successTextPassword.setText("Old Password is Incorrect.");
            this.successTextPassword.setTextColor(SupportMenu.CATEGORY_MASK);
            this.successTextPassword.setVisibility(View.VISIBLE);
            return false;
        } else if (this.retypeNewPassword.equals(this.newPassword)) {
            return true;
        } else {
            this.successTextPassword.setText("New Passwords don't match. Retry!");
            this.successTextPassword.setTextColor(SupportMenu.CATEGORY_MASK);
            this.successTextPassword.setVisibility(View.VISIBLE);
            return false;
        }
    }

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            final Uri saveUri = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), saveUri);
                //Setting the Bitmap to ImageView
                profileSt = getStringImage(bitmap);
                profileIv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (saveUri != null) {
                final ProgressDialog mDialog = new ProgressDialog(this);
                mDialog.setMessage("Uploading...");
                mDialog.show();

                StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_PHOTO_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            String success = jsonObject.getString("success");
                            String message = jsonObject.getString("msg");

                            if (success.equals("1")) {
                                mDialog.dismiss();
                                onBackPressed();
                                Toast.makeText(getApplicationContext(), "Your profile photo has been updated.", Toast.LENGTH_LONG).show();
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed!!! Please try again.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mDialog.dismiss();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("access_key", Config.PURCHASE_CODE);
                        parameters.put("id", id);
                        parameters.put("user_profile", profileSt);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(this).addToRequestque(request);
            }
        }
    }

}
