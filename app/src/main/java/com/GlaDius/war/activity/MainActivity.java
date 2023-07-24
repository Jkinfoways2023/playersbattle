package com.GlaDius.war.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.GlaDius.war.MyApplication;
import com.GlaDius.war.fragment.LotteryFragment;
import com.GlaDius.war.fragment.MyAccountFragment;
import com.GlaDius.war.fragment.TransactionsFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.GlaDius.war.BuildConfig;
import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.fragment.EarnFragment;
import com.GlaDius.war.fragment.GameFragment;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public Fragment fragment;
    public LinearLayout homeLayout;
    public LinearLayout myContestLayout;
    public LinearLayout myAccountLayout;

    private Toolbar toolbar;
    private ImageView tool_logo;
    private LinearLayout toolbalance;
    private FrameLayout newNotiIcon;

    public static LinearLayout bottomView;
    public static TextView toolwallet;
    public TextView cart_badge;

    private SessionManager session;
    private String force_update, whats_new, update_date, latest_version_name, update_url;
    private int balance,won,bonus;
    private String id, isBlocked;

    public static BottomNavigationView navigation;
    public boolean doubleBackToExitPressedOnce = false;

    public SharedPreferences preferences;
    public String prefName = "Sky_Winner";
    public int count = 0;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initSession();

        getCounter();
        MyApplication.getInstance().testsignin();
        getupikey();
        //initViews();
        //initListeners();

        loadUpdate();
        loadProfile();

        navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_recent:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new EarnFragment()).commit();
                        return true;
                    case R.id.navigation_favorites:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, new GameFragment()).commit();
                        return true;
                    case R.id.navigation_nearby:
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,new MyAccountFragment()).commit();
                        return true;
                    case R.id.navigation_lottery:
                        Bundle bundle = new Bundle();
                        bundle.putString("isFrom", "MainActivity");
                        LotteryFragment  lotteryFragment = new LotteryFragment();
                        lotteryFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,lotteryFragment).commit();
                        return true;
                    case R.id.navigation_history: {
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("isFrom", "MainActivity");
                        TransactionsFragment transactionsFragment = new TransactionsFragment();
                        transactionsFragment.setArguments(bundle1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, transactionsFragment).commit();
                        return true;
                    }
                }
                return false;
            }
        });

        if (savedInstanceState ==  null){
            addHomeFragment();
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void loadUpdate() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.UPDATE_APP_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);
                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            force_update = jsonObject1.getString("force_update");
                            whats_new = jsonObject1.getString("whats_new");
                            update_date = jsonObject1.getString("update_date");
                            latest_version_name = jsonObject1.getString("latest_version_name");
                            update_url = jsonObject1.getString("update_url");
                            try {
                                if (BuildConfig.VERSION_CODE < Integer.parseInt(latest_version_name)) {
                                    if (force_update.equals("1")) {
                                        Intent intent = new Intent(MainActivity.this, UpdateAppActivity.class);
                                        intent.putExtra("forceUpdate", force_update);
                                        intent.putExtra("whatsNew", whats_new);
                                        intent.putExtra("updateDate", update_date);
                                        intent.putExtra("latestVersionName", latest_version_name);
                                        intent.putExtra("updateURL", update_url);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else if (force_update.equals("0")) {
                                        Intent intent = new Intent(MainActivity.this, UpdateAppActivity.class);
                                        intent.putExtra("forceUpdate", force_update);
                                        intent.putExtra("whatsNew", whats_new);
                                        intent.putExtra("updateDate", update_date);
                                        intent.putExtra("latestVersionName", latest_version_name);
                                        intent.putExtra("updateURL", update_url);
                                        startActivity(intent);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
                            balance = jsonObject1.getInt("cur_balance");
                            won = jsonObject1.getInt("won_balance");
                            bonus = jsonObject1.getInt("bonus_balance");
                            isBlocked = jsonObject1.getString("status");
                            try {
                                toolwallet.setText(String.valueOf(balance));
                            }catch (NullPointerException | NumberFormatException e ) {
                                toolwallet.setText("0");
                                e.printStackTrace();
                            }
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

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.my_wallet_appbar);
        toolbalance = (LinearLayout) toolbar.findViewById(R.id.toolbalance);
        tool_logo = (ImageView) toolbar.findViewById(R.id.tool_logo);
        toolwallet = (TextView) toolbar.findViewById(R.id.toolwallet);
        newNotiIcon = (FrameLayout) toolbar.findViewById(R.id.newNotiIcon);
        cart_badge = (TextView) toolbar.findViewById(R.id.cart_badge);

        toolbalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyWalletActivity.class);
                startActivity(intent);
            }
        });

        newNotiIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCounter();

                Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        tool_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(Config.WEB_URL);
            }
        });
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);

        OneSignal.sendTag("User_ID", user.get(SessionManager.KEY_USERNAME));
        OneSignal.setExternalUserId(user.get(SessionManager.KEY_USERNAME));
    }

    private void initViews() {
        this.bottomView = (LinearLayout) findViewById(R.id.bottom_navigation);
        this.myContestLayout = (LinearLayout) this.bottomView.findViewById(R.id.myContestLayout);
        this.homeLayout = (LinearLayout) this.bottomView.findViewById(R.id.homeLayout);
        this.myAccountLayout = (LinearLayout) this.bottomView.findViewById(R.id.myAccountLayout);
    }

    private void initListeners() {
        this.myContestLayout.setTag("Earn");
        this.homeLayout.setTag("Home");
        this.myAccountLayout.setTag("Me");
    }

    private void addHomeFragment() {
        GameFragment gameFragment = new GameFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, gameFragment)
                .commit();

        navigation.getMenu().getItem(0).setChecked(true);
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void saveCounter() {
        count = 0;

        preferences = getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("counter", count);
        editor.apply();
    }

    public void getCounter() {
        preferences = getSharedPreferences(prefName, 0);
        count = preferences.getInt("counter", 0);
        cart_badge.setText(""+count);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCounter();
        initSession();
        loadProfile();
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) { finish(); return; }
        doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() { @Override public void run() { doubleBackToExitPressedOnce = false; }}, 1500);

    }

    public void getupikey()
    {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.get_upi_data).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);
                        session.setstringdata(session.upi_gateway_key,jsonObject1.getString("upi_gateway_key"));
                        Log.e("keyissss",jsonObject1.getString("upi_gateway_key"));
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
}
