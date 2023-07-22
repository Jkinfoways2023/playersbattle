package com.GlaDius.war.activity;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.GlaDius.war.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.GlaDius.war.R;
import com.GlaDius.war.adapter.MyStatisticsAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.model.StatisticsPojo;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;

public class MyStatisticsActivity extends AppCompatActivity {

    private LinearLayout noStats;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<StatisticsPojo> statisticsPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private SharedPreferences sharedPreferences;
    private LinearLayout stats;

    private String id;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_statistics);

        initToolbar();
        initView();
        initSession();

        statisticsPojoList = new ArrayList<>();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadMyStatistics();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMyStatistics() {
        stats.setVisibility(View.VISIBLE);
        noStats.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.MY_STATISTICS_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("user_id", id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_STATISTICS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        stats.setVisibility(View.GONE);
                        noStats.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_STATISTICS(JSONArray response) {
        statisticsPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            StatisticsPojo statisticsPojo = new StatisticsPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                statisticsPojo.setId(json.getString("id"));
                statisticsPojo.setTitle(json.getString("title"));
                statisticsPojo.setTime(json.getString("time"));
                statisticsPojo.setEntry_fee(json.getInt("entry_fee"));
                statisticsPojo.setPrize(json.getInt("prize"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            statisticsPojoList.add(statisticsPojo);
        }
        if (!statisticsPojoList.isEmpty()){
            adapter = new MyStatisticsAdapter(statisticsPojoList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            stats.setVisibility(View.VISIBLE);
            noStats.setVisibility(View.GONE);
        }
        else {
            stats.setVisibility(View.GONE);
            noStats.setVisibility(View.VISIBLE);
        }
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
    }

    private void initView() {
        this.recyclerView = (RecyclerView) findViewById(R.id.matchListRecyclerView);
        this.stats = (LinearLayout) findViewById(R.id.statsLL);
        this.noStats = (LinearLayout) findViewById(R.id.noStatsLL);
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
}
