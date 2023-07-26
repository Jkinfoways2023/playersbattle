package com.tournaments.grindbattles.activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.session.SessionManager;

public class RefereEarnActivity extends AppCompatActivity {

    private LinearLayout noReferralOffer;
    private LinearLayout referralOfferLL;

    private TextView referCode;
    private TextView referDesc;
    private TextView referralExpiredText;
    private ImageView howrefer;
    private Button referNow;

    private String shareContent;
    private String shareSub;

    private SessionManager session;
    private String id;
    private String password;
    private String username;
    private String firstname;
    private String lastname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refere_earn);
        session = new SessionManager(getApplicationContext());

        initToolbar();
        initSession();
        initView();

        referDesc.setText(R.string.desc_refer);
        referCode.setText(username);

        if (Config.REFER_EARN) {
            referCode.setText(this.username);
        } else if (!Config.REFER_EARN) {
            noReferralOffer.setVisibility(View.VISIBLE);
            referralOfferLL.setVisibility(View.GONE);
        }

        this.referNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContent = getString(R.string.shareContent)+" "+username;
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", getString(R.string.shareSub));
                intent.putExtra("android.intent.extra.TEXT", shareContent);
                startActivity(Intent.createChooser(intent, "Share using"));
            }
        });
    }

    private void initView() {
        this.referNow = (Button) findViewById(R.id.referButton);
        this.referDesc = (TextView) findViewById(R.id.refMessage);
        this.referCode = (TextView) findViewById(R.id.referCode);
        this.howrefer = (ImageView) findViewById(R.id.howrefer);
        this.noReferralOffer = (LinearLayout) findViewById(R.id.noReferOffer);
        this.referralOfferLL = (LinearLayout) findViewById(R.id.referralLL);
        this.referralExpiredText = (TextView) findViewById(R.id.referralExpiredText);
    }

    private void initSession() {
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
       // toolbar.setTitle((CharSequence) "Refer & Earn");
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
        getMenuInflater().inflate(R.menu.refer_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.myreferrals) {
            Intent intent = new Intent(RefereEarnActivity.this,MyReferralsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.leaderboard) {
            Intent intent = new Intent(RefereEarnActivity.this,LeaderboardReferActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId() == R.id.terms) {
            Intent intent = new Intent(RefereEarnActivity.this,TermsConditionsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
