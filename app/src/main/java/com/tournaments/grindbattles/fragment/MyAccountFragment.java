package com.tournaments.grindbattles.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.tournaments.grindbattles.BuildConfig;
import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.AboutUsActivity;
import com.tournaments.grindbattles.activity.ContactUsActivity;
import com.tournaments.grindbattles.activity.FAQActivity;
import com.tournaments.grindbattles.activity.KycActivity;
import com.tournaments.grindbattles.activity.MainActivity;
import com.tournaments.grindbattles.activity.MyProfileActivity;
import com.tournaments.grindbattles.activity.MyStatisticsActivity;
import com.tournaments.grindbattles.activity.MyWalletActivity;
import com.tournaments.grindbattles.activity.NotificationActivity;
import com.tournaments.grindbattles.activity.PrivacyPolicyActivity;
import com.tournaments.grindbattles.activity.SettingActivity;
import com.tournaments.grindbattles.activity.TermsConditionsActivity;
import com.tournaments.grindbattles.activity.TopPlayersActivity;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ObjIntConsumer;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View view;
    private boolean isNavigationHide = false;

    private CardView customerSupport;
    private CardView importantUpdates;
    private CardView myProfile;
    private CardView myStatistics;
    private CardView myWallet;
    private CardView topPlayers;
    private CardView settingsCard;

    private CardView aboutUs;
    private CardView privacyCard;
    private CardView logOut;
    private CardView rateApp;
    private CardView shareApp;
    private CardView moreApp;
    private CardView faqCard;
    private CardView termsCard;

    private LinearLayout amountWonLayout;
    private LinearLayout totalKillsLayout;
    private LinearLayout matchesPlayedLayout;

    private TextView appVersion;
    private TextView myAmountWon;
    private TextView myBalance;
    private TextView myKills;
    private TextView myMatchesNumber;
    private TextView myname;
    private TextView myusername;

    private String id;
    private String firstname;
    private String lastname;
    private String name;
    private String email;
    private String mnumber;
    private String kyc;
    private String username;
    private String password;

    private SessionManager session;
    private String matches_played;
    private String total_amount_won;
    private String total_kills;

    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;

    private SwitchCompat notificationSwitch;
    TextView kycstatus;
    CardView kyccard;
    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);

        callbackManager = CallbackManager.Factory.create();

        initView();
        initListener();
        initSession();

        loadSummery();

        try {
            this.appVersion.setText("App Version : v" + BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                }
            }
        });

        MyApplication MyApp = MyApplication.getInstance();

        notificationSwitch.setChecked(MyApp.getNotification());

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApp.saveIsNotification(isChecked);
                //OneSignal.setSubscription(isChecked);
            }
        });

        return view;
    }

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * MainActivity.navigation.getHeight()) : 0;
        MainActivity.navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username = user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);
        kyc = user.get(SessionManager.kyc);

        if(kyc.equalsIgnoreCase("0"))
        {
            //pending
            kycstatus.setVisibility(View.VISIBLE);
            kyccard.setVisibility(View.VISIBLE);

        }
        else if(kyc.equalsIgnoreCase("2"))
        {
            //completed
            kycstatus.setText("Kyc Under process");
            kycstatus.setTextColor(getContext().getColor(R.color.orange_A400));
            kycstatus.setVisibility(View.VISIBLE);
        }

        kyccard.setOnClickListener(v->{
            startActivity(new Intent(getActivity(), KycActivity.class));
        });
        try {
            if (!firstname.equals("null") && !lastname.equals("null")) {
                name = firstname + " " + lastname;
            } else if (!firstname.equals("null")) {
                name = firstname;
            } else {
                name = "Guest User";
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            name = "Guest User";
        }
        this.myname.setText(this.name);
        this.myusername.setText(this.username);
    }

    private void initListener() {
        this.matchesPlayedLayout.setOnClickListener(this);
        this.totalKillsLayout.setOnClickListener(this);
        this.amountWonLayout.setOnClickListener(this);
        this.myProfile.setOnClickListener(this);
        this.myWallet.setOnClickListener(this);
        this.myStatistics.setOnClickListener(this);
        this.topPlayers.setOnClickListener(this);
        this.importantUpdates.setOnClickListener(this);
        this.customerSupport.setOnClickListener(this);
        this.settingsCard.setOnClickListener(this);

        this.privacyCard.setOnClickListener(this);
        this.shareApp.setOnClickListener(this);
        this.logOut.setOnClickListener(this);
        this.aboutUs.setOnClickListener(this);
        this.faqCard.setOnClickListener(this);
        this.termsCard.setOnClickListener(this);
        this.rateApp.setOnClickListener(this);
        this.moreApp.setOnClickListener(this);
    }

    private void initView() {
        this.myname = (TextView) view.findViewById(R.id.name);
        this.myusername = (TextView) view.findViewById(R.id.myusername);
        this.myBalance = (TextView) view.findViewById(R.id.myBalance);
        this.myMatchesNumber = (TextView) view.findViewById(R.id.matchesPlayed);
        this.myKills = (TextView) view.findViewById(R.id.myKills);
        this.myAmountWon = (TextView) view.findViewById(R.id.amountWon);
        this.myProfile = (CardView) view.findViewById(R.id.profileCard);
        this.myWallet = (CardView) view.findViewById(R.id.myWalletCard);
        this.myStatistics = (CardView) view.findViewById(R.id.statsCard);
        this.topPlayers = (CardView) view.findViewById(R.id.topPlayersCard);
        this.importantUpdates = (CardView) view.findViewById(R.id.impUpdates);
        this.appVersion = (TextView) view.findViewById(R.id.appVersion);
        this.customerSupport = (CardView) view.findViewById(R.id.customerSupportCard);
        this.settingsCard = (CardView) view.findViewById(R.id.settingsCard);
        this.matchesPlayedLayout = (LinearLayout) view.findViewById(R.id.matchesPlayedLL);
        this.totalKillsLayout = (LinearLayout) view.findViewById(R.id.totalKillsLL);
        this.amountWonLayout = (LinearLayout) view.findViewById(R.id.amountWonLL);
        this.notificationSwitch = view.findViewById(R.id.switch_notification);

        this.privacyCard = (CardView) view.findViewById(R.id.privacyCard);
        this.aboutUs = (CardView) view.findViewById(R.id.aboutUsCard);
        this.shareApp = (CardView) view.findViewById(R.id.shareCard);
        this.logOut = (CardView) view.findViewById(R.id.logOutCard);
        this.faqCard = (CardView) view.findViewById(R.id.faqCard);
        this.termsCard = (CardView) view.findViewById(R.id.termsCard);
        this.rateApp = (CardView) view.findViewById(R.id.rateCard);
        this.moreApp =(CardView) view.findViewById(R.id.moreCard);
        this.notificationSwitch = view.findViewById(R.id.switch_notification);

        kycstatus=view.findViewById(R.id.kycstatus);
        kyccard=view.findViewById(R.id.kyccard);
    }

    private void loadSummery() {
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            Uri.Builder builder = Uri.parse(Constant.MY_SUMMARY_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("user_id", id);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            matches_played = jsonObject1.getString("maches_played");
                            total_kills = jsonObject1.getString("total_kills");
                            total_amount_won = jsonObject1.getString("amount_won");

                            if (matches_played == null || matches_played.equals("null") || matches_played.equals("")) {
                                myMatchesNumber.setText("0");
                            } else {
                                myMatchesNumber.setText(matches_played);
                            }
                            if (total_kills == null || total_kills.equals("null") || total_kills.equals("")) {
                                myKills.setText("0");
                            } else {
                                myKills.setText(total_kills);
                            }
                            if (total_amount_won == null || total_amount_won.equals("null") || total_amount_won.equals("")) {
                                myAmountWon.setText("0");
                            } else {
                                myAmountWon.setText(total_amount_won);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
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
            request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            MySingleton.getInstance(getActivity()).addToRequestque(request);
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.profileCard) {
            this.startActivity(new Intent(this.getActivity(), MyProfileActivity.class));
        } else if (id == R.id.myWalletCard) {
            this.startActivity(new Intent(this.getActivity(), MyWalletActivity.class));
        } else if (id == R.id.statsCard) {
            this.startActivity(new Intent(this.getActivity(), MyStatisticsActivity.class));
        } else if (id == R.id.topPlayersCard) {
            this.startActivity(new Intent(this.getActivity(), TopPlayersActivity.class));
        } else if (id == R.id.faqCard) {
            this.startActivity(new Intent(this.getActivity(), FAQActivity.class));
        } else if (id == R.id.impUpdates) {
            this.startActivity(new Intent(this.getActivity(), NotificationActivity.class));
        } else if (id == R.id.customerSupportCard) {
            this.startActivity(new Intent(this.getActivity(), ContactUsActivity.class));
        } else if (id == R.id.settingsCard) {
            this.startActivity(new Intent(this.getActivity(), SettingActivity.class));
        } else if (id == R.id.privacyCard) {
            this.startActivity(new Intent(this.getActivity(), PrivacyPolicyActivity.class));
        } else if (id == R.id.termsCard) {
            this.startActivity(new Intent(this.getActivity(), TermsConditionsActivity.class));
        } else if (id == R.id.aboutUsCard) {
            this.startActivity(new Intent(this.getActivity(), AboutUsActivity.class));
        } else if (id == R.id.faqCard) {
            this.startActivity(new Intent(this.getActivity(), FAQActivity.class));
        } else if (id == R.id.moreCard) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
        } else if (id == R.id.rateCard) {
            try {
                Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
                Intent rateApp = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(rateApp);
            } catch (ActivityNotFoundException e) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } else if (id == R.id.shareCard) {
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("text/plain");
            String string = this.getString(R.string.shareContent) + username;
            shareIntent.putExtra("android.intent.extra.SUBJECT", this.getString(R.string.shareSub));
            shareIntent.putExtra("android.intent.extra.TEXT", string);
            this.startActivity(Intent.createChooser(shareIntent, "Share using"));
        } else if (id == R.id.logOutCard) {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                signOutGoogle();
            } else if (AccessToken.getCurrentAccessToken() != null) {
                signOutFacebook();
            } else {
                session.logoutUser();
            }
        }
    }

    public void signOutGoogle() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                session.logoutUser();
            }
        });
    }

    public void signOutFacebook() {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
                session.logoutUser();
            }
        }).executeAsync();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        initSession();
        loadSummery();
    }


}
