package com.tournaments.grindbattles.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.tournaments.grindbattles.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ActionAlertMessage;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class JoiningMatchActivity extends AppCompatActivity {

    private LinearLayout nextButtonLL;
    private LinearLayout addMoneyLL;
    private Button addMoney;
    private Button next;
    private Button cancelButton;

    private TextView walletBalanceTv;
    private TextView depositedTv;
    private TextView winningTv;
    private TextView bonusTv;
    private TextView balanceStatus;
    private TextView entryfee;

    private int entryFee;
    private int roomSize;
    private int totalJoined;
    private String privateStatus;
    private String type = "";
    private String joinStatus;
    private String matchID;
    private String matchRules;
    private String matchType;
    private String matchName;

    private SessionManager session;
    private String is_active, tot_coins, won_coins, bonus_coins;
    private String id;
    private String password;
    private String username;
    private String name;
    private String firstname;
    private String lastname;
    private String email;
    private String mnumber,kyc,is_block,status;
    public String selected_slot;
    ArrayList<String> selected_slots=new ArrayList<>();
    ArrayList<String> selected_positions=new ArrayList<>();
    ArrayList<String> selected_ids=new ArrayList<>();
    public static ProgressBar progressBar;
    LinearLayout    ll1,ll2,ll3,ll4;
    TextView position1,position2,position3,position4;
    TextView team1,team2,team3,team4;
    TextView add_player1,add_player2,add_player3,add_player4;
    ImageView edit1,edit2,edit3,edit4;

    String access_code_for_private_match="";
    String encodeGameUserID1="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joining_match);

        initToolbar();
        initView();
        initSession();
        initIntent();

        loadProfile();


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MyWalletActivity.class);
                startActivity(intent);
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = firstname+" "+lastname;
                if(selected_ids.size()==0)
                {
                    if(is_block.equalsIgnoreCase("1"))
                    {
                        Toast.makeText(JoiningMatchActivity.this, "Account banned contact admin...", Toast.LENGTH_LONG).show();
                    }
                    else if(status.equalsIgnoreCase("0"))
                    {
                        Toast.makeText(JoiningMatchActivity.this, "Account locked contact admin...", Toast.LENGTH_LONG).show();
                    }
                    else{
                        new ActionAlertMessage().showJoinMatchAlert(JoiningMatchActivity.this, id, username, name, matchID, type, matchType, privateStatus, entryFee);

                    }
                }
                else{

                    position1=findViewById(R.id.position1);
                    position2=findViewById(R.id.position2);
                    position3=findViewById(R.id.position3);
                    position4=findViewById(R.id.position4);
                    team1=findViewById(R.id.team1);
                    team2=findViewById(R.id.team2);
                    team3=findViewById(R.id.team3);
                    team4=findViewById(R.id.team4);
                    add_player1=findViewById(R.id.add_player1);
                    add_player2=findViewById(R.id.add_player2);
                    add_player3=findViewById(R.id.add_player3);
                    add_player4=findViewById(R.id.add_player4);
                    edit1=findViewById(R.id.edit1);
                    edit2=findViewById(R.id.edit2);
                    edit3=findViewById(R.id.edit3);
                    edit4=findViewById(R.id.edit4);

                    if(selected_ids.size()==1)
                    {
                        Log.e("ifcalled","11");
                        if(add_player1.getText().toString().trim().equalsIgnoreCase("") || add_player1.getText().toString().trim().equalsIgnoreCase("Player 1"))
                        {
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 1 detail", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.e("ifcalled","else 11");
                            encodeGameUserID1=selected_ids.get(0)+"____"+add_player1.getText().toString().trim();
                            if(is_block.equalsIgnoreCase("1"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account banned contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else if(status.equalsIgnoreCase("0"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account locked contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else{
                                joinmatch();
                            }
                        }


                    }
                    else if(selected_ids.size()==2)
                    {
                        if(add_player1.getText().toString().trim().equalsIgnoreCase("")|| add_player1.getText().toString().trim().equalsIgnoreCase("Player 1"))
                        {
                            Log.e("ifcalled","21");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 1 detail", Toast.LENGTH_SHORT).show();
                        }

                        else if(add_player2.getText().toString().trim().equalsIgnoreCase("")|| add_player2.getText().toString().trim().equalsIgnoreCase("Player 2"))
                        {
                            Log.e("ifcalled","22");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 2 detail", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.e("ifcalled","else 22");
                            encodeGameUserID1=selected_ids.get(0)+"____"+add_player1.getText().toString().trim();
                            encodeGameUserID1=encodeGameUserID1+"/_/"+selected_ids.get(1)+"____"+add_player2.getText().toString().trim();
                            if(is_block.equalsIgnoreCase("1"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account banned contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else if(status.equalsIgnoreCase("0"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account locked contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else{
                                joinmatch();
                            }
                        }

                    }

                    else if(selected_ids.size()==3)
                    {

                        if(add_player1.getText().toString().trim().equalsIgnoreCase("")|| add_player1.getText().toString().trim().equalsIgnoreCase("Player 1"))
                        {
                            Log.e("if","31");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 1 detail", Toast.LENGTH_SHORT).show();
                        }

                        else if(add_player2.getText().toString().trim().equalsIgnoreCase("")|| add_player2.getText().toString().trim().equalsIgnoreCase("Player 2"))
                        {
                            Log.e("if","32");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 2 detail", Toast.LENGTH_SHORT).show();
                        }
                         else if(add_player3.getText().toString().trim().equalsIgnoreCase("")|| add_player3.getText().toString().trim().equalsIgnoreCase("Player 3"))
                        {
                            Log.e("if","33");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 3 detail", Toast.LENGTH_SHORT).show();
                        }
                         else{
                            Log.e("if","else 33");
                             encodeGameUserID1=selected_ids.get(0)+"=>"+add_player1.getText().toString().trim();
                            encodeGameUserID1=encodeGameUserID1+"/_/"+selected_ids.get(1)+"____"+add_player2.getText().toString().trim();
                            encodeGameUserID1=encodeGameUserID1+"/_/"+selected_ids.get(2)+"____"+add_player3.getText().toString().trim();
                            if(is_block.equalsIgnoreCase("1"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account banned contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else if(status.equalsIgnoreCase("0"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account locked contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else{
                                joinmatch();
                            }
                        }


                    }
                    else if(selected_ids.size()==4){
                        if(add_player1.getText().toString().trim().equalsIgnoreCase("")|| add_player1.getText().toString().trim().equalsIgnoreCase("Player 1"))
                        {
                            Log.e("if","41");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 1 detail", Toast.LENGTH_SHORT).show();
                        }

                       else if(add_player2.getText().toString().trim().equalsIgnoreCase("")|| add_player2.getText().toString().trim().equalsIgnoreCase("Player 2"))
                        {
                            Log.e("if","42");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 2 detail", Toast.LENGTH_SHORT).show();
                        }
                        else if(add_player3.getText().toString().trim().equalsIgnoreCase("")|| add_player3.getText().toString().trim().equalsIgnoreCase("Player 3"))
                        {
                            Log.e("if","43");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 3 detail", Toast.LENGTH_SHORT).show();
                        }
                        else if(add_player4.getText().toString().trim().equalsIgnoreCase("")|| add_player4.getText().toString().trim().equalsIgnoreCase("Player 4"))
                        {
                            Log.e("if","44");
                            Toast.makeText(JoiningMatchActivity.this, "Enter player 4 detail", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.e("if","else 44");
                            encodeGameUserID1=selected_ids.get(0)+"____"+add_player1.getText().toString().trim();
                            encodeGameUserID1=encodeGameUserID1+"/_/"+selected_ids.get(1)+"____"+add_player2.getText().toString().trim();
                            encodeGameUserID1=encodeGameUserID1+"/_/"+selected_ids.get(2)+"____"+add_player3.getText().toString().trim();
                            encodeGameUserID1=encodeGameUserID1+"/_/"+selected_ids.get(3)+"____"+add_player4.getText().toString().trim();
                            if(is_block.equalsIgnoreCase("1"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account banned contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else if(status.equalsIgnoreCase("0"))
                            {
                                Toast.makeText(JoiningMatchActivity.this, "Account locked contact admin...", Toast.LENGTH_LONG).show();
                            }
                            else{
                                joinmatch();
                            }
                        }

                    }
                }
            }
        });

    }

    private void joinmatch()
    {
        if (new ExtraOperations().haveNetworkConnection(JoiningMatchActivity.this)) {
            Uri.Builder builder = Uri.parse(Constant.JOIN_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("match_id", matchID);
            builder.appendQueryParameter("user_id", id);
            builder.appendQueryParameter("username", username);
            builder.appendQueryParameter("name", name);
            builder.appendQueryParameter("is_private", privateStatus);
            builder.appendQueryParameter("accessKey", access_code_for_private_match);
            builder.appendQueryParameter("pubg_id", encodeGameUserID1);
            builder.appendQueryParameter("entry_type", type);
            builder.appendQueryParameter("match_type", matchType);
            builder.appendQueryParameter("slot_based", "1");
            builder.appendQueryParameter("selected_slot", String.valueOf(selected_ids.size()));
            builder.appendQueryParameter("entry_fee", String.valueOf(entryFee));
            Log.e("urlisssssss",builder.toString());
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
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(JoiningMatchActivity.this, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(JoiningMatchActivity.this, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("2")) {
                            progressBar.setVisibility(View.GONE);
                            ActionAlertMessage.successDialog(JoiningMatchActivity.this);
                        } else if (success.equals("3")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(JoiningMatchActivity.this, msg + "", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressBar.setVisibility(View.GONE);
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
            MySingleton.getInstance(JoiningMatchActivity.this).addToRequestque(request);
        } else {
            Toast.makeText(JoiningMatchActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
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

                            walletBalanceTv.setText(tot_coins);
                            winningTv.setText(won_coins);
                            bonusTv.setText(bonus_coins);

                            try {
                                depositedTv.setText(String.valueOf(Integer.parseInt(tot_coins)-Integer.parseInt(won_coins)-Integer.parseInt(bonus_coins)));
                            }
                            catch (NumberFormatException e){
                                e.printStackTrace();
                            }

                            if (is_active.equals("1")) {
                                if (Integer.parseInt(tot_coins) >= entryFee) {
                                    addMoneyLL.setVisibility(View.GONE);
                                    nextButtonLL.setVisibility(View.VISIBLE);
                                    balanceStatus.setText(R.string.sufficientBalanceText);
                                } else {
                                    addMoneyLL.setVisibility(View.VISIBLE);
                                    nextButtonLL.setVisibility(View.GONE);
                                    balanceStatus.setTextColor(Color.parseColor("#ff0000"));
                                    balanceStatus.setText(R.string.you_don_t_have_sufficient_balance);
                                }
                            }
                            else {
                                addMoneyLL.setVisibility(View.GONE);
                                nextButtonLL.setVisibility(View.GONE);
                                balanceStatus.setTextColor(Color.parseColor("#ff0000"));
                                balanceStatus.setText("You are not eligible to Join Match as your account is not active.");
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
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        this.walletBalanceTv = (TextView) findViewById(R.id.walletBalanceTv);
        this.depositedTv = (TextView) findViewById(R.id.depositedTv);
        this.winningTv = (TextView) findViewById(R.id.winningTv);
        this.bonusTv = (TextView) findViewById(R.id.bonusTv);
        entryfee = (TextView) findViewById(R.id.entryFee);
        balanceStatus = (TextView) findViewById(R.id.statusTextView);
        next = (Button) findViewById(R.id.next);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        addMoney = (Button) findViewById(R.id.addMoneyButton);
        nextButtonLL = (LinearLayout) findViewById(R.id.nextButtonLL);
        addMoneyLL = (LinearLayout) findViewById(R.id.addMoneyLL);
        progressBar = findViewById(R.id.progressBar);

        ll1=findViewById(R.id.ll1);
        ll2=findViewById(R.id.ll2);
        ll3=findViewById(R.id.ll3);
        ll4=findViewById(R.id.ll4);

        position1=findViewById(R.id.position1);
        position2=findViewById(R.id.position2);
        position3=findViewById(R.id.position3);
        position4=findViewById(R.id.position4);
        team1=findViewById(R.id.team1);
        team2=findViewById(R.id.team2);
        team3=findViewById(R.id.team3);
        team4=findViewById(R.id.team4);
        add_player1=findViewById(R.id.add_player1);
        add_player2=findViewById(R.id.add_player2);
        add_player3=findViewById(R.id.add_player3);
        add_player4=findViewById(R.id.add_player4);
        edit1=findViewById(R.id.edit1);
        edit2=findViewById(R.id.edit2);
        edit3=findViewById(R.id.edit3);
        edit4=findViewById(R.id.edit4);




        addMoneyLL.setVisibility(View.GONE);
        nextButtonLL.setVisibility(View.GONE);
        balanceStatus.setTextColor(Color.parseColor("#000000"));
        balanceStatus.setText("Please wait a few seconds...");
    }

    private void initIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.matchID = getIntent().getStringExtra("matchID");
            this.matchName = getIntent().getStringExtra("matchName");
            this.matchType = getIntent().getStringExtra("matchType");
            this.entryFee = getIntent().getIntExtra("entryFee",0);
            this.type = getIntent().getStringExtra("entryType");
            this.joinStatus = getIntent().getStringExtra("JoinStatus");
            this.privateStatus = getIntent().getStringExtra("isPrivate");
            this.matchRules = getIntent().getStringExtra("matchRules");
            this.roomSize = getIntent().getIntExtra("ROOM_SIZE_KEY",100);
            this.totalJoined = getIntent().getIntExtra("TOTAL_JOINED_KEY",0);

            if (this.matchType.equals("Free")) {
                this.entryfee.setText("Free");
                this.entryfee.setTextColor(Color.parseColor("#1E7E34"));
            } else {
                this.entryfee.setText(String.valueOf(entryFee));
            }

            if(getIntent().hasExtra("from"))
            {
                Log.e("selected_ids",getIntent().getStringExtra("selected_slot_id"));
                Log.e("selected_ids",getIntent().getStringExtra("selected_slot"));
                Log.e("selected_ids",getIntent().getStringExtra("selected_slot_position"));

                String[] stringArray1 = getIntent().getStringExtra("selected_slot_id").split(",");
                String[] stringArray2 = getIntent().getStringExtra("selected_slot").split(",");
                String[] stringArray3 = getIntent().getStringExtra("selected_slot_position").split(",");

                for (String value : stringArray1)
                {
                    selected_ids.add(value);
                }
                for (String value : stringArray2)
                {
                    selected_slots.add(value);
                }
                for (String value : stringArray3)
                {
                    selected_positions.add(value);
                }

               /* for(int a=0;a<selected_ids.size();a++)
                {
                    team1.setText("Team "+selected_slots.get(a));
                    position1.setText("Team "+selected_positions.get(a));

                    team2.setText("Team "+selected_slots.get(a));
                    position2.setText("Team "+selected_positions.get(a));

                    team3.setText("Team "+selected_slots.get(a));
                    position3.setText("Team "+selected_positions.get(a));

                    team4.setText("Team "+selected_slots.get(a));
                    position4.setText("Team "+selected_positions.get(a));
                }*/
                if(selected_ids.size()==1)
                {
                    team1.setText("Team "+selected_slots.get(0));
                    position1.setText(selected_positions.get(0));
                }


                if(selected_ids.size()==2)
                {
                    team1.setText("Team "+selected_slots.get(0));
                    position1.setText(selected_positions.get(0));

                    team2.setText("Team "+selected_slots.get(1));
                    position2.setText(selected_positions.get(1));
                }

                if(selected_ids.size()==3)
                {
                    team1.setText("Team "+selected_slots.get(0));
                    position1.setText(selected_positions.get(0));

                    team2.setText("Team "+selected_slots.get(1));
                    position2.setText(selected_positions.get(1));

                    team3.setText("Team " + selected_slots.get(2));
                    position3.setText(selected_positions.get(2));
                }

                if(selected_ids.size()==4) {
                    team1.setText("Team " + selected_slots.get(0));
                    position1.setText(selected_positions.get(0));

                    team2.setText("Team " + selected_slots.get(1));
                    position2.setText(selected_positions.get(1));

                    team3.setText("Team " + selected_slots.get(2));
                    position3.setText(selected_positions.get(2));

                    team4.setText("Team " + selected_slots.get(3));
                    position4.setText(selected_positions.get(3));
                }





                LinearLayout squadFeeLL=findViewById(R.id.squadFeeLL);
                squadFeeLL.setVisibility(View.VISIBLE);

                if (this.matchType.equals("Free")) {
                    this.entryfee.setText("Free");
                    this.entryfee.setTextColor(Color.parseColor("#1E7E34"));
                } else {
                    this.entryfee.setText(String.valueOf(entryFee));
                }


                TextView squadEntryFee=findViewById(R.id.squadEntryFee);
//                squadEntryFee.setText("Total payable = "+String.valueOf(selected_ids.size())+" X "+entryFee);
                this.entryFee =selected_ids.size()*this.entryFee;
                TextView squadEntryFeeRight=findViewById(R.id.squadEntryFeeRight);
                squadEntryFeeRight.setText(String.valueOf(entryFee));

                findViewById(R.id.team_data).setVisibility(View.VISIBLE);
                if(selected_ids.size()==1)
                {

                    ll2.setVisibility(View.GONE);
                    ll3.setVisibility(View.GONE);
                    ll4.setVisibility(View.GONE);
                }
                else if(selected_ids.size()==2)
                {
                    ll3.setVisibility(View.GONE);
                    ll4.setVisibility(View.GONE);
                }
                else if(selected_ids.size()==3)
                {
                    ll4.setVisibility(View.GONE);
                }

                edit1.setOnClickListener(v->{
                    showdialog(add_player1);
                });
                add_player1.setOnClickListener(v->{
                    showdialog(add_player1);
                });

                edit2.setOnClickListener(v->{
                    showdialog(add_player2);
                });
                add_player2.setOnClickListener(v->{
                    showdialog(add_player2);
                });

                edit3.setOnClickListener(v->{
                    showdialog(add_player3);
                });
                add_player3.setOnClickListener(v->{
                    showdialog(add_player3);
                });

                edit4.setOnClickListener(v->{
                    showdialog(add_player4);
                });
                add_player4.setOnClickListener(v->{
                    showdialog(add_player4);
                });




            }



            try{
                if (!joinStatus.equals("null")) {
                    next.setText("Add New Entry");
                }
                else {
                    next.setText("Join Now");
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.joiningtoolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Joining Match");
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
        kyc=user.get(SessionManager.kyc);
        is_block=user.get(SessionManager.is_block);
        status=user.get(SessionManager.status);
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }


    public void showdialog(TextView tv)
    {
        final Dialog dialog = new Dialog(JoiningMatchActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_joinprompt_solo);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputEditText gameID = (TextInputEditText) dialog.findViewById(R.id.gameID);


        Button button = (Button) dialog.findViewById(R.id.next);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);
        final TextView textError = (TextView) dialog.findViewById(R.id.textError);
        TextView accessCodeInfoText = (TextView) dialog.findViewById(R.id.accessCodeInfoText);
        final TextInputLayout accessCodeView = (TextInputLayout) dialog.findViewById(R.id.accessCodeView);
        final TextInputEditText accessCode = (TextInputEditText) dialog.findViewById(R.id.accessCode);

        if (privateStatus.equals("yes")) {
            if(access_code_for_private_match.equalsIgnoreCase(""))
            {
                accessCodeView.setVisibility(View.VISIBLE);
                accessCodeInfoText.setVisibility(View.VISIBLE);
            }
        }
        else {
            accessCode.setVisibility(View.GONE);
            accessCodeInfoText.setVisibility(View.GONE);
        }
        button.setOnClickListener(v->{
            if(gameID.getText().toString().trim().equalsIgnoreCase(""))
            {
                Toast.makeText(this, "Enter Game Id", Toast.LENGTH_SHORT).show();
                return;
            }
            tv.setText(gameID.getText().toString().trim());
            dialog.dismiss();
        });

        button2.setOnClickListener(v->{
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
