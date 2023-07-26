package com.tournaments.grindbattles.activity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import com.tournaments.grindbattles.BuildConfig;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.utils.ExtraOperations;

public class UpdateAppActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 5;
    private static final int STORAGE_PERMISSION_CODE = 101;
    public static String TAG = "UpdateApp";

    private String updateURL;
    private String updatedOn;
    private String whatsNewData;
    private String isForceUpdate = "0";
    private String latestVersion;

    private Button later;
    private Button update;
    private TextView updateDate;
    private TextView whatsNew;
    private TextView newVersion;
    private TextView forceUpdateNote;
    private ProgressBar progressBar;
    private RelativeLayout progressRl;
    private TextView progressCompleteTv;
    private TextView progressRemainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        this.updateDate = (TextView) findViewById(R.id.date);
        this.newVersion = (TextView) findViewById(R.id.version);
        this.whatsNew = (TextView) findViewById(R.id.whatsnew);
        this.forceUpdateNote = (TextView) findViewById(R.id.forceUpdateNote);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.later = (Button) findViewById(R.id.laterButton);
        this.update = (Button) findViewById(R.id.updateButton);

        this.progressRemainTv = (TextView) findViewById(R.id.progressRemainTv);
        this.progressCompleteTv = (TextView) findViewById(R.id.progressCompleteTv);
        this.progressRl = (RelativeLayout) findViewById(R.id.progressRl);

        this.isForceUpdate = getIntent().getStringExtra("forceUpdate");
        this.updateURL = getIntent().getStringExtra("updateURL");
        this.updatedOn = getIntent().getStringExtra("updateDate");
        this.whatsNewData = getIntent().getStringExtra("whatsNew");
        this.latestVersion = getIntent().getStringExtra("latestVersionName");

        this.updateDate.setText("Updated On: "+this.updatedOn);
        this.newVersion.setText("New Version: v"+ latestVersion+".0.0");

        if (whatsNewData != null){
            this.whatsNew.setText(whatsNewData);
        }

        whatsNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(updateURL);
            }
        });

        if (this.isForceUpdate.equals("1"))
        {
            this.later.setVisibility(View.GONE);
            this.forceUpdateNote.setVisibility(View.VISIBLE);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAppActivity.this.finish();
            }
        });

    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(UpdateAppActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(UpdateAppActivity.this, new String[] { permission }, requestCode);
        }
        else {
            if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                downloadUpdate(UpdateAppActivity.this);
            }else {
                Toast.makeText(getApplicationContext(),"No internet connection found!", Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(UpdateAppActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 & grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(UpdateAppActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                    downloadUpdate(UpdateAppActivity.this);
                }else {
                    Toast.makeText(getApplicationContext(),"No internet connection found!", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(UpdateAppActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        Toast.makeText(context, R.string.no_internet_title, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(UpdateAppActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateAppActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    return true;
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(UpdateAppActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                    return false;
                }
            } else {
                // Permission has already been granted
                return true;
            }
        }else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Storage permission is granted");
            return true;
        }
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void downloadUpdate(final Context context){
        try{
            final File apk_file_path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "skywinner-multigames.apk");
            if (apk_file_path.exists()) apk_file_path.delete();

            Log.v(TAG,"Downloading request on url :"+updateURL);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateURL));
            request.setDescription(BuildConfig.VERSION_NAME+"");
            request.setTitle(context.getString(R.string.app_name));
            //set destination
            final Uri uri = Uri.parse("file://" + apk_file_path);
            request.setDestinationUri(uri);

            // get download service and enqueue file
            final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            progressRl.setVisibility(View.VISIBLE);
            later.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            forceUpdateNote.setVisibility(View.GONE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean downloading = true;
                    while(downloading) {
                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(downloadId);
                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();

                        final int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        }
                        int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        if (bytes_total != 0) {
                            final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                            UpdateAppActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress((int) dl_progress);
                                    progressCompleteTv.setText(String.valueOf(dl_progress)+"%");
                                    progressRemainTv.setText(String.valueOf(100-dl_progress)+"%");
                                }
                            });
                        }
                        cursor.close();

                    }
                }
            }).start();


            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    //BroadcastReceiver on Complete
                    if (apk_file_path.exists()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Uri apkUri = FileProvider.getUriForFile(context, getApplicationContext().getPackageName() + ".fileprovider", apk_file_path);
                            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            intent.setData(apkUri);
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else {
                            Uri apkUri = Uri.fromFile(apk_file_path);
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(apkUri, manager.getMimeTypeForDownloadedFile(downloadId));
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(ctxt, "Something went wrong", Toast.LENGTH_SHORT).show();
                        progressRl.setVisibility(View.GONE);
                        if (isForceUpdate.equals("1")) {
                            later.setVisibility(View.GONE);
                            update.setVisibility(View.VISIBLE);
                            forceUpdateNote.setVisibility(View.VISIBLE);
                        }
                        else {
                            later.setVisibility(View.VISIBLE);
                            update.setVisibility(View.VISIBLE);
                            forceUpdateNote.setVisibility(View.GONE);
                        }
                    }
                    context.unregisterReceiver(this);
                }
            };
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        }catch (Exception e){
            e.printStackTrace();
            progressRl.setVisibility(View.GONE);
            if (isForceUpdate.equals("1")) {
                later.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                forceUpdateNote.setVisibility(View.VISIBLE);
                openWebPage(updateURL);
            }
            else {
                later.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);
                forceUpdateNote.setVisibility(View.GONE);
            }
        }

    }

}
