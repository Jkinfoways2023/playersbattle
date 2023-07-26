package com.tournaments.grindbattles.activity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
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

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.LeaderboardReferAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.LeaderboardReferPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;

public class LeaderboardReferActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<LeaderboardReferPojo> leaderboardPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;

    private String id;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_refer);
        session = new SessionManager(getApplicationContext());

        initToolbar();
        initView();
        initSession();

        leaderboardPojoList = new ArrayList<>();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadLeaderboard();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadLeaderboard() {
        Uri.Builder builder = Uri.parse(Constant.TOP_LEADERS_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_LEADER_BOARD(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_LEADER_BOARD(JSONArray response) {
        leaderboardPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            LeaderboardReferPojo leaderboardPojo = new LeaderboardReferPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //topPlayersPojo.setId(json.getString("id"));
                leaderboardPojo.setFname(json.getString("fname"));
                leaderboardPojo.setLname(json.getString("lname"));
                leaderboardPojo.setRefer_points(json.getString("refer_points"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            leaderboardPojoList.add(leaderboardPojo);
        }
        if (!leaderboardPojoList.isEmpty()){
            adapter = new LeaderboardReferAdapter(leaderboardPojoList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }
    }

    private void initView() {
        this.recyclerView = (RecyclerView) findViewById(R.id.topPlayersListRecyclerView);
    }

    private void initSession() {
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "Refer Leader Board");
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
