package com.GlaDius.war.utils;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import com.GlaDius.war.R;

import androidx.appcompat.app.AlertDialog;

public class CustomLoader {

    private AlertDialog alertDialog;

    public CustomLoader(Activity activity, boolean cancelable) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_loader_dialog, null);
        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCancelable(cancelable);
    }


    public void showLoader() {
        if (alertDialog != null && !alertDialog.isShowing())
            alertDialog.show();
    }

    public void dismissLoader() {
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
    }
}
