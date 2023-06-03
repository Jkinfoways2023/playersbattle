package com.GlaDius.war.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;
import com.GlaDius.war.bottomsheet.BottomSheetMyEntries;
import com.GlaDius.war.bottomsheet.BottomSheetViewEntries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RoomAuthActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView roomIDValue;
    private TextView roomPassValue;
    private TextView roomSlotValue;
    private TextView textView35;

    private Button textView32;
    private Button textView67;
    private Button entryButton;
    private Button joinButton;

    private ProgressBar pg_countdown_timer_days;
    private TextView tv_countdown_timer_days;
    private ProgressBar pg_countdown_timer_hours;
    private TextView tv_countdown_timer_hours;
    private ProgressBar pg_countdown_timer_minutes;
    private TextView tv_countdown_timer_minutes;
    private ProgressBar pg_countdown_timer_seconds;
    private TextView tv_countdown_timer_seconds;
    private RelativeLayout onGoingLL;
    private LinearLayout activity_countdown_wheels_layout;
    private LinearLayout activity_countdown_labels_layout;
    private TextView upcomingText;
    private TextView textView40;
    private TextView textView41;

    private int matchEntryFee,matchPerKill,matchWinPrize,roomSize,totalJoined,admin_share;
    private String matchID, matchMap,matchRules,currentTime,startTime,matchStartTime,matchStatus,matchTitle,matchTopImage,roomID,roomPass,backTag,matchPrizePool,platform,pool_type,slotNo;
    private String matchType,matchVersion,entryType,spectateURL,privateStatus,matchIds,joinedStatus,userJoined,isCanceled,canceledDesc,secretCode;

    private SessionManager session;
    private String is_active, tot_coins, won_coins, bonus_coins;
    private String id;
    private String password;
    private String username;
    private String name;
    private String firstname;
    private String lastname;
    private String email;
    private String mnumber;

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
        setContentView(R.layout.activity_room_auth);

        initToolbar();

        roomIDValue = findViewById(R.id.roomIDValue);
        roomPassValue = findViewById(R.id.roomPassValue);
        roomSlotValue = findViewById(R.id.roomSlotValue);
        upcomingText = findViewById(R.id.upcomingText);
        onGoingLL = findViewById(R.id.onGoingLL);
        activity_countdown_wheels_layout = findViewById(R.id.activity_countdown_wheels_layout);
        activity_countdown_labels_layout = findViewById(R.id.activity_countdown_labels_layout);

        textView32 = findViewById(R.id.textView32);
        textView35 = findViewById(R.id.textView35);
        textView35.setPaintFlags(textView35.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textView40 = findViewById(R.id.textView40);
        textView41 = findViewById(R.id.textView41);
        textView67 = findViewById(R.id.textView67);
        entryButton = findViewById(R.id.entryButton);
        joinButton = findViewById(R.id.joinButton);

        textView32.setOnClickListener(this);
        textView35.setOnClickListener(this);
        textView67.setOnClickListener(this);
        entryButton.setOnClickListener(this);
        joinButton.setOnClickListener(this);

        pg_countdown_timer_days = findViewById(R.id.pg_countdown_timer_days);
        tv_countdown_timer_days = findViewById(R.id.tv_countdown_timer_days);
        pg_countdown_timer_hours = findViewById(R.id.pg_countdown_timer_hours);
        tv_countdown_timer_hours = findViewById(R.id.tv_countdown_timer_hours);
        pg_countdown_timer_minutes = findViewById(R.id.pg_countdown_timer_minutes);
        tv_countdown_timer_minutes = findViewById(R.id.tv_countdown_timer_minutes);
        pg_countdown_timer_seconds = findViewById(R.id.pg_countdown_timer_seconds);
        tv_countdown_timer_seconds = findViewById(R.id.tv_countdown_timer_seconds);

        initSession();
        initIntent();
        loadTime();

        roomIDValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(roomIDValue.getText().toString());
            }
        });

        roomPassValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(roomPassValue.getText().toString());
            }
        });

        roomSlotValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipBoard(roomSlotValue.getText().toString());
            }
        });
    }

    private void loadTime() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.TIMER_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("match_id", matchID);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            currentTime = jsonObject1.getString("msg");
                            startTime = jsonObject1.getString("time");

                            try {
                                //currentTime = "1578499200";
                                //matchStartTime = "1578655554";
                                if (Integer.parseInt(currentTime) >= Integer.parseInt(startTime) && roomID.isEmpty()){
                                    onGoingLL.setVisibility(View.GONE);
                                    upcomingText.setVisibility(View.VISIBLE);
                                    upcomingText.setText(R.string.upcoming_username_and_password);
                                    textView41.setVisibility(View.VISIBLE);
                                    activity_countdown_wheels_layout.setVisibility(View.GONE);
                                    activity_countdown_labels_layout.setVisibility(View.GONE);
                                }
                                else if (Integer.parseInt(currentTime) >= Integer.parseInt(startTime) && !joinedStatus.equals("null") && !roomID.isEmpty()){
                                    onGoingLL.setVisibility(View.VISIBLE);
                                    upcomingText.setVisibility(View.GONE);
                                    upcomingText.setText(R.string.upcoming_username_and_password);
                                    textView41.setVisibility(View.VISIBLE);
                                    textView41.setText("Hurry Up! Match is started please join game as possible as soon.");
                                    activity_countdown_wheels_layout.setVisibility(View.GONE);
                                    activity_countdown_labels_layout.setVisibility(View.GONE);
                                }
                                else if (Integer.parseInt(currentTime) >= Integer.parseInt(startTime) && joinedStatus.equals("null") && !roomID.isEmpty()){
                                    onGoingLL.setVisibility(View.VISIBLE);
                                    upcomingText.setVisibility(View.GONE);
                                    upcomingText.setText(R.string.upcoming_username_and_password);
                                    textView41.setVisibility(View.VISIBLE);
                                    textView41.setText("Hurry Up! Match is started you can watch game live from our youtube channel. .");
                                    activity_countdown_wheels_layout.setVisibility(View.GONE);
                                    activity_countdown_labels_layout.setVisibility(View.GONE);
                                }

                                int time = Integer.parseInt(startTime) - Integer.parseInt(currentTime);
                                pg_countdown_timer_days.setMax((int) (time * 1000) / (1000 * 60 * 60 * 24));
                                pg_countdown_timer_hours.setMax(24);
                                pg_countdown_timer_minutes.setMax(60);
                                pg_countdown_timer_seconds.setMax(60);

                                setTime(time * 1000);
                                startCountDown();
                            }catch (Exception e){
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
            request.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.textView32){
            if (backTag.equals("home")){
                if (new ExtraOperations().haveNetworkConnection(this)) {
                    Intent intent = new Intent(RoomAuthActivity.this, PlayDetailsActivity.class);
                    intent.putExtra("EntryFee_KEY", matchEntryFee);
                    intent.putExtra("ID_KEY", matchID);
                    intent.putExtra("Map_KEY", matchMap);
                    intent.putExtra("Rules_KEY", matchRules);
                    intent.putExtra("PerKill_KEY", matchPerKill);
                    //intent.putExtra("CurrentTime_KEY", playPojo.getCurrent_time());
                    intent.putExtra("StartTime_KEY", matchStartTime);
                    intent.putExtra("Match_Status_KEY", matchStatus);
                    intent.putExtra("Title_KEY", matchTitle);
                    intent.putExtra("TopImage_KEY", matchTopImage);
                    intent.putExtra("Entry_Type_KEY", entryType);
                    intent.putExtra("Match_Type_KEY", matchType);
                    intent.putExtra("Version_KEY", matchVersion);
                    intent.putExtra("WinPrize_KEY", matchWinPrize);
                    intent.putExtra("Private_Status_KEY", privateStatus);
                    intent.putExtra("MATCH__KEY", matchIds);
                    intent.putExtra("ROOM_ID_KEY", roomID);
                    intent.putExtra("ROOM_PASS_KEY", roomPass);
                    intent.putExtra("ROOM_SIZE_KEY", roomSize);
                    intent.putExtra("SLOT_KEY", Integer.parseInt(slotNo));
                    intent.putExtra("TOTAL_JOINED_KEY", totalJoined);
                    intent.putExtra("JOINED_STATUS_KEY", joinedStatus);
                    intent.putExtra("USER_JOINED_KEY", userJoined);
                    intent.putExtra("IS_CANCELED_KEY", isCanceled);
                    intent.putExtra("CANCELED_DESC_KEY", canceledDesc);
                    intent.putExtra("SECRET_CODE_KEY", secretCode);
                    intent.putExtra("PrizePool_KEY", matchPrizePool);
                    intent.putExtra("SpectateURL_KEY", spectateURL);
                    intent.putExtra("PLATFORM_KEY", platform);
                    intent.putExtra("POOL_TYPE_KEY", pool_type);
                    intent.putExtra("ADMIN_SHARE_KEY", admin_share);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RoomAuthActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
            else if (backTag.equals("details")) {
                onBackPressed();
            }
        }
        else if (v.getId() == R.id.textView67){
            new BottomSheetViewEntries(matchID,id,password).show(getSupportFragmentManager(), "myBidsBottomSheet");
        }
        else if (v.getId() == R.id.entryButton){
            new BottomSheetMyEntries(matchID,id,password,roomID,isCanceled).show(getSupportFragmentManager(), "myBidsBottomSheet");
        }
        else if (v.getId() == R.id.joinButton){
            Intent intent = new Intent(getApplicationContext(), JoiningMatchActivity.class);
            intent.putExtra("matchType", matchType);
            intent.putExtra("matchID", matchID);
            intent.putExtra("matchName", matchTitle);
            intent.putExtra("entryFee", matchEntryFee);
            intent.putExtra("entryType", entryType);
            intent.putExtra("JoinStatus", joinedStatus);
            intent.putExtra("isPrivate", privateStatus);
            intent.putExtra("matchRules", matchRules);
            intent.putExtra("ROOM_SIZE_KEY", roomSize);
            intent.putExtra("TOTAL_JOINED_KEY", totalJoined);
            startActivity(intent);
        }
        else if (v.getId() == R.id.textView35) {
            if (!Config.HOW_TO_JOIN_URL.startsWith("http://") && !Config.HOW_TO_JOIN_URL.startsWith("https://")){
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+Config.HOW_TO_JOIN_URL)));
            }
            else {
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Config.HOW_TO_JOIN_URL)));
            }
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
            //currentTime = extras.getString("CurrentTime_KEY");
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
            backTag = extras.getString("BACK_TAG_KEY");

            try {
                if (isCanceled.equals("1")){
                    joinButton.setText("Match CANCELED");
                    //joinButton.setBackgroundColor(Color.parseColor("#757575"));
                    joinButton.setClickable(false);
                    joinButton.setFocusable(false);
                    //entryButton.setClickable(false);
                    //entryButton.setFocusable(false);

                    onGoingLL.setVisibility(View.GONE);
                    upcomingText.setVisibility(View.VISIBLE);
                    textView40.setText("Match Cancel Due To");
                    textView41.setVisibility(View.VISIBLE);
                    textView41.setText(canceledDesc);
                    activity_countdown_wheels_layout.setVisibility(View.GONE);
                    activity_countdown_labels_layout.setVisibility(View.GONE);
                }
                else if (totalJoined >= roomSize) {
                    joinButton.setText("Match Full");
                    //joinButton.setBackgroundColor(Color.parseColor("#757575"));
                    joinButton.setClickable(false);
                    joinButton.setFocusable(false);
                } else if (userJoined.equals("0") && totalJoined < roomSize) {
                    joinButton.setText("Join Now");
                    joinButton.setEnabled(true);
                    joinButton.setClickable(true);
                }
                else if (!userJoined.equals("1") && matchType.equals("Solo") && totalJoined < roomSize) {
                    joinButton.setText("+ Add New Entry");
                    joinButton.setEnabled(true);
                    joinButton.setClickable(true);
                }
                else if (!userJoined.equals("2") && matchType.equals("Duo") && totalJoined < roomSize) {
                    joinButton.setText("+ Add New Entry");
                    joinButton.setEnabled(true);
                    joinButton.setClickable(true);
                }
                else if (!userJoined.equals("4") && matchType.equals("Squad") && totalJoined < roomSize) {
                    joinButton.setText("+ Add New Entry");
                    joinButton.setEnabled(true);
                    joinButton.setClickable(true);
                }
                else if (userJoined.equals("1") && matchType.equals("Solo") && totalJoined < roomSize) {
                    joinButton.setText("Already Joined");
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                }
                else if (userJoined.equals("2") && matchType.equals("Duo") && totalJoined < roomSize) {
                    joinButton.setText("Already Joined");
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                }
                else if (userJoined.equals("4") && matchType.equals("Squad") && totalJoined < roomSize) {
                    joinButton.setText("Already Joined");
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                }
                else if (totalJoined >= roomSize) {
                    joinButton.setText("Match Full");
                    joinButton.setBackgroundColor(Color.parseColor("#757575"));
                    joinButton.setClickable(false);
                    joinButton.setEnabled(false);
                }


                if (!(roomID.equals("null") || roomID.isEmpty())) {
                    roomIDValue.setText(roomID);
                    roomPassValue.setText(roomPass);
                    //roomSlotValue.setText(slotNo);
                    onGoingLL.setVisibility(View.VISIBLE);
                    upcomingText.setVisibility(View.GONE);
                    //entryButton.setClickable(false);
                    //entryButton.setFocusable(false);
                }
                else {
                    onGoingLL.setVisibility(View.GONE);
                    upcomingText.setVisibility(View.VISIBLE);
                }

            }catch (NullPointerException e){
                e.printStackTrace();
            }
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("View More");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void initSession(){
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);
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
            tv_countdown_timer_days.setText(""+getTwoDigitNumber(mDays));
            tv_countdown_timer_hours.setText(""+getTwoDigitNumber(mHours));
            tv_countdown_timer_minutes.setText(""+getTwoDigitNumber(mMinutes));
            tv_countdown_timer_seconds.setText(""+getTwoDigitNumber(mSeconds));

            pg_countdown_timer_days.setProgress((int) mDays);
            pg_countdown_timer_hours.setProgress((int) mHours);
            pg_countdown_timer_minutes.setProgress((int) mMinutes);
            pg_countdown_timer_seconds.setProgress((int) mSeconds);

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
