package com.tournaments.grindbattles.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.VideoView;

import com.google.android.gms.ads.MobileAds;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.session.SessionManager;


public class SplashActivity extends AppCompatActivity {

    private boolean mIsBackButtonPressed;
    private SessionManager sessionManager;
    private VideoView videoView;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(getApplicationContext());
        MobileAds.initialize(SplashActivity.this, Config.AD_APP_ID);

        printHashKey();
        executeShellCommand();
        videoView = (VideoView) findViewById(R.id.videoView);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.gif_splash);
        videoView.setVideoURI(video);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (isFinishing())
                    return;
                try {
                    if (!Config.PURCHASE_CODE.isEmpty()) {
                        if (sessionManager.isLoggedIn()) {
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        videoView.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Config.PURCHASE_CODE.isEmpty()) {
                    if (sessionManager.isLoggedIn()) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent i = new Intent(SplashActivity.this, SignInActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        }, 2000);
    }

    private void executeShellCommand() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            finishAffinity();
            //Toast.makeText(SplashActivity.this, "It is rooted device", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.tournaments.skycoder.playerwar", PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();

    }

}
