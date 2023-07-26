package com.tournaments.grindbattles.services;

import android.app.NotificationManager;
import android.content.SharedPreferences;


import com.tournaments.grindbattles.R;


public class NotificationExtenderExample/* extends NotiFicationE */{

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    String isAnnouncement,message, bigpicture, title, date, url;
    private String NOTIFICATION_CHANNEL_ID = "sky_ch_1";

    public SharedPreferences preferences;
    public String prefName = "Sky_Winner";
    public int count = 0;
/*
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedEvent receivedResult) {

        title = receivedResult.payload.title;
        message = receivedResult.payload.body;
        bigpicture = receivedResult.payload.bigPicture;

        try {
            isAnnouncement = receivedResult.payload.additionalData.getString("is_announcement");
            title = receivedResult.payload.additionalData.getString("notification_title");
            message = receivedResult.payload.additionalData.getString("notification_msg");
            bigpicture = receivedResult.payload.additionalData.getString("tpath2");
            url = receivedResult.payload.additionalData.getString("external_link");
            date = receivedResult.payload.additionalData.getString("txtDate");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isAnnouncement.equals("0")) {
            getCounter();
            saveCounter(count);
        }

        sendNotification();
        return true;
    }

    public void saveCounter(int count) {
        count = count+1;

        preferences = this.getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("counter", count);
        editor.apply();
    }

    public void getCounter() {
        preferences = this.getSharedPreferences(prefName, 0);
        count = preferences.getInt("counter", 0);
    }

    private void sendNotification() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent;
        if(isAnnouncement.equals("0") && !url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(this, NotificationDetailsActivity.class);
            intent.putExtra("isNotification", true);
            intent.putExtra("id", "0");
            intent.putExtra("title", title);
            intent.putExtra("message", message);
            intent.putExtra("image", bigpicture);
            intent.putExtra("url", url);
            intent.putExtra("created", date);
        }

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SkyWinner Channel";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setSound(uri)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setLights(Color.RED, 800, 800);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(message);

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)).setSummaryText(message));
        } else {
            mBuilder.setContentText(message);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.drawable.ic_stat_onesignal_default;
        } else {
            return R.drawable.ic_stat_onesignal_default;
        }
    }

    private int getColour() {
        return 0x3F51B5;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }*/
}