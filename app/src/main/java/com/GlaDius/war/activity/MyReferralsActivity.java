package com.GlaDius.war.activity;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.GlaDius.war.R;
import com.GlaDius.war.adapter.ReferralAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.model.ReferralPojo;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;

public class MyReferralsActivity extends AppCompatActivity {

    LinearLayout noRefers;
    TextView noRefersText;
    RecyclerView recyclerView;
    List<ReferralPojo> referralList;
    LinearLayout refersList;
    SharedPreferences sharedPreferences;

    TextView totalEarnings;
    TextView totalRefers;

    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;

    String id;
    String username;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referrals);
        session = new SessionManager(getApplicationContext());

        initToolbar();

        this.totalRefers = (TextView) findViewById(R.id.referralsCount);
        this.totalEarnings = (TextView) findViewById(R.id.earnings);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.noRefers = (LinearLayout) findViewById(R.id.noRefersLL);
        this.refersList = (LinearLayout) findViewById(R.id.refersListLL);
        this.noRefersText = (TextView) findViewById(R.id.noRefersText);

        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);

        this.referralList = new ArrayList();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            ReferralSummary();
            ReferralsList();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }


    }

    private void ReferralSummary() {
        Uri.Builder builder = Uri.parse(Constant.REFERRAL_SUMMARY_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("refer_code", username);
        StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("result");
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);

                    String success = jsonObject1.getString("success");

                    if (success.equals("1")) {
                        totalRefers.setText(jsonObject1.getString("refer_code"));
                        if (jsonObject1.getString("refer_points").equals("null")){
                            totalEarnings.setText("0");
                        }
                        else {
                            totalEarnings.setText(jsonObject1.getString("refer_points"));
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
    }

    private void ReferralsList() {
        refersList.setVisibility(View.VISIBLE);
        noRefers.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.REFERRAL_LIST_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("refer_code", username);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MY_REFERRAL(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        refersList.setVisibility(View.GONE);
                        noRefers.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MY_REFERRAL(JSONArray response) {
        referralList.clear();
        for(int i = 0; i<response.length(); i++) {
            ReferralPojo referralPojo = new ReferralPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                referralPojo.setFname(json.getString("fname"));
                referralPojo.setLname(json.getString("lname"));
                referralPojo.setRefer_date(json.getString("refer_date"));
                referralPojo.setRefer_status(json.getString("refer_status"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            referralList.add(referralPojo);
        }
        if (!referralList.isEmpty()){
            adapter = new ReferralAdapter(referralList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            refersList.setVisibility(View.VISIBLE);
            noRefers.setVisibility(View.GONE);
        }
        else {
            refersList.setVisibility(View.GONE);
            noRefers.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle((CharSequence) "My Referrals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
