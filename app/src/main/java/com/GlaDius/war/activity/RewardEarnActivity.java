package com.GlaDius.war.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.GlaDius.war.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;

public class RewardEarnActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd rewardedVideoAd;
    private static final long COUNTER_TIME = 10;

    private CountDownTimer countDownTimer;
    private long timeRemaining;

    private boolean gameOver;
    private boolean gamePaused;

    private ImageView howreward;
    private LinearLayout noRewardOffer,rewardOfferLL;
    private Button rewardNow;
    private TextView infoNote,counterTimer,rewardDesc,rewardExpiredText;

    private SessionManager session;
    private String id,password,username,firstname,lastname;
    private String currentBalance, currentWon;
    private String is_active, tot_coins, won_coins, bonus_coins;

    private int watch_count;
    private String date_time;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private long mMilliSeconds = 0;

    private CharSequence mPrefixText;
    private CharSequence mSuffixText;

    private TimerListener mListener;

    public void setOnTimerListener(TimerListener listener) {
        mListener = listener;
    }

    private CountDownTimer mCountDownTimer;

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_earn);

        initToolbar();
        initSession();
        initView();
        initListener();

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            rewardNow.setText("WATCH NOW");
            rewardNow.setEnabled(false);
            loadRewardedVideoAd();
            loadRewards();
            loadProfile();
        } else {
            rewardNow.setText("NO INTERNET");
            rewardNow.setEnabled(false);
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        rewardNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                    showRewardedVideo();
                } else {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initListener() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
    }

    private void loadRewards() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.LOAD_REWARDS_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("username",username);
            builder.appendQueryParameter("reward_limits",Config.WATCH_COUNT);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");
                        String msg = jsonObject1.getString("msg");

                        if (success.equals("0")) {
                            rewardNow.setEnabled(false);
                            setTime(Integer.parseInt(msg) * 1000);
                            startCountDown();
                        }
                        else if (success.equals("1")) {
                            try {
                                watch_count = Integer.parseInt(msg);
                                if (watch_count == 0){
                                    rewardNow.setEnabled(true);
                                    infoNote.setText("Complete Task");
                                    rewardNow.setText("Watch Now");
                                    counterTimer.setText(watch_count+"/"+Config.WATCH_COUNT);
                                }
                                else {
                                    rewardNow.setEnabled(true);
                                    infoNote.setText("Complete Task");
                                    rewardNow.setText("Watch Again");
                                    counterTimer.setText(watch_count+"/"+Config.WATCH_COUNT);
                                }
                                startGame();
                            }catch (NumberFormatException | NullPointerException e) {
                                e.printStackTrace();
                            }
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

    private void initView() {
        this.rewardNow = (Button) findViewById(R.id.rewardButton);
        this.rewardDesc = (TextView) findViewById(R.id.rewardMessage);
        this.infoNote = (TextView) findViewById(R.id.infoNote);
        this.counterTimer = (TextView) findViewById(R.id.counterTimer);
        this.howreward = (ImageView) findViewById(R.id.howreward);
        this.noRewardOffer = (LinearLayout) findViewById(R.id.noRewardOffer);
        this.rewardOfferLL = (LinearLayout) findViewById(R.id.rewardLL);
        this.rewardExpiredText = (TextView) findViewById(R.id.rewardExpiredText);

        if (!Config.WATCH_EARN) {
            noRewardOffer.setVisibility(View.VISIBLE);
            rewardOfferLL.setVisibility(View.GONE);
        }
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle((CharSequence) "Watch & Earn");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reward_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.myrewards) {
            Intent intent = new Intent(RewardEarnActivity.this,MyRewardsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.leaderboard) {
            Intent intent = new Intent(RewardEarnActivity.this, LeaderboardRewardActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.terms) {
            Intent intent = new Intent(RewardEarnActivity.this,TermsConditionsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
                            tot_coins = jsonObject1.getString("cur_balance");
                            won_coins = jsonObject1.getString("won_balance");
                            bonus_coins = jsonObject1.getString("bonus_balance");
                            is_active = jsonObject1.getString("status");
                            MainActivity.toolwallet.setText(String.valueOf(tot_coins));
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

    private void addRewardDetails() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.ADD_REWARD_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("username",username);
            builder.appendQueryParameter("reward_points",Config.PAY_REWARD);
            builder.appendQueryParameter("reward_limits",Config.WATCH_COUNT);
            StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");
                        String msg = jsonObject1.getString("msg");

                        if (success.equals("0")) {
                            rewardNow.setEnabled(false);
                            setTime(Integer.parseInt(msg) * 1000);
                            startCountDown();
                            successDialog();
                        }
                        else  if (success.equals("1")) {
                            rewardNow.setEnabled(true);
                            watch_count += 1;
                            infoNote.setText("Complete Task");
                            rewardNow.setText("Watch Again");
                            counterTimer.setText(watch_count+"/"+Config.WATCH_COUNT);
                            Toast.makeText(RewardEarnActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
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



    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(Config.AD_REWARDED_ID, new AdRequest.Builder().build());
        }
    }

    private void startGame() {
        // Hide the retry button, load the ad, and start the timer.
        loadRewardedVideoAd();
        createTimer(COUNTER_TIME);
        gamePaused = false;
        gameOver = false;
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private void createTimer(long time) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(time * 1000, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timeRemaining = ((millisUnitFinished / 1000) + 1);
                rewardNow.setText("Wait "+timeRemaining+" Sec");
                rewardNow.setEnabled(false);
            }

            @Override
            public void onFinish() {
                if (rewardedVideoAd.isLoaded()) {
                    if (watch_count == 0) {
                        rewardNow.setText("WATCH NOW");
                        rewardNow.setEnabled(true);
                    }
                    else {
                        rewardNow.setText("Watch Again");
                        rewardNow.setEnabled(true);
                    }
                }
                else {
                    rewardNow.setText("Failed! Try Later");
                }
                gameOver = true;
            }
        };
        countDownTimer.start();
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
        else {
            rewardedVideoAd.loadAd(Config.AD_REWARDED_ID, new AdRequest.Builder().build());
            rewardedVideoAd.show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        initSession();
        loadProfile();
        if (!gameOver && gamePaused) {
            resumeGame();
        }
        rewardedVideoAd.resume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
        rewardedVideoAd.pause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rewardedVideoAd.destroy(getApplicationContext());
    }

    private void pauseGame() {
        try {
            countDownTimer.cancel();
            gamePaused = true;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void resumeGame() {
        createTimer(timeRemaining);
        gamePaused = false;
    }



    @Override
    public void onRewardedVideoAdLeftApplication() {
        //Toast.makeText(getApplicationContext(), "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        //Toast.makeText(getApplicationContext(), "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
        // Preload the next video ad.
        if (watch_count+1 != Integer.parseInt(Config.WATCH_COUNT)){
            loadRewardedVideoAd();
            createTimer(COUNTER_TIME);
            gamePaused = false;
            gameOver = false;
        }
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        //Toast.makeText(getApplicationContext(), "Video Ad Failed To Load", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        //Toast.makeText(getApplicationContext(), "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        //Toast.makeText(getApplicationContext(), "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewarded(RewardItem reward) {
        //Toast.makeText(getApplicationContext(), String.format(" onRewarded! currency: %s amount: %d", reward.getType(), reward.getAmount()), Toast.LENGTH_SHORT).show();
        addRewardDetails();
    }

    @Override
    public void onRewardedVideoStarted() {
        //Toast.makeText(getApplicationContext(), "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoCompleted() {
        //Toast.makeText(getApplicationContext(), "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show();
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
        mSeconds = (milliSeconds / 1000);
        mMinutes = mSeconds / 60;
        mSeconds = mSeconds % 60;

        mHours = mMinutes / 60;
        mMinutes = mMinutes % 60;

        displayText();
    }

    private void displayText() {
        StringBuffer buffer = new StringBuffer();
        if (!TextUtils.isEmpty(mPrefixText)) {
            buffer.append(mPrefixText);
            buffer.append(" ");
        }

        buffer.append(getTwoDigitNumber(mHours));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(mMinutes));
        buffer.append(":");
        buffer.append(getTwoDigitNumber(mSeconds));

        if (!TextUtils.isEmpty(mSuffixText)) {
            buffer.append(" ");
            buffer.append(mSuffixText);
        }

        try{
            rewardNow.setEnabled(false);
            infoNote.setText("Next Video");
            rewardNow.setText("Task Completed");
            counterTimer.setText(""+buffer);
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

    private void successDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView titleTv = (TextView) dialog.findViewById(R.id.titleTv);
        TextView subTitleTv = (TextView) dialog.findViewById(R.id.subTitleTv);
        TextView noteTv = (TextView) dialog.findViewById(R.id.noteTv);

        Button cancelBt = (Button) dialog.findViewById(R.id.cancelBt);
        Button okBt = (Button) dialog.findViewById(R.id.okBt);

        noteTv.setText("Congratulations!!! You have successfully completed today task and get reward coins!!!. Reward coins has been added to your account wallet. You can you this for join any paid match.");

        cancelBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        okBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}