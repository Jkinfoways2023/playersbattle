package com.tournaments.grindbattles.common;

import android.util.Base64;

public class Config {

    static {
        System.loadLibrary("keys");
    }

    public static native String getNativeKey1();

    public static native String getNativeKey2();

    public static native String getNativeKey3();

    public static native String getNativeKey4();

    public static native String getNativeKey5();

    // put your admin panel url here
    public static String ADMIN_PANEL_URL = new String(Base64.decode(getNativeKey1(), Base64.DEFAULT));
    public static String FILE_PATH_URL = new String(Base64.decode(getNativeKey2(), Base64.DEFAULT));
    public static String PAYTM_URL = new String(Base64.decode(getNativeKey3(), Base64.DEFAULT));
    public static String PAYPAL_URL = new String(Base64.decode(getNativeKey4(), Base64.DEFAULT));
    public static String PURCHASE_CODE = new String(Base64.decode(getNativeKey5(), Base64.DEFAULT));

    // Paytm Production API Details
    public static final String M_ID = "WoHAvf46210528804823";   //Paytm Merchand Id we got it in button_paytm credentials
    public static final String CHANNEL_ID = "WAP";              //Paytm Channel Id, got it in button_paytm credentials
    public static final String INDUSTRY_TYPE_ID = "Retail";     //Paytm industry type got it in button_paytm credential
    public static final String WEBSITE = "DEFAULT";
    public static final String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

    // UPI Payment Id
    public static final String UPI_ID = "";

    // How To Join Room YouTube Link
    public static final String YOUTUBE_CHANNEL_ID = "https://www.youtube.com/@ADOXRYNDERGAMING";
    public static final String HOW_TO_JOIN_URL = "https://www.youtube.com/watch?v=GtV9tqkj6Wo";

    // Discord Channel Link
    public static final String DISCORD_CHANNEL_ID = "https://discord.gg/6qMeuX";

    // UPI Payment Id
    //public static final String WEB_URL = "https://blackwarofficial.co.in";
    public static final String WEB_URL = "http://Grindbattle.com";

    // Refer & Reward Offer
    //set true to enable or set false to disable
    public static final boolean REFER_EARN = true;
    public static final boolean WATCH_EARN = true;

    // AdMob Keys
    //set admob app id and ad unit id
    public static final String AD_APP_ID = "ca-app-pub-3902536661112357~2203819569";
    public static final String AD_REWARDED_ID = "ca-app-pub-3902536661112357/9415245111";

    // Reward Ads Setup
    //set next reward time, watch video, pay rewars
    public static final String WATCH_COUNT = "5";       // Set minimum watch video
    public static final String PAY_REWARD = "2";        // Set amount after rewarded

    // Customer Support Details
    //set true to enable or set false to disable
    public static final boolean ENABLE_EMAIL_SUPPORT = true;
    public static final boolean ENABLE_PHONE_SUPPORT = true;
    public static final boolean ENABLE_WHATSAPP_SUPPORT = true;
    public static final boolean ENABLE_MESSENGER_SUPPORT = true;
    public static final boolean ENABLE_DISCORD_SUPPORT = true;

    // Follow Us Link
    //set true to enable or set false to disable
    public static final boolean ENABLE_TWITTER_FOLLOW = true;
    public static final boolean ENABLE_YOUTUBE_FOLLOW = true;
    public static final boolean ENABLE_FACEBOOK_FOLLOW = true;
    public static final boolean ENABLE_INSTAGRAM_FOLLOW = true;

}
