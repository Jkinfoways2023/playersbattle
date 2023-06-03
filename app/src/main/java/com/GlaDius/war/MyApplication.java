package com.GlaDius.war;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.onesignal.OSDeviceState;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;
import com.GlaDius.war.activity.NotificationDetailsActivity;
import com.GlaDius.war.activity.SplashActivity;

import org.json.JSONObject;


public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "SkyWinner";

    public MyApplication() {
        mInstance = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId("4ac49b76-376a-4da7-bc58-438f9a50fcb7");
        OSDeviceState device = OneSignal.getDeviceState();
        boolean areNotificationsEnabled = device.areNotificationsEnabled();
        if (!areNotificationsEnabled ){
            OneSignal.addTrigger("showPrompt", "true");
        }

        Fresco.initialize(this);
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void saveIsNotification(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IsNotification", flag);
        editor.apply();
    }

    public boolean getNotification() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsNotification", true);
    }

    public class NotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult osNotificationOpenedResult) {
            JSONObject data = osNotificationOpenedResult.getNotification().toJSONObject();
            Log.e("data", "" + data);
            String isAnnouncement;
            String isExternalLink;
            String title;
            String msg;
            String path;
            String date;
            if (data != null) {
                isAnnouncement = data.optString("is_announcement", null);
                title = data.optString("notification_title", null);
                msg = data.optString("notification_msg", null);
                path = data.optString("tpath2", null);
                isExternalLink = data.optString("external_link", null);
                date = data.optString("txtDate", null);
                if (isAnnouncement != null) {
                    if (!isAnnouncement.equals("0")) {
                        Intent intent = new Intent(MyApplication.this, NotificationDetailsActivity.class);
                        intent.putExtra("isNotification", true);
                        intent.putExtra("id", "0");
                        intent.putExtra("title", title);
                        intent.putExtra("message", msg);
                        intent.putExtra("image", path);
                        intent.putExtra("url", isExternalLink);
                        intent.putExtra("created", date);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        try {
                            if (!isExternalLink.equals("false")) {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(isExternalLink));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }catch (ActivityNotFoundException e){
                                    e.printStackTrace();
                                }
                            } else {
                                Intent intent = new Intent(MyApplication.this, SplashActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }catch (ActivityNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}