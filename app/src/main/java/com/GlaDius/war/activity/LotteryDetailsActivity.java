package com.GlaDius.war.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.GlaDius.war.R;
import com.GlaDius.war.adapter.LotteryParticipantAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.model.LotteryResultsPojo;
import com.GlaDius.war.session.SessionManager;
import com.GlaDius.war.utils.ExtraOperations;
import com.GlaDius.war.utils.MySingleton;
import com.GlaDius.war.bottomsheet.BottomSheetMyLottery;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LotteryDetailsActivity extends AppCompatActivity {

    private TextView totalText, timeLeftText, titleText, prizeText, entryFeeText, resultDateText, leadNameText;
    private WebView howtoplay;
    private ImageView lotteryImage;
    private Button myButton, joinButton;

    private int size, total_joined;
    private String user_id, name, firstname, lastname;
    private String id, title, time, timestamp, fee, prize, my_number, rules, image, currenttime;
    private String is_active, tot_coins, won_coins, bonus_coins;

    private Bundle bundle;
    private SessionManager session;

    private long mDays = 0;
    private long mHours = 0;
    private long mMinutes = 0;
    private long mSeconds = 0;
    private long mMilliSeconds = 0;

    private CharSequence mPrefixText;
    private CharSequence mSuffixText;

    private RoomAuthActivity.TimerListener mListener;
    private CountDownTimer mCountDownTimer;

    public interface TimerListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }


    private RecyclerView lvParticipants;
    private RecyclerView.Adapter adapter;
    private LinearLayout linearLayout;

    private ArrayList<LotteryResultsPojo> lotteryResultsPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private Button loadParticipants;
    private LinearLayout loadBtnLL;
    private TextView refreshLV;
    private TextView noParticipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_details);

        initToolbar();
        initView();
        initSession();
        initBundel();

        lotteryResultsPojoList = new ArrayList<>();
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

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BottomSheetMyLottery(id,user_id,String.valueOf(total_joined)).show(getSupportFragmentManager(), "myBidsBottomSheet");
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinDialog();
            }
        });
    }

    private void loadParticipants() {
        loadBtnLL.setVisibility(View.GONE);
        lvParticipants.setVisibility(View.VISIBLE);
        refreshLV.setVisibility(View.VISIBLE);
        Uri.Builder builder = Uri.parse(Constant.LOTTERY_PARTICIPANTS_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("lottery_id", id);
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
        lotteryResultsPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            LotteryResultsPojo lotteryResultsPojo = new LotteryResultsPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                lotteryResultsPojo.setId(json.getString("id"));
                lotteryResultsPojo.setName(json.getString("name"));
                lotteryResultsPojo.setLottery_no(json.getString("lottery_no"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lotteryResultsPojoList.add(lotteryResultsPojo);
        }
        if (!lotteryResultsPojoList.isEmpty()){
            adapter = new LotteryParticipantAdapter(lotteryResultsPojoList,this);
            adapter.notifyDataSetChanged();
            lvParticipants.setAdapter(adapter);
        }
        else {
            noParticipants.setVisibility(View.VISIBLE);
            lvParticipants.setVisibility(View.GONE);
        }
    }

    private void JoinDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_lottery);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        final LinearLayout betLL = (LinearLayout) dialog.findViewById(R.id.betLL);
        final LinearLayout noBetLL = (LinearLayout) dialog.findViewById(R.id.noBetLL);

        final TextView errorTv = (TextView) dialog.findViewById(R.id.errorTv);
        final TextView entryFee = (TextView) dialog.findViewById(R.id.entryFee);

        final Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final Button submitBet = (Button) dialog.findViewById(R.id.submitBet);

        final Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        final Button buyButton = (Button) dialog.findViewById(R.id.buyButton);

        entryFee.setText("₹"+fee);
        noBetLL.setVisibility(View.VISIBLE);
        betLL.setVisibility(View.GONE);
        errorTv.setTextColor(Color.parseColor("#000000"));
        errorTv.setText("Please wait a few seconds...");

        Uri.Builder builder1 = Uri.parse(Constant.GET_PROFILE_URL).buildUpon();
        builder1.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder1.appendQueryParameter("id", user_id);
        StringRequest request = new StringRequest(Request.Method.POST, builder1.toString(), new Response.Listener<String>() {
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

                        try {
                            if (is_active.equals("1")) {
                                if (Integer.parseInt(tot_coins) >= Integer.parseInt(fee)) {
                                    noBetLL.setVisibility(View.GONE);
                                    betLL.setVisibility(View.VISIBLE);
                                    errorTv.setText(R.string.sufficientBalanceText);
                                } else {
                                    noBetLL.setVisibility(View.VISIBLE);
                                    betLL.setVisibility(View.GONE);
                                    errorTv.setTextColor(Color.parseColor("#ff0000"));
                                    errorTv.setText("Not enough balance to join lottery. Add some before proceeding.");
                                }
                            }
                            else {
                                noBetLL.setVisibility(View.GONE);
                                betLL.setVisibility(View.GONE);
                                errorTv.setTextColor(Color.parseColor("#ff0000"));
                                errorTv.setText("You are not eligible to join lottery as your account is not active.");
                            }
                        }catch (NumberFormatException e){
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
        request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestque(request);

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(),MyWalletActivity.class);
                startActivity(intent);
            }
        });


        submitBet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (new ExtraOperations().haveNetworkConnection(LotteryDetailsActivity.this)) {
                    //progressBar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    Uri.Builder builder = Uri.parse(Constant.LOTTERY_JOIN_URL).buildUpon();
                    builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                    builder.appendQueryParameter("lottery_id", id);
                    builder.appendQueryParameter("user_id", user_id);
                    builder.appendQueryParameter("name", name);
                    builder.appendQueryParameter("fee", fee);
                    StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                String success = jsonObject1.getString("success");
                                String msg = jsonObject1.getString("msg");

                                if (success.equals("0")) {
                                    //progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LotteryDetailsActivity.this, msg + "", Toast.LENGTH_LONG).show();
                                } else if (success.equals("1")) {
                                   // progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LotteryDetailsActivity.this, msg + "", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LotteryDetailsActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                //progressBar.setVisibility(View.GONE);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            //progressBar.setVisibility(View.GONE);
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
                    MySingleton.getInstance(LotteryDetailsActivity.this).addToRequestque(request);
                } else {
                    Toast.makeText(LotteryDetailsActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initSession() {
        session = new SessionManager(this);
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);
        //username= user.get(SessionManager.KEY_USERNAME);
        //token = user.get(SessionManager.KEY_PASSWORD);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        name = firstname+" "+lastname;
    }

    private void initBundel() {
        bundle = getIntent().getExtras();
        if (bundle != null) {

            id = bundle.getString("ID_KEY");
            title = bundle.getString("TITLE_KEY");
            time = bundle.getString("TIME_KEY");
            timestamp = bundle.getString("TIMESTAMP_KEY");
            fee = bundle.getString("FEE_KEY");
            prize = bundle.getString("PRIZE_KEY");
            size = bundle.getInt("SIZE_KEY");
            total_joined = bundle.getInt("TOTAL_JOINED_KEY");
            my_number = bundle.getString("MY_NUMBER_KEY");
            rules = bundle.getString("RULES_KEY");
            image = bundle.getString("IMAGE_KEY");
            currenttime = bundle.getString("CURRENT_TIME_KEY");


            totalText.setText("Spots: "+total_joined+"/"+size);
            titleText.setText(title);
            prizeText.setText(prize);
            entryFeeText.setText(fee);
            resultDateText.setText(time);

            howtoplay.setBackgroundColor(0);
            howtoplay.loadData(Base64.encodeToString(rules.getBytes(), Base64.NO_PADDING),"text/html", "base64");

            Picasso.get().load(Config.FILE_PATH_URL+image.replace(" ", "%20")).resize(500,250).into(lotteryImage);

            if (Integer.parseInt(currenttime) >= Integer.parseInt(timestamp)) {
                timeLeftText.setText("Registration Closed.");
                joinButton.setEnabled(false);
                joinButton.setFocusable(false);
            }
            else {
                int time = Integer.parseInt(timestamp) - Integer.parseInt(currenttime);
                setTime(time * 1000);
                startCountDown();
            }

            if (!my_number.equals("0")){
                joinButton.setEnabled(false);
                joinButton.setFocusable(false);
                joinButton.setText("REGISTERED");
            }
            else {
                joinButton.setText("REGISTER NOW");
            }

        }
    }

    private void initView() {
        totalText = (TextView) findViewById(R.id.totalText);
        timeLeftText = (TextView) findViewById(R.id.timeLeft);
        titleText = (TextView) findViewById(R.id.title);
        prizeText = (TextView) findViewById(R.id.prizeText);
        entryFeeText = (TextView) findViewById(R.id.entryFeeText);
        resultDateText = (TextView) findViewById(R.id.resultDateText);
        leadNameText = (TextView) findViewById(R.id.leadNameText);
        howtoplay = (WebView) findViewById(R.id.howtoplay);
        lotteryImage = (ImageView) findViewById(R.id.lotteryImage);
        myButton = (Button) findViewById(R.id.myButton);
        joinButton = (Button) findViewById(R.id.joinButton);
        this.loadParticipants = (Button) findViewById(R.id.loadBtn);
        this.loadBtnLL = (LinearLayout) findViewById(R.id.LLloadBtn);
        this.refreshLV = (TextView) findViewById(R.id.refreshLVBtn);
        this.noParticipants = (TextView) findViewById(R.id.noParticipantsText);
        this.lvParticipants = (RecyclerView) findViewById(R.id.listParticipants);
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Lucky Draw");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
            timeLeftText.setText(""+getTwoDigitNumber(mDays)+"d : "+getTwoDigitNumber(mHours)+"h : "+getTwoDigitNumber(mMinutes)+"m : "+getTwoDigitNumber(mSeconds)+"s");

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
