package com.tournaments.grindbattles.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import com.tournaments.grindbattles.bottomsheet.BottomSheetPricePool;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.ParticipantsResultAdapter;
import com.tournaments.grindbattles.adapter.RunnerupAdapter;
import com.tournaments.grindbattles.adapter.WinnerAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.ParticipantPojo;
import com.tournaments.grindbattles.model.WinnerPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;

public class ResultDetailsActivity extends AppCompatActivity {

    private TextView dateTime;
    private TextView entryFee;
    private CardView notesCard;
    private CardView runnerUpsCard;
    private CardView betCard;
    private TextView notesText;
    private TextView notesTitle;
    private TextView perKill;
    private TextView title;
    private TextView winPrize;
    private LinearLayout prizePoolLL;

    private String matchID;
    private String matchMap;
    private String matchNotes;
    private String matchPerKill;
    private String matchStartTime;
    private String matchStatus;
    private String matchTitle;
    private String matchTopImage;
    private String matchType;
    private String matchVersion;
    private String joinedStatus;
    private String platform;
    private String pool_type;
    private String matchPrizePool;
    private String entryType;
    private String spectateURL;
    private String privateStatus;

    private int matchEntryFee;
    private int matchWinPrize;
    private int totalJoined;
    private int admin_share;

    private String id;
    private String username;
    private String token;

    private RecyclerView recyclerViewWinner;
    private RecyclerView.Adapter winnerAdapter;

    private ArrayList<WinnerPojo> winnerPojoList;
    private RequestQueue winnerRequestQueue;
    private JsonArrayRequest winnerArrayRequest ;

    private RecyclerView recyclerViewRunnerUps;
    private RecyclerView.Adapter runnerupAdapter;

    private ArrayList<WinnerPojo> runnerupPojoList;
    private RequestQueue runnerupRequestQueue;
    private JsonArrayRequest runnerupArrayRequest ;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter participantAdapter;

    private ArrayList<ParticipantPojo> participantPojoList;
    private RequestQueue participantRequestQueue;
    private JsonArrayRequest participantArrayRequest ;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_details);

        initToolbar();
        iniView();
        initSession();
        initIntent();

        this.winnerPojoList = new ArrayList();
        this.runnerupPojoList = new ArrayList();
        this.participantPojoList = new ArrayList();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.recyclerViewWinner.setHasFixedSize(true);
        this.recyclerViewWinner.setNestedScrollingEnabled(false);
        this.recyclerViewWinner.setLayoutManager(new LinearLayoutManager(this));

        this.recyclerViewRunnerUps.setHasFixedSize(true);
        this.recyclerViewRunnerUps.setNestedScrollingEnabled(false);
        this.recyclerViewRunnerUps.setLayoutManager(new LinearLayoutManager(this));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadWinner();
            loadRunnerup();
            loadParticipant();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.result_options, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != R.id.watch_match) {
            return super.onOptionsItemSelected(menuItem);
        }
        if (!spectateURL.startsWith("http://") && !spectateURL.startsWith("https://")){
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://"+spectateURL)));
        }
        else {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(spectateURL)));
        }
        return true;
    }

    private void loadParticipant() {
        Uri.Builder builder = Uri.parse(Constant.MATCH_FULL_RESULT_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("match_id",matchID);
        participantArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_PARTICIPANT(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        participantRequestQueue = Volley.newRequestQueue(getApplicationContext());
        participantArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        participantArrayRequest.setShouldCache(false);
        participantRequestQueue.getCache().clear();
        participantRequestQueue.add(participantArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_PARTICIPANT(JSONArray response) {
        participantPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            ParticipantPojo participantPojo = new ParticipantPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //participantPojo.setId(json.getString("id"));
                //participantPojo.setUser_id(json.getString("user_id"));
                participantPojo.setPubg_id(json.getString("pubg_id"));
                participantPojo.setKills(json.getInt("kills"));
                participantPojo.setPrize(json.getInt("prize"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            participantPojoList.add(participantPojo);
        }
        if (!participantPojoList.isEmpty()){
            participantAdapter = new ParticipantsResultAdapter(participantPojoList,getApplicationContext());
            participantAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(participantAdapter);
        }
    }

    private void loadWinner() {
        Uri.Builder builder = Uri.parse(Constant.MATCH_WINNER_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("match_id",matchID);
        winnerArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_WINNER(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        winnerRequestQueue = Volley.newRequestQueue(getApplicationContext());
        winnerArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        winnerArrayRequest.setShouldCache(false);
        winnerRequestQueue.getCache().clear();
        winnerRequestQueue.add(winnerArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_WINNER(JSONArray response) {
        winnerPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            WinnerPojo winnerPojo = new WinnerPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //winnerPojo.setId(json.getString("id"));
                //winnerPojo.setUser_id(json.getString("user_id"));
                winnerPojo.setPubg_id(json.getString("pubg_id"));
                winnerPojo.setKills(json.getInt("kills"));
                winnerPojo.setPosition(json.getInt("position"));
                winnerPojo.setPrize(json.getInt("prize"));
               
            } catch (JSONException e) {
                e.printStackTrace();
            }
            winnerPojoList.add(winnerPojo);
        }
        if (!winnerPojoList.isEmpty()){
            winnerAdapter = new WinnerAdapter(winnerPojoList,getApplicationContext());
            winnerAdapter.notifyDataSetChanged();
            recyclerViewWinner.setAdapter(winnerAdapter);
        }
    }

    private void loadRunnerup() {
        runnerUpsCard.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.MATCH_RUNNERUP_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("match_id",matchID);
        runnerupArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        runnerUpsCard.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_RUNNERUP(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        runnerupRequestQueue = Volley.newRequestQueue(getApplicationContext());
        runnerupArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        runnerupArrayRequest.setShouldCache(false);
        runnerupRequestQueue.getCache().clear();
        runnerupRequestQueue.add(runnerupArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_RUNNERUP(JSONArray response) {
        runnerupPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            WinnerPojo runnerupPojo = new WinnerPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //runnerupPojo.setId(json.getString("id"));
                //runnerupPojo.setUser_id(json.getString("user_id"));
                runnerupPojo.setPubg_id(json.getString("pubg_id"));
                runnerupPojo.setKills(json.getInt("kills"));
                runnerupPojo.setPosition(json.getInt("position"));
                runnerupPojo.setPrize(json.getInt("prize"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            runnerupPojoList.add(runnerupPojo);
        }
        if (!runnerupPojoList.isEmpty()){
            runnerupAdapter = new RunnerupAdapter(runnerupPojoList,getApplicationContext());
            runnerupAdapter.notifyDataSetChanged();
            recyclerViewRunnerUps.setAdapter(runnerupAdapter);
            runnerUpsCard.setVisibility(View.VISIBLE);
        }
        else {
            runnerUpsCard.setVisibility(View.GONE);
        }
    }

    private void initIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            matchEntryFee = extras.getInt("EntryFee_KEY");
            matchID = extras.getString("ID_KEY");
            matchMap = extras.getString("Map_KEY");
            matchNotes = extras.getString("Notes_KEY");
            matchPerKill = extras.getString("PerKill_KEY");
            matchStartTime = extras.getString("StartTime_KEY");
            matchStatus = extras.getString("Match_Status_KEY");
            matchTitle = extras.getString("Title_KEY");
            //matchTopImage = extras.getString("TopImage_KEY");
            matchType = extras.getString("Match_Type_KEY");
            entryType = extras.getString("Entry_Type_KEY");
            matchVersion = extras.getString("Version_KEY");
            matchWinPrize = extras.getInt("WinPrize_KEY");
            matchPrizePool = extras.getString("PrizePool_KEY");
            privateStatus = extras.getString("Private_Status_KEY");
            spectateURL = extras.getString("SpectateURL_KEY");
            joinedStatus = extras.getString("JOINED_STATUS_KEY");
            totalJoined = extras.getInt("TOTAL_JOINED_KEY");
            platform = extras.getString("PLATFORM_KEY");
            pool_type = extras.getString("POOL_TYPE_KEY");
            admin_share = extras.getInt("ADMIN_SHARE_KEY");

            title.setText(matchTitle);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Organised on ");
            stringBuilder.append(matchStartTime);
            dateTime.setText(stringBuilder.toString());

            if (pool_type.equals("1") && entryType.equals("Paid")) {
                int share = 100-admin_share;
                int pricepool = ((matchEntryFee*totalJoined)*share)/100;
                if (pricepool > matchWinPrize){
                    winPrize.setText(String.valueOf(pricepool));
                }
                else {
                    winPrize.setText(String.valueOf(matchWinPrize));
                }
            }
            else
            {
                winPrize.setText(String.valueOf(matchWinPrize));
            }

            perKill.setText(matchPerKill);

            prizePoolLL.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    new BottomSheetPricePool(matchTitle, matchPrizePool).show(getSupportFragmentManager(), "exampleBottomSheet");
                }
            });


            entryFee.setText(String.valueOf(matchEntryFee));

            if (matchNotes.length() >= 5) {
                notesText.setText(this.matchNotes);
                notesCard.setVisibility(View.VISIBLE);
            }
            else {
                notesCard.setVisibility(View.GONE);
            }

        }
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
    }

    private void iniView() {
        this.winPrize = (TextView) findViewById(R.id.winPrize);
        this.perKill = (TextView) findViewById(R.id.perKill);
        this.entryFee = (TextView) findViewById(R.id.entryFee);
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.recyclerViewWinner = (RecyclerView) findViewById(R.id.recyclerViewWinner);
        this.recyclerViewRunnerUps = (RecyclerView) findViewById(R.id.recyclerViewRunnerUps);
        this.title = (TextView) findViewById(R.id.matchTitle);
        this.dateTime = (TextView) findViewById(R.id.datetime);
        this.notesCard = (CardView) findViewById(R.id.specialNotesCard);
        this.runnerUpsCard = (CardView) findViewById(R.id.runnerUpsCard);
        this.notesText = (TextView) findViewById(R.id.notesText);
        this.notesTitle = (TextView) findViewById(R.id.noteTitle);
        this.prizePoolLL = (LinearLayout) findViewById(R.id.prizePoolLL);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "Match Result");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
