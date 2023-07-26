package com.tournaments.grindbattles.activity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
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

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.RewardAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.RewardPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;

public class MyRewardsActivity extends AppCompatActivity {

    private LinearLayout noRewards;
    private TextView noRewardsText;
    private RecyclerView recyclerView;
    private List<RewardPojo> rewardPojoList;
    private LinearLayout rewardsListLL;

    private TextView totalEarnings;
    private TextView totalRewards;

    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;

    private String id;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rewards);
        session = new SessionManager(getApplicationContext());

        initToolbar();

        this.totalRewards = (TextView) findViewById(R.id.rewardsCount);
        this.totalEarnings = (TextView) findViewById(R.id.earningsCount);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.noRewards = (LinearLayout) findViewById(R.id.noRewardsLL);
        this.rewardsListLL = (LinearLayout) findViewById(R.id.rewardsListLL);
        this.noRewardsText = (TextView) findViewById(R.id.noRewardsText);

        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);

        this.rewardPojoList = new ArrayList();
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
        Uri.Builder builder = Uri.parse(Constant.REWARDED_SUMMARY_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("username", username);
        StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("result");
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);

                    String success = jsonObject1.getString("success");

                    if (success.equals("1")) {
                        totalRewards.setText(jsonObject1.getString("rewards"));
                        if (jsonObject1.getString("earnings").equals("null")){
                            totalEarnings.setText("0");
                        }
                        else {
                            totalEarnings.setText(jsonObject1.getString("earnings"));
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
        rewardsListLL.setVisibility(View.VISIBLE);
        noRewards.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.REWARDED_LIST_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("username", username);
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
                        rewardsListLL.setVisibility(View.GONE);
                        noRewards.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MY_REFERRAL(JSONArray response) {
        rewardPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            RewardPojo rewardPojo = new RewardPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                rewardPojo.setReward_date(json.getString("reward_date"));
                rewardPojo.setReward_count(json.getString("reward_count"));
                rewardPojo.setReward_points(json.getString("reward_points"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            rewardPojoList.add(rewardPojo);
        }
        if (!rewardPojoList.isEmpty()){
            adapter = new RewardAdapter(rewardPojoList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            rewardsListLL.setVisibility(View.VISIBLE);
            noRewards.setVisibility(View.GONE);
        }
        else {
            rewardsListLL.setVisibility(View.GONE);
            noRewards.setVisibility(View.VISIBLE);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "My Rewarded");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
