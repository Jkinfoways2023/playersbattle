package com.GlaDius.war.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.GlaDius.war.views.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.GlaDius.war.R;
import com.GlaDius.war.adapter.ParticipantsListAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.model.ParticipantPojo;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;

public class LiveDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing_toolbar;
    private TextView fee;
    private WebView listRulesDetails;
    private LinearLayout loadBtnLL;
    private Button loadParticipants;
    private TextView map;

    int matchEntryFee;
    String matchID;
    String matchMap;
    String matchRules;
    int matchPerKill;
    String matchStartTime;
    String matchStatus;
    String matchTitle;
    String matchTopImage;
    String matchType;
    String matchVersion;
    int matchWinPrize;
    String matchPrizePool;
    String isCanceled,canceledDesc;
    String entryType;
    String spectateURL;
    String gameName;
    String gameURL;
    String privateStatus;
    String matchIds;
    String roomID;
    String roomPass;
    String slotNo;
    int roomSize;
    int totalJoined;
    int admin_share;
    String joinedStatus;
    String platform;
    String pool_type;

    TextView matchtype;
    NestedScrollView nestedScrollView;
    TextView noParticipants;
    TextView perKillPrize;
    TextView fullPrizeText;
    Button playButton;

    TextView refreshLV;
    RelativeLayout roomIDPassRL;
    TextView titleMatchIDPass;
    TextView roomIDPassUpcoming;
    RelativeLayout roomIDPasswordsRL;
    TextView room_ID;
    TextView room_Password;
    TextView matchSlot;
    Button spectateButton;
    LinearLayout spectatePlayButton;
    TextView startTime;
    TextView title;
    ImageView topImage;
    TextView type;
    TextView version;
    TextView winPrize;

    String id;
    String username;
    String token;

    private RecyclerView lvParticipants;
    private RecyclerView.Adapter adapter;
    private LinearLayout linearLayout;

    private ArrayList<ParticipantPojo> participantPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_details);
        session = new SessionManager(getApplicationContext());

        initToolbar();
        initView();
        initIntent();

        participantPojoList = new ArrayList<>();
        this.lvParticipants.setHasFixedSize(true);
        this.lvParticipants.setLayoutManager(new LinearLayoutManager(this));

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

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(gameURL);
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(LiveDetailsActivity.this, gameName+" is Not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spectateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spectateURL.startsWith("http://") && !spectateURL.startsWith("https://")){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://"+spectateURL)));
                }
                else {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(spectateURL)));
                }
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

    private void loadParticipants() {
        loadBtnLL.setVisibility(View.GONE);
        lvParticipants.setVisibility(View.VISIBLE);
        refreshLV.setVisibility(View.VISIBLE);
        Uri.Builder builder = Uri.parse(Constant.PARTICIPANTS_MATCH_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("match_id", matchID);
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
            adapter = new ParticipantsListAdapter(participantPojoList,this);
            adapter.notifyDataSetChanged();
            lvParticipants.setAdapter(adapter);
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
            gameName = extras.getString("GAME_NAME_KEY");
            gameURL = extras.getString("GAME_URL_KEY");
            matchMap = extras.getString("Map_KEY");
            matchRules = extras.getString("Rules_KEY");
            matchPerKill = extras.getInt("PerKill_KEY");
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
            matchIds = extras.getString("MATCH__KEY");
            roomID = extras.getString("ROOM_ID_KEY");
            roomPass = extras.getString("ROOM_PASS_KEY");
            roomSize = extras.getInt("ROOM_SIZE_KEY");
            totalJoined = extras.getInt("TOTAL_JOINED_KEY");
            joinedStatus = extras.getString("JOINED_STATUS_KEY");
            isCanceled = extras.getString("IS_CANCELED_KEY");
            canceledDesc = extras.getString("CANCELED_DESC_KEY");
            platform = extras.getString("PLATFORM_KEY");
            pool_type = extras.getString("POOL_TYPE_KEY");
            admin_share = extras.getInt("ADMIN_SHARE_KEY");
            slotNo = String.valueOf(extras.getInt("SLOT_KEY"));

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

            if (!(entryType.equals("Free") || entryType.equals("Sponsored") || entryType.equals("Giveaway"))) {
                fee.setText(String.valueOf(matchEntryFee));

                if (isCanceled.equals("1")){
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    roomIDPasswordsRL.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    playButton.setEnabled(false);
                    playButton.setFocusable(false);
                    playButton.setText("CANCELED");
                    titleMatchIDPass.setText("Match Canceled Details");
                    this.roomIDPassUpcoming.setText(this.canceledDesc);
                }
                else if (joinedStatus.equals("null")) {
                    roomIDPassRL.setVisibility(View.GONE);
                    roomIDPassUpcoming.setVisibility(View.GONE);
                    roomIDPasswordsRL.setVisibility(View.GONE);
                    playButton.setVisibility(View.GONE);
                    playButton.setEnabled(false);
                    playButton.setFocusable(false);
                }
                else {
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    roomIDPasswordsRL.setVisibility(View.GONE);
                    playButton.setEnabled(true);
                    playButton.setFocusable(true);
                    playButton.setText("PLAY NOW");
                    titleMatchIDPass.setText("Match Room Details");
                    this.roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }

                if (!roomID.isEmpty() && !joinedStatus.equals("null") && !isCanceled.equals("1")) {
                    room_ID.setText(roomID);
                    room_Password.setText(roomPass);
                    matchSlot.setText(slotNo);
                    roomIDPassUpcoming.setVisibility(View.GONE);
                    roomIDPasswordsRL.setVisibility(View.VISIBLE);
                    RoomDetailsAlertDialog(this);
                }

                /*if (!matchTopImage.isEmpty()) {
                    Picasso.get().load(Config.FILE_PATH_URL+matchTopImage.replace(" ", "%20")).resize(500,250).placeholder(R.drawable.pubg_banner).into(topImage);
                }*/

            }
            else {
                fee.setTextColor(Color.parseColor("#1E7E34"));
                fee.setText("FREE");

                if (isCanceled.equals("1")){
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    roomIDPasswordsRL.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                    playButton.setEnabled(false);
                    playButton.setFocusable(false);
                    playButton.setText("CANCELED");
                    titleMatchIDPass.setText("Match Canceled Details");
                    this.roomIDPassUpcoming.setText(this.canceledDesc);
                }
                else if (joinedStatus.equals("null")) {
                    playButton.setVisibility(View.GONE);
                    playButton.setEnabled(false);
                    playButton.setFocusable(false);
                } else {
                    roomIDPassRL.setVisibility(View.VISIBLE);
                    roomIDPassUpcoming.setVisibility(View.VISIBLE);
                    roomIDPasswordsRL.setVisibility(View.GONE);
                    playButton.setEnabled(true);
                    playButton.setFocusable(true);
                    playButton.setText("PLAY NOW");
                    titleMatchIDPass.setText("Match Room Details");
                    this.roomIDPassUpcoming.setText(R.string.upcoming_username_and_password);
                }

                if (!roomID.isEmpty() && !joinedStatus.equals("null") && !isCanceled.equals("1")) {
                    room_ID.setText(roomID);
                    room_Password.setText(roomPass);
                    matchSlot.setText(slotNo);
                    roomIDPassUpcoming.setVisibility(View.GONE);
                    roomIDPasswordsRL.setVisibility(View.VISIBLE);
                    RoomDetailsAlertDialog(LiveDetailsActivity.this);
                }

               /* if (!matchTopImage.isEmpty()) {
                    Picasso.get().load(Config.FILE_PATH_URL+matchTopImage.replace(" ", "%20")).resize(500,250).placeholder(R.drawable.pubg_banner).into(topImage);
                }*/
            }

        }
    }

    public void RoomDetailsAlertDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_id_password_prompt);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final TextView textView = (TextView) dialog.findViewById(R.id.roomIDValue);
        final TextView textView2 = (TextView) dialog.findViewById(R.id.roomPassValue);
        final TextView roomSlotValue = (TextView) dialog.findViewById(R.id.roomSlotValue);
        TextView textView3 = (TextView) dialog.findViewById(R.id.howJoinRoom);
        textView3.setPaintFlags(textView3.getPaintFlags() | 8);
        Button button = (Button) dialog.findViewById(R.id.play);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Config.HOW_TO_JOIN_URL.startsWith("http://") && !Config.HOW_TO_JOIN_URL.startsWith("https://")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+Config.HOW_TO_JOIN_URL)));
                }
                else {
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Config.HOW_TO_JOIN_URL)));
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        textView.setText(this.roomID);
        textView2.setText(this.roomPass);
        roomSlotValue.setText(this.slotNo);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(textView.getText().toString());
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(textView2.getText().toString());
            }
        });

        roomSlotValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(roomSlotValue.getText().toString());
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(gameURL);
                if (launchIntentForPackage != null) {
                    startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(LiveDetailsActivity.this, gameName+" is Not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initView() {
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
        this.noParticipants = (TextView) findViewById(R.id.noParticipantsText);
        this.nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScroll);
        this.spectateButton = (Button) findViewById(R.id.spectateButton);
        this.playButton = (Button) findViewById(R.id.playButton);
        this.titleMatchIDPass = (TextView) findViewById(R.id.titleMatchIDPass);
        this.roomIDPassUpcoming = (TextView) findViewById(R.id.RoomIDPassUpcoming);
        this.spectatePlayButton = (LinearLayout) findViewById(R.id.spectatePlayLL);
        this.roomIDPassRL = (RelativeLayout) findViewById(R.id.matchIDPassLL);
        this.roomIDPasswordsRL = (RelativeLayout) findViewById(R.id.roomIDPassRL);
        this.room_ID = (TextView) findViewById(R.id.roomIDValue);
        this.room_Password = (TextView) findViewById(R.id.roomPassValue);
        this.matchSlot = (TextView) findViewById(R.id.matchSlotValue);
        this.lvParticipants = (RecyclerView) findViewById(R.id.listParticipants);
        this.listRulesDetails = (WebView) findViewById(R.id.listRules);
        this.topImage = (ImageView) findViewById(R.id.matchImage);
    }

}
