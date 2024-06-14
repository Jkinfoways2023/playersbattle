package com.tournaments.grindbattles.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.tournaments.grindbattles.adapter.ParticipantsSlotListAdapter;
import com.tournaments.grindbattles.views.Tools;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.ParticipantsListAdapter;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.ParticipantPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.bottomsheet.BottomSheetMyEntries;

public class PlayDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing_toolbar;

    private int matchEntryFee,matchPerKill,matchWinPrize,roomSize,totalJoined,admin_share;
    private String matchID, matchMap,matchRules,matchStartTime,currentTime,matchStatus,matchTitle,matchTopImage, matchPrizePool,isCanceled,canceledDesc,secretCode;
    private String matchType,matchVersion,entryType,spectateURL,privateStatus,matchIds,roomID,roomPass,slotNo,joinedStatus,userJoined, platform, pool_type,betStatus,game_type;

    private NestedScrollView nestedScrollView;
    private TextView titleMatchIDPass;
    private TextView roomIDPassUpcoming;
    private RelativeLayout roomIDPassRL;
    private TextView room_ID;
    private TextView room_Password;
    private TextView matchSlot;
    private WebView listRulesDetails;
    private LinearLayout loadBtnLL;
    RelativeLayout roomIDPasswordsRL;

    private ImageView topImage;
    private TextView fee,map,type,version,winPrize,fullPrizeText,startTime,startTimer,title,noParticipants,perKillPrize,refreshLV,matchtype;
    private Button entryButton,joinButton,loadParticipants;

    private RecyclerView lvParticipants;
    private RecyclerView.Adapter adapter;
    private LinearLayout linearLayout;

    private ArrayList<ParticipantPojo> participantPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String id,name,firstname,lastname,email,mnumber,username,password;

    private long mDays = 0;
    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private long mMilliSeconds = 0;

    private CharSequence mPrefixText;
    private CharSequence mSuffixText;

    private TimerListener mListener;
    private CountDownTimer mCountDownTimer;

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_details);

        initView();
        initSession();
        initToolbar();
        initIntent();

        participantPojoList = new ArrayList<>();
        lvParticipants.setHasFixedSize(true);
        lvParticipants.setLayoutManager(new LinearLayoutManager(this));

        loadParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                    loadParticipants();
                    loadBtnLL.setVisibility(View.GONE);
                    lvParticipants.setVisibility(View.VISIBLE);
                    refreshLV.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refreshLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                    loadParticipants();
                    loadBtnLL.setVisibility(View.GONE);
                    lvParticipants.setVisibility(View.VISIBLE);
                    refreshLV.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        entryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheetMyEntries(matchID,id,password,roomID,isCanceled).show(getSupportFragmentManager(), "myBidsBottomSheet");
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoiningMatchActivity.class);
                intent.putExtra("matchType", matchType);
                intent.putExtra("matchID",matchID);
                intent.putExtra("matchName",matchTitle);
                intent.putExtra("entryFee", matchEntryFee);
                intent.putExtra("entryType", entryType);
                intent.putExtra("JoinStatus",joinedStatus);
                intent.putExtra("isPrivate", privateStatus);
                intent.putExtra("matchRules", matchRules);
                intent.putExtra("ROOM_SIZE_KEY", roomSize);
                intent.putExtra("TOTAL_JOINED_KEY", totalJoined);
                startActivity(intent);
            }
        });

        room_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(room_ID.getText().toString());
            }
        });

        room_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(room_Password.getText().toString());
            }
        });

        matchSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(matchSlot.getText().toString());
            }
        });


        try {
            currentTime = "1579319761";
            matchStartTime = "1579551620";
            int time = Integer.parseInt(matchStartTime) - Integer.parseInt(currentTime);
            //setTime(time * 1000);
            //startCountDown();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    // Copy EditCopy text to the ClipBoard
    private void copyToClipBoard(String s) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", s);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Text Copied", Toast.LENGTH_SHORT).show();
    }


    private void initToolbar() {
        Tools.setSystemBarColor(this, R.color.statusBar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setCollapsedTitleTextAppearance(R.style.personal_collapsed_title);
        collapsing_toolbar.setExpandedTitleTextAppearance(R.style.personal_expanded_title);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);

        name = firstname+" "+lastname;
    }

    private void loadParticipants() {
        loadBtnLL.setVisibility(View.GONE);
        lvParticipants.setVisibility(View.VISIBLE);
        refreshLV.setVisibility(View.VISIBLE);
        Uri.Builder builder = Uri.parse(Constant.PARTICIPANTS_MATCH_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("match_id", matchID);
        Log.e("urlisssssss",builder.toString());
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_PARTICIPANTS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        noParticipants.setVisibility(View.VISIBLE);
                        lvParticipants.setVisibility(View.GONE);
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_PARTICIPANTS(JSONArray response) {
        participantPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            ParticipantPojo participantPojo = new ParticipantPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                //participantPojo.setId(json.getString("id"));
                //participantPojo.setUser_id(json.getString("user_id"));
                participantPojo.setPubg_id(json.getString("pubg_id"));
                participantPojo.setSlot(json.getInt("slot"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            participantPojoList.add(participantPojo);
        }
        if (!participantPojoList.isEmpty()){
            if(game_type.equalsIgnoreCase("1"))
            {
                adapter = new ParticipantsListAdapter(participantPojoList,this);
                adapter.notifyDataSetChanged();
                lvParticipants.setAdapter(adapter);
            }
            else{
                adapter = new ParticipantsSlotListAdapter(participantPojoList,this);
                adapter.notifyDataSetChanged();
                lvParticipants.setAdapter(adapter);
            }

        }
        else {
            noParticipants.setVisibility(View.VISIBLE);
            lvParticipants.setVisibility(View.GONE);
        }
    }

    private void initIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            matchEntryFee = extras.getInt("EntryFee_KEY");
            matchID = extras.getString("ID_KEY");
            matchMap = extras.getString("Map_KEY");
            matchRules = extras.getString("Rules_KEY");
            matchPerKill = extras.getInt("PerKill_KEY");
            matchStartTime = extras.getString("StartTime_KEY");
            matchStatus = extras.getString("Match_Status_KEY");
            matchTitle = extras.getString("Title_KEY");
            matchTopImage = extras.getString("TopImage_KEY");
            matchType = extras.getString("Match_Type_KEY");
            entryType = extras.getString("Entry_Type_KEY");
            matchVersion = extras.getString("Version_KEY");
            matchWinPrize = extras.getInt("WinPrize_KEY");
            matchPrizePool = extras.getString("PrizePool_KEY");
            privateStatus = extras.getString("Private_Status_KEY");
            spectateURL = extras.getString("SpectateURL_KEY");
            matchIds = extras.getString("MATCH__KEY");
            roomID = extras.getString("ROOM_ID_KEY");
            roomPass = extras.getString("ROOM_PASS_KEY");
            roomSize = extras.getInt("ROOM_SIZE_KEY");
            totalJoined = extras.getInt("TOTAL_JOINED_KEY");
            joinedStatus = extras.getString("JOINED_STATUS_KEY");
            userJoined = extras.getString("USER_JOINED_KEY");
            isCanceled = extras.getString("IS_CANCELED_KEY");
            canceledDesc = extras.getString("CANCELED_DESC_KEY");
            secretCode = extras.getString("SECRET_CODE_KEY");
            platform = extras.getString("PLATFORM_KEY");
            pool_type = extras.getString("POOL_TYPE_KEY");
            admin_share = extras.getInt("ADMIN_SHARE_KEY");
            slotNo = String.valueOf(extras.getInt("SLOT_KEY"));
            betStatus = extras.getString("BET_STATUS_KEY");
            game_type = String.valueOf(extras.getInt("GAME_TYPE"));

            toolbar.setTitle((CharSequence) matchTitle);
            this.title.setText(this.matchTitle);
            this.startTime.setText(this.matchStartTime);
            this.type.setText(matchType);
            this.version.setText(this.matchVersion);
            this.map.setText(this.matchMap);
            this.matchtype.setText(this.entryType);

            this.listRulesDetails.setBackgroundColor(0);
            this.listRulesDetails.loadData(Base64.encodeToString(matchRules.getBytes(), Base64.NO_PADDING),"text/html", "base64");

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

            perKillPrize.setText(String.valueOf(matchPerKill));

            if (this.matchPrizePool != null){
                if (  android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                {
                    this.fullPrizeText.setText(Html.fromHtml(this.matchPrizePool ,Html.FROM_HTML_MODE_LEGACY));
                }
                else {
                    this.fullPrizeText.setText(Html.fromHtml(this.matchPrizePool));
                }
            }

            try {
                if (!matchTopImage.equals("null")) {
                    Picasso.get().load(Config.FILE_PATH_URL+matchTopImage).resize(720,480).placeholder(R.drawable.pubg_banner).into(topImage);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (!(entryType.equals("Free") || entryType.equals("Sponsored") || entryType.equals("Giveaway"))) {
                fee.setText(String.valueOf(matchEntryFee));

                if (!roomID.isEmpty() && !joinedStatus.equals("null") && !isCanceled.equals("1")) {
                    room_ID.setText(roomID);
                    room_Password.setText(roomPass);
                    matchSlot.setText(slotNo);
                    roomIDPassUpcoming.setVisibility(View.GONE);
                    roomIDPasswordsRL.setVisibility(View.VISIBLE);
                    //RoomDetailsAlertDialog(PlayDetailsActivity.this);
                }

            }
            else {
                fee.setTextColor(Color.parseColor("#1E7E34"));
                fee.setText("FREE");
            }

            try {
                if (isCanceled.equals("1")){
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    joinButton.setText("Match CANCELED");
                    joinButton.setBackgroundColor(Color.parseColor("#757575"));
                    joinButton.setClickable(false);
                    joinButton.setFocusable(false);
                    titleMatchIDPass.setText("Match Canceled Details");
                    roomIDPassUpcoming.setText(this.canceledDesc);
                } else if (userJoined.equals("0") && totalJoined < roomSize) {
                    joinButton.setText("Join Now");
                    joinButton.setEnabled(true);
                    joinButton.setClickable(true);
                    roomIDPassRL.setVisibility(View.GONE);
                    roomIDPassUpcoming.setVisibility(View.GONE);
                }
                else if (!userJoined.equals("1") && matchType.equals("Solo") && totalJoined < roomSize) {
                    joinButton.setText("+ Add New Entry");
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    titleMatchIDPass.setText("Match Room Details");
                    roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }
                else if (!userJoined.equals("2") && matchType.equals("Duo") && totalJoined < roomSize) {
                    joinButton.setText("+ Add New Entry");
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    titleMatchIDPass.setText("Match Room Details");
                    roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }
                else if (!userJoined.equals("4") && matchType.equals("Squad") && totalJoined < roomSize) {
                    joinButton.setText("+ Add New Entry");
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    titleMatchIDPass.setText("Match Room Details");
                    roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }
                else if (userJoined.equals("1") && matchType.equals("Solo") && totalJoined < roomSize) {
                    joinButton.setText("Already Joined");
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    titleMatchIDPass.setText("Match Room Details");
                    roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }
                else if (userJoined.equals("2") && matchType.equals("Duo") && totalJoined < roomSize) {
                    joinButton.setText("Already Joined");
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    titleMatchIDPass.setText("Match Room Details");
                    roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }
                else if (userJoined.equals("4") && matchType.equals("Squad") && totalJoined < roomSize) {
                    joinButton.setText("Already Joined");
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    titleMatchIDPass.setText("Match Room Details");
                    roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }
                else if (totalJoined >= roomSize) {
                    joinButton.setText("Match Full");
                    joinButton.setBackgroundColor(Color.parseColor("#757575"));
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                }

                if (!roomID.isEmpty() && !joinedStatus.equals("null") && !isCanceled.equals("1")) {
                    room_ID.setText(roomID);
                    room_Password.setText(roomPass);
                    //matchSlot.setText(slotNo);
                    roomIDPassUpcoming.setVisibility(View.GONE);
                    roomIDPasswordsRL.setVisibility(View.VISIBLE);
                    //RoomDetailsAlertDialog(PlayDetailsActivity.this);
                }

            }catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }

    private void initView() {
        setContentView((int) R.layout.activity_play_details);
        this.title = (TextView) findViewById(R.id.title);
        this.type = (TextView) findViewById(R.id.type);
        this.version = (TextView) findViewById(R.id.version);
        this.map = (TextView) findViewById(R.id.map);
        this.matchtype = (TextView) findViewById(R.id.matchType);
        this.fee = (TextView) findViewById(R.id.fee);
        this.winPrize = (TextView) findViewById(R.id.winnerPrize);
        this.fullPrizeText = (TextView) findViewById(R.id.fullPrizeText);
        this.perKillPrize = (TextView) findViewById(R.id.perKillPrize);
        this.loadParticipants = (Button) findViewById(R.id.loadBtn);
        this.loadBtnLL = (LinearLayout) findViewById(R.id.LLloadBtn);
        this.refreshLV = (TextView) findViewById(R.id.refreshLVBtn);
        this.startTime = (TextView) findViewById(R.id.startdate);
        this.startTimer = (TextView) findViewById(R.id.starttimer);
        this.noParticipants = (TextView) findViewById(R.id.noParticipantsText);
        this.nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);
        this.joinButton = (Button) findViewById(R.id.joinButton);
        this.entryButton = (Button) findViewById(R.id.entryButton);
        this.roomIDPassUpcoming = (TextView) findViewById(R.id.roomIDPassUpcoming);
        this.titleMatchIDPass = (TextView) findViewById(R.id.titleMatchIDPass);
        this.roomIDPassRL = (RelativeLayout) findViewById(R.id.matchIDPassLL);
        this.roomIDPasswordsRL = (RelativeLayout) findViewById(R.id.roomIDPassRL);
        this.room_ID = (TextView) findViewById(R.id.roomIDValue);
        this.room_Password = (TextView) findViewById(R.id.roomPassValue);
        this.matchSlot = (TextView) findViewById(R.id.matchSlotValue);
        this.lvParticipants = (RecyclerView) findViewById(R.id.listParticipants);
        this.listRulesDetails = (WebView) findViewById(R.id.listRules);
        this.topImage = (ImageView) findViewById(R.id.matchImage);
    }

    private void initCounter() {
        mCountDownTimer = new CountDownTimer(mMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                calculateTime(millisUntilFinished);
                if (mListener != null) {
                    mListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                calculateTime(0);
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        };
    }

    public void startCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    public void stopCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public void setTime(long milliSeconds) {
        mMilliSeconds = milliSeconds;
        initCounter();
        calculateTime(milliSeconds);
    }

    private void calculateTime(long milliSeconds) {
        mSeconds = (milliSeconds / 1000) % 60;
        mMinutes = (milliSeconds / (1000 * 60)) % 60;
        mHours = (milliSeconds / (1000 * 60 * 60)) % 24;
        mDays = (milliSeconds / (1000 * 60 * 60 * 24));

        displayText();
    }

    private void displayText() {
        try{
            TextView textView = startTimer;
            StringBuilder sb = new StringBuilder();
            sb.append(mDays);
            sb.append("d, ");
            sb.append(mHours);
            sb.append("h ");
            sb.append(mMinutes);
            sb.append("m ");
            sb.append(mSeconds);
            sb.append("s");
            textView.setText(sb.toString());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private String getTwoDigitNumber(long number) {
        if (number >= 0 && number < 10) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
