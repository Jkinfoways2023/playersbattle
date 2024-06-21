package com.tournaments.grindbattles.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Map;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.TopPlayersAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.TopPlayersPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ActionAlertMessage;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;

public class TopPlayersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<TopPlayersPojo> topPlayersPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;

    private String id;
    private String username;
    private String token;
    TabLayout tabname;
    JSONObject object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_players);
        session = new SessionManager(getApplicationContext());

        initToolbar();
        initView();
        initSession();
        setdatas();
        topPlayersPojoList = new ArrayList<>();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadTopPlayers();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void setdatas()
    {
        tabname=findViewById(R.id.tabname);
        tabname.addTab(tabname.newTab().setText("Fulltime"));
        tabname.addTab(tabname.newTab().setText("Weekly"));
        tabname.addTab(tabname.newTab().setText("Monthly"));

        for (int i = 0; i < tabname.getTabCount(); i++) {
            View tab = ((ViewGroup) tabname.getChildAt(0)).getChildAt(i);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tab.getLayoutParams();
            layoutParams.setMargins(16, 0, 16, 0); // Add left and right margins (adjust as needed)
            tab.setLayoutParams(layoutParams);
            tabname.requestLayout();
        }

        // Set a listener for tab selection
        tabname.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        // Handle Tab 1 selected
                        if(object!=null)
                        {
                            try {
                                JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(object.getJSONArray("overall"));
                            } catch (JSONException e) {
                                Log.e("Exceptioisssss", "3"+String.valueOf(e));
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            Toast.makeText(TopPlayersActivity.this, "Feching data", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
                        // Handle Tab 2 selected
                        if(object!=null)
                        {
                            try {
                                JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(object.getJSONArray("weekly"));
                            } catch (JSONException e) {
                                Log.e("Exceptioisssss", "4"+String.valueOf(e));
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            Toast.makeText(TopPlayersActivity.this, "Feching data", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case 2:
                        // Handle Tab 3 selected
                        if(object!=null) {
                            try {
                                JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(object.getJSONArray("monthly"));
                            } catch (JSONException e) {
                                Log.e("Exceptioisssss", "2" + String.valueOf(e));
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                        Toast.makeText(TopPlayersActivity.this, "Feching data", Toast.LENGTH_SHORT).show();
                    }
                        break;
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
                // Handle tab unselected
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
                // Handle tab reselected
            }
        });
    }

    private void loadTopPlayers() {
        Uri.Builder builder = Uri.parse(Constant.TOP_PLAYERS_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        Log.e("urlissssss",builder.toString());
        /*jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });*/

        StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    object = new JSONObject(response);
                    JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(object.getJSONArray("overall"));

                } catch (JSONException e) {
                    Log.e("Exceptioisssss", "1"+String.valueOf(e));
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
        MySingleton.getInstance(TopPlayersActivity.this).addToRequestque(request);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
     /*   jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);*/
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(JSONArray response) {
        topPlayersPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            TopPlayersPojo topPlayersPojo = new TopPlayersPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //topPlayersPojo.setId(json.getString("id"));
                topPlayersPojo.setPubg_id(json.getString("pubg_id"));
                topPlayersPojo.setPrize(json.getInt("prize"));
            } catch (JSONException e) {

                Log.e("Exceptioisssss", "00"+String.valueOf(e));
                e.printStackTrace();
            }
            topPlayersPojoList.add(topPlayersPojo);
        }
        if (!topPlayersPojoList.isEmpty()){
            recyclerView.removeAllViews();
            adapter = new TopPlayersAdapter(topPlayersPojoList,getApplicationContext());
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
        ImageView backPress = (ImageView) findViewById(R.id.backPress);
        backPress.setOnClickListener(new View.OnClickListener() {
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
