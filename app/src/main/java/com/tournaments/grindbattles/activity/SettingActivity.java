package com.tournaments.grindbattles.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.tournaments.grindbattles.BuildConfig;
import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.session.SessionManager;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView aboutUs;
    private CardView privacyCard;
    private CardView logOut;
    private CardView rateApp;
    private CardView shareApp;
    private CardView moreApp;
    private CardView faqCard;
    private CardView termsCard;

    private TextView appVersion;
    SwitchCompat notificationSwitch;

    private CallbackManager callbackManager;
    private GoogleApiClient googleApiClient;

    private String id;
    private String firstname;
    private String lastname;
    private String name;
    private String email;
    private String mnumber;
    private String username;
    private String password;

    private SessionManager session;
    MyApplication MyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        callbackManager = CallbackManager.Factory.create();

        initToolbar();
        initView();
        initListener();
        initSession();

        MyApp = MyApplication.getInstance();
        notificationSwitch.setChecked(MyApp.getNotification());

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyApp.saveIsNotification(isChecked);
                //OneSignal.sub(isChecked);
            }
        });

        try {
            this.appVersion.setText("App Version : v"+ BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initListener() {
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
        this.privacyCard = (CardView) findViewById(R.id.privacyCard);
        this.aboutUs = (CardView) findViewById(R.id.aboutUsCard);
        this.shareApp = (CardView) findViewById(R.id.shareCard);
        this.logOut = (CardView) findViewById(R.id.logOutCard);
        this.appVersion = (TextView) findViewById(R.id.appVersion);
        this.faqCard = (CardView) findViewById(R.id.faqCard);
        this.termsCard = (CardView) findViewById(R.id.termsCard);
        this.rateApp = (CardView) findViewById(R.id.rateCard);
        this.moreApp =(CardView) findViewById(R.id.moreCard);
        this.notificationSwitch = findViewById(R.id.switch_notification);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id==R.id.privacyCard){
            this.startActivity(new Intent(this, PrivacyPolicyActivity.class));
        }
        else if (id==R.id.termsCard){
            this.startActivity(new Intent(this, TermsConditionsActivity.class));
        }
        else if (id==R.id.aboutUsCard){
            this.startActivity(new Intent(this, AboutUsActivity.class));
        }
        else if (id==R.id.faqCard){
            this.startActivity(new Intent(this, FAQActivity.class));
        }
        else if (id==R.id.moreCard){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
        }
        else if (id==R.id.rateCard){
            try {
                Uri uri = Uri.parse("market://details?id="+BuildConfig.APPLICATION_ID);
                Intent rateApp = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(rateApp);
            }catch (ActivityNotFoundException e){
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        }
        else if (id==R.id.shareCard){
            Intent shareIntent = new Intent("android.intent.action.SEND");
            shareIntent.setType("text/plain");
            String string = this.getString(R.string.shareContent)+username;
            shareIntent.putExtra("android.intent.extra.SUBJECT", this.getString(R.string.shareSub));
            shareIntent.putExtra("android.intent.extra.TEXT", string);
            this.startActivity(Intent.createChooser(shareIntent, "Share using"));
        }
        else if (id==R.id.logOutCard){
            if (googleApiClient != null && googleApiClient.isConnected()){
                signOutGoogle();
            }
            else if (AccessToken.getCurrentAccessToken() != null) {
                signOutFacebook();
            }
            else {
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

    private void initToolbar() {
        ImageView backpress = (ImageView) findViewById(R.id.backPress);
       backpress.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
    }

}
