package com.tournaments.grindbattles.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.LotteryDetailsActivity;
import com.tournaments.grindbattles.activity.MainActivity;
import com.tournaments.grindbattles.activity.MyWalletActivity;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.LotteryDetailsPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class LotteryDetailsAdapter extends RecyclerView.Adapter<LotteryDetailsAdapter.ViewHolder> {

    private String matchStartTime,currentTime;
    private Context context;
    private List<LotteryDetailsPojo> lotteryDetailsPojosList;

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

    private SessionManager session;
    private String left_spots;
    private String is_active, tot_coins, won_coins, bonus_coins;
    private String user_id, name, firstname, lastname;

    public LotteryDetailsAdapter(List<LotteryDetailsPojo> lotteryDetailsPojosList, Context context){
        super();
        this.lotteryDetailsPojosList = lotteryDetailsPojosList;
        this.context = context;

        initSession();
    }

    @Override
    public LotteryDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lottery, parent, false);
        LotteryDetailsAdapter.ViewHolder viewHolder = new LotteryDetailsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LotteryDetailsAdapter.ViewHolder holder, int position) {
        final LotteryDetailsPojo lotteryDetailsPojo =  lotteryDetailsPojosList.get(position);

        holder.timeText.setText(lotteryDetailsPojo.getTime());
        holder.lotteryTitle.setText(lotteryDetailsPojo.getTitle());
        holder.priceText.setText(lotteryDetailsPojo.getFee());
        Picasso.get().load(Config.FILE_PATH_URL+lotteryDetailsPojo.getImage().replace(" ", "%20")).resize(500,250).placeholder(R.drawable.lottery).into(holder.lotteryBanner);

        currentTime = lotteryDetailsPojo.getCurrenttime();
        matchStartTime = lotteryDetailsPojo.getTimestamp();

        if (Integer.parseInt(currentTime) >= Integer.parseInt(matchStartTime)) {
            holder.timeText.setText("Registration Closed.");
            holder.joinButton.setEnabled(false);
            holder.joinButton.setFocusable(false);
        }
        else {
            int time = Integer.parseInt(matchStartTime) - Integer.parseInt(currentTime);
            setTime(time * 1000,holder.timeText);
            startCountDown(holder.timeText);
        }

        holder.materialProgressBar.setMax(lotteryDetailsPojo.getSize());
        holder.materialProgressBar.setProgress(lotteryDetailsPojo.getTotal_joined());

        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(lotteryDetailsPojo.getTotal_joined());
        stringBuilder1.append("/");
        stringBuilder1.append(lotteryDetailsPojo.getSize());
        holder.size.setText(stringBuilder1.toString());

        if (lotteryDetailsPojo.getTotal_joined() >= lotteryDetailsPojo.getSize()) {
            holder.spots.setTextColor(Color.parseColor("#ff0000"));
            holder.spots.setText("No Spots Left! Draw is Full.");
            holder.joinButton.setText("DRAW FULL");
            holder.joinButton.setTextColor(Color.parseColor("#ffffff"));
            holder.joinButton.setBackgroundResource(R.drawable.buttonbackactive);
            holder.joinButton.setEnabled(false);
            holder.joinButton.setFocusable(false);
        } else {
            left_spots = String.valueOf(lotteryDetailsPojo.getSize() - lotteryDetailsPojo.getTotal_joined());
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Only ");
            stringBuilder2.append(left_spots);
            stringBuilder2.append(" spots left");
            holder.spots.setText(stringBuilder2.toString());
        }

        if (!lotteryDetailsPojo.getMy_number().equals("0")){
            holder.joinButton.setEnabled(false);
            holder.joinButton.setFocusable(false);
            holder.joinButton.setText("REGISTERED");
        }
        else {
            holder.joinButton.setText("REGISTER");
        }

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(context, LotteryDetailsActivity.class);
                intent.putExtra("ID_KEY", lotteryDetailsPojo.getId());
                intent.putExtra("TITLE_KEY", lotteryDetailsPojo.getTitle());
                intent.putExtra("TIME_KEY", lotteryDetailsPojo.getTime());
                intent.putExtra("TIMESTAMP_KEY", lotteryDetailsPojo.getTimestamp());
                intent.putExtra("FEE_KEY", lotteryDetailsPojo.getFee());
                intent.putExtra("PRIZE_KEY", lotteryDetailsPojo.getPrize());
                intent.putExtra("SIZE_KEY", lotteryDetailsPojo.getSize());
                intent.putExtra("TOTAL_JOINED_KEY", lotteryDetailsPojo.getTotal_joined());
                intent.putExtra("MY_NUMBER_KEY", lotteryDetailsPojo.getMy_number());
                intent.putExtra("RULES_KEY", lotteryDetailsPojo.getRules());
                intent.putExtra("IMAGE_KEY", lotteryDetailsPojo.getImage());
                intent.putExtra("CURRENT_TIME_KEY", lotteryDetailsPojo.getCurrenttime());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinDialog(lotteryDetailsPojo);
            }
        });

    }

    private void initSession() {
        session = new SessionManager(context);
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);
        //username= user.get(SessionManager.KEY_USERNAME);
        //token = user.get(SessionManager.KEY_PASSWORD);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        name = firstname+" "+lastname;
    }
    private void JoinDialog(LotteryDetailsPojo lotteryDetailsPojo) {
        final Dialog dialog = new Dialog(context);
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

        entryFee.setText(lotteryDetailsPojo.getFee());
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
                                if (Integer.parseInt(tot_coins) >= Integer.parseInt(lotteryDetailsPojo.getFee())) {
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
                        Toast.makeText(context,"Something went wrong", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(context).addToRequestque(request);

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
                Intent intent = new Intent(context, MyWalletActivity.class);
                context.startActivity(intent);
            }
        });


        submitBet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (new ExtraOperations().haveNetworkConnection(context)) {
                    //progressBar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    Uri.Builder builder = Uri.parse(Constant.LOTTERY_JOIN_URL).buildUpon();
                    builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                    builder.appendQueryParameter("lottery_id", lotteryDetailsPojo.getId());
                    builder.appendQueryParameter("user_id", user_id);
                    builder.appendQueryParameter("name", name);
                    builder.appendQueryParameter("fee", lotteryDetailsPojo.getFee());
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
                                    Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
                                } else if (success.equals("1")) {
                                    // progressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
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
                    MySingleton.getInstance(context).addToRequestque(request);
                } else {
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


    @Override
    public int getItemCount() {
        return lotteryDetailsPojosList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView timeText;
        TextView lotteryTitle;
        TextView priceText;
        TextView spots;
        TextView size;
        ProgressBar materialProgressBar;
        Button joinButton;
        ImageView lotteryBanner;
        CardView mainCard;

        public ViewHolder(View itemView) {
            super(itemView);

            this.timeText = (TextView) itemView.findViewById(R.id.timedate);
            this.lotteryTitle = (TextView) itemView.findViewById(R.id.title);
            this.priceText = (TextView) itemView.findViewById(R.id.priceText);
            this.joinButton = (Button) itemView.findViewById(R.id.joinButton);
            this.spots = (TextView) itemView.findViewById(R.id.spots);
            this.size = (TextView) itemView.findViewById(R.id.size);
            this.materialProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            lotteryBanner = (ImageView) itemView.findViewById(R.id.lotteryBanner);
            mainCard = (CardView) itemView.findViewById(R.id.mainCard);
        }

    }


    private void initCounter(TextView timeText) {
        mCountDownTimer = new CountDownTimer(mMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                calculateTime(millisUntilFinished,timeText);
                if (mListener != null) {
                    mListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                calculateTime(0,timeText);
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        };
    }

    public void startCountDown(TextView timeText) {
        if (mCountDownTimer != null) {
            mCountDownTimer.start();
        }
    }

    public void stopCountDown() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public void setTime(long milliSeconds, TextView timeText) {
        mMilliSeconds = milliSeconds;
        initCounter(timeText);
        calculateTime(milliSeconds,timeText);
    }

    private void calculateTime(long milliSeconds, TextView timeText) {
        mSeconds = (milliSeconds / 1000) % 60;
        mMinutes = (milliSeconds / (1000 * 60)) % 60;
        mHours = (milliSeconds / (1000 * 60 * 60)) % 24;
        mDays = (milliSeconds / (1000 * 60 * 60 * 24));

        displayText(timeText);
    }

    private void displayText(TextView timeText) {
        try{
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getTwoDigitNumber(mDays)+"d, ");
            stringBuilder.append(getTwoDigitNumber(mHours)+"h : ");
            stringBuilder.append(getTwoDigitNumber(mMinutes)+"m : ");
            stringBuilder.append(getTwoDigitNumber(mSeconds)+"s");
            timeText.setText(stringBuilder.toString());

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
