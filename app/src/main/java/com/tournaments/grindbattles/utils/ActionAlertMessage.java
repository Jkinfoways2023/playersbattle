package com.tournaments.grindbattles.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputEditText;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.JoiningMatchActivity;
import com.tournaments.grindbattles.activity.MainActivity;
import com.tournaments.grindbattles.common.Constant;

import static com.tournaments.grindbattles.activity.JoiningMatchActivity.progressBar;

public class ActionAlertMessage {
    private String accessKey;
    private String encodeGameUserID1;
    private String encodeGameUserID2;
    private String encodeGameUserID3;
    private String encodeGameUserID4;

    private Context context;

    public ActionAlertMessage() {
    }

    public ActionAlertMessage(Context context) {
        this.context = context;
    }

    public void showJoinMatchAlert(final JoiningMatchActivity joiningMatchActivity, final String id, final String username, final String name, final String matchID, final String entryType, final String matchType, final String privateStatus, final int entryFee) {
        final Dialog dialog = new Dialog(joiningMatchActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_joinprompt_solo);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputLayout accessCodeView = (TextInputLayout) dialog.findViewById(R.id.accessCodeView);
        final TextInputEditText accessCode = (TextInputEditText) dialog.findViewById(R.id.accessCode);
        final TextInputEditText gameID = (TextInputEditText) dialog.findViewById(R.id.gameID);

        Button button = (Button) dialog.findViewById(R.id.next);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);

        final TextView textError = (TextView) dialog.findViewById(R.id.textError);
        TextView accessCodeInfoText = (TextView) dialog.findViewById(R.id.accessCodeInfoText);

        if (privateStatus.equals("yes")) {
            accessCodeView.setVisibility(View.VISIBLE);
            accessCodeInfoText.setVisibility(View.VISIBLE);
        }
        else {
            accessCode.setVisibility(View.GONE);
            accessCodeInfoText.setVisibility(View.GONE);
        }

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                accessKey = accessCode.getText().toString().trim();
                encodeGameUserID1 = gameID.getText().toString().trim();


                if (privateStatus.equals("yes") && accessKey.isEmpty() || accessKey.contains(" ")) {
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Invalid Access Code. Retry.");
                    progressBar.setVisibility(View.GONE);
                }
                if (!encodeGameUserID1.isEmpty()) {
                    joinSoloMatch(joiningMatchActivity,id,username,accessKey,encodeGameUserID1,name,matchID,entryType,matchType,privateStatus,entryFee);
                    dialog.dismiss();
                    progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Invalid Game Username. Retry.");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void joinSoloMatch(final JoiningMatchActivity joiningMatchActivity, final String id, final String username, final String accessKey, final String encodeGameUserID1, final String name, final String matchID, final String entryType, final String matchType, final String privateStatus, final int entryFee) {
        if (new ExtraOperations().haveNetworkConnection(joiningMatchActivity)) {
            Uri.Builder builder = Uri.parse(Constant.JOIN_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("match_id", matchID);
            builder.appendQueryParameter("user_id", id);
            builder.appendQueryParameter("username", username);
            builder.appendQueryParameter("name", name);
            builder.appendQueryParameter("is_private", privateStatus);
            builder.appendQueryParameter("accessKey", accessKey);
            builder.appendQueryParameter("pubg_id", encodeGameUserID1);
            builder.appendQueryParameter("entry_type", entryType);
            builder.appendQueryParameter("match_type", matchType);
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
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("2")) {
                            progressBar.setVisibility(View.GONE);
                            successDialog(joiningMatchActivity);
                        } else if (success.equals("3")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
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
            MySingleton.getInstance(joiningMatchActivity).addToRequestque(request);
        } else {
            Toast.makeText(joiningMatchActivity, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void joinDuoMatch(final JoiningMatchActivity joiningMatchActivity, final String id, final String username, final String accessKey, final String encodeGameUserID1, final String encodeGameUserID2, final String name, final String matchID, final String entryType, final String matchType, final String privateStatus, final int entryFee) {
        if (new ExtraOperations().haveNetworkConnection(joiningMatchActivity)) {
            Uri.Builder builder = Uri.parse(Constant.JOIN_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("match_id", matchID);
            builder.appendQueryParameter("user_id", id);
            builder.appendQueryParameter("username", username);
            builder.appendQueryParameter("name", name);
            builder.appendQueryParameter("is_private", privateStatus);
            builder.appendQueryParameter("accessKey", accessKey);
            builder.appendQueryParameter("pubg_id1", encodeGameUserID1);
            builder.appendQueryParameter("pubg_id2", encodeGameUserID2);
            builder.appendQueryParameter("entry_type", entryType);
            builder.appendQueryParameter("match_type", matchType);
            builder.appendQueryParameter("entry_fee", String.valueOf(entryFee));
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
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("2")) {
                            progressBar.setVisibility(View.GONE);
                            successDialog(joiningMatchActivity);
                        } else if (success.equals("3")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
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
            MySingleton.getInstance(joiningMatchActivity).addToRequestque(request);
        } else {
            Toast.makeText(joiningMatchActivity, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void joinSquadMatch(final JoiningMatchActivity joiningMatchActivity, final String id, final String username, final String accessKey, final String encodeGameUserID1, final String encodeGameUserID2, final String encodeGameUserID3, final String encodeGameUserID4, final String name, final String matchID, final String entryType, final String matchType, final String privateStatus, final int entryFee) {
        if (new ExtraOperations().haveNetworkConnection(joiningMatchActivity)) {
            Uri.Builder builder = Uri.parse(Constant.JOIN_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("match_id", matchID);
            builder.appendQueryParameter("user_id", id);
            builder.appendQueryParameter("username", username);
            builder.appendQueryParameter("name", name);
            builder.appendQueryParameter("is_private", privateStatus);
            builder.appendQueryParameter("accessKey", accessKey);
            builder.appendQueryParameter("pubg_id1", encodeGameUserID1);
            builder.appendQueryParameter("pubg_id2", encodeGameUserID2);
            builder.appendQueryParameter("pubg_id3", encodeGameUserID3);
            builder.appendQueryParameter("pubg_id4", encodeGameUserID4);
            builder.appendQueryParameter("entry_type", entryType);
            builder.appendQueryParameter("match_type", matchType);
            builder.appendQueryParameter("entry_fee", String.valueOf(entryFee));
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
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("1")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("2")) {
                            progressBar.setVisibility(View.GONE);
                            successDialog(joiningMatchActivity);
                        } else if (success.equals("3")) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
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
            MySingleton.getInstance(joiningMatchActivity).addToRequestque(request);
        } else {
            Toast.makeText(joiningMatchActivity, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void successDialog(final JoiningMatchActivity joiningMatchActivity) {
        final Dialog dialog = new Dialog(joiningMatchActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        TextView titleTv = (TextView) dialog.findViewById(R.id.titleTv);
        TextView subTitleTv = (TextView) dialog.findViewById(R.id.subTitleTv);
        TextView noteTv = (TextView) dialog.findViewById(R.id.noteTv);

        Button cancelBt = (Button) dialog.findViewById(R.id.cancelBt);
        Button okBt = (Button) dialog.findViewById(R.id.okBt);

        noteTv.setText("Congratulations!!! You have successfully joined this match. Entry fee has been deducted from your account if any. Room Id and Password are visible in match description before 15 minutes.");

        cancelBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    Intent intent = new Intent(joiningMatchActivity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    joiningMatchActivity.startActivity(intent);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        okBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    Intent intent = new Intent(joiningMatchActivity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    joiningMatchActivity.startActivity(intent);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void transactionDetailDialog(Context context, String id, String user_id, String account_holder_name, String account_holder_id, String wallet, String date, String payment_id, String coins, String amount, String type, String status, String remark) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_redeem_details);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView holderNameText = (TextView) dialog.findViewById(R.id.holderNameText);
        TextView holderIdText = (TextView) dialog.findViewById(R.id.holderIdText);
        TextView walletNameText = (TextView) dialog.findViewById(R.id.walletNameText);
        TextView dateText = (TextView) dialog.findViewById(R.id.dateText);
        TextView orderIdText = (TextView) dialog.findViewById(R.id.orderIdText);
        TextView coinsText = (TextView) dialog.findViewById(R.id.coinsText);
        TextView amountText = (TextView) dialog.findViewById(R.id.amountText);
        TextView modeText = (TextView) dialog.findViewById(R.id.modeText);
        TextView statusText = (TextView) dialog.findViewById(R.id.statusText);
        TextView remarkText = (TextView) dialog.findViewById(R.id.remarkText);

        TextView holderNameValue = (TextView) dialog.findViewById(R.id.holderNameValue);
        TextView holderIdValue = (TextView) dialog.findViewById(R.id.holderIdValue);
        TextView walletNameValue = (TextView) dialog.findViewById(R.id.walletNameValue);
        TextView dateValue = (TextView) dialog.findViewById(R.id.dateValue);
        TextView orderIdValue = (TextView) dialog.findViewById(R.id.orderIdValue);
        TextView coinsValue = (TextView) dialog.findViewById(R.id.coinsValue);
        TextView amountValue = (TextView) dialog.findViewById(R.id.amountValue);
        TextView modeValue = (TextView) dialog.findViewById(R.id.modeValue);
        TextView statusValue = (TextView) dialog.findViewById(R.id.statusValue);
        TextView remarkValue = (TextView) dialog.findViewById(R.id.remarkValue);

        if (type.equals("0")) {
            if (!account_holder_name.equals("null")){
                holderNameValue.setText(account_holder_name);
                holderNameText.setVisibility(View.VISIBLE);
                holderNameValue.setVisibility(View.VISIBLE);
            }
            else {
                holderNameText.setVisibility(View.GONE);
                holderNameValue.setVisibility(View.GONE);
            }


            if (!account_holder_id.equals("null")){
                holderIdValue.setText(account_holder_id);
                holderIdText.setVisibility(View.VISIBLE);
                holderIdValue.setVisibility(View.VISIBLE);
            }
            else {
                holderIdText.setVisibility(View.GONE);
                holderIdValue.setVisibility(View.GONE);
            }

            walletNameValue.setText(wallet);
            dateValue.setText(date);

            if (!id.equals("null")){
                orderIdValue.setText("PCDB"+id);
                orderIdText.setVisibility(View.VISIBLE);
                orderIdValue.setVisibility(View.VISIBLE);
            }
            else {
                orderIdText.setVisibility(View.GONE);
                orderIdValue.setVisibility(View.GONE);
            }

            if (!remark.equals("null")){
                remarkValue.setText(remark);
                remarkText.setVisibility(View.VISIBLE);
                remarkValue.setVisibility(View.VISIBLE);
            }
            else {
                remarkText.setVisibility(View.GONE);
                remarkValue.setVisibility(View.GONE);
            }

            coinsValue.setText("- ₹"+coins);
            amountValue.setText(String.valueOf(amount));
            coinsValue.setTextColor(context.getResources().getColor(R.color.colorError));
            amountValue.setTextColor(context.getResources().getColor(R.color.colorError));
        }
        else if (type.equals("1")){
            if (!account_holder_name.equals("null")){
                holderIdValue.setText(account_holder_name);
                holderIdText.setVisibility(View.VISIBLE);
                holderIdValue.setVisibility(View.VISIBLE);
            }
            else {
                holderIdText.setVisibility(View.GONE);
                holderIdValue.setVisibility(View.GONE);
            }

            if (!account_holder_id.equals("null")){
                holderNameValue.setText(account_holder_id);
                holderNameText.setVisibility(View.VISIBLE);
                holderNameValue.setVisibility(View.VISIBLE);
            }
            else {
                holderNameText.setVisibility(View.GONE);
                holderNameValue.setVisibility(View.GONE);
            }

            holderNameValue.setText(account_holder_name);
            holderIdValue.setText(account_holder_id);
            walletNameValue.setText(wallet);
            dateValue.setText(date);

            if (!id.equals("null")){
                orderIdValue.setText("PCCR"+id);
                orderIdText.setVisibility(View.VISIBLE);
                orderIdValue.setVisibility(View.VISIBLE);
            }
            else {
                orderIdText.setVisibility(View.GONE);
                orderIdValue.setVisibility(View.GONE);
            }

            if (!remark.equals("null")){
                remarkValue.setText(remark);
                remarkText.setVisibility(View.VISIBLE);
                remarkValue.setVisibility(View.VISIBLE);
            }
            else {
                remarkText.setVisibility(View.GONE);
                remarkValue.setVisibility(View.GONE);
            }

            coinsValue.setText("+ ₹"+coins);
            amountValue.setText(String.valueOf(amount));
            coinsValue.setTextColor(context.getResources().getColor(R.color.colorSuccess));
            amountValue.setTextColor(context.getResources().getColor(R.color.colorSuccess));
        }

        if(type.equals("1")){
            modeValue.setTextColor(context.getResources().getColor(R.color.colorSuccess));
            modeValue.setText(context.getResources().getString(R.string.credit));

        }else if(type.equals("0")){

            modeValue.setTextColor(context.getResources().getColor(R.color.colorError));
            modeValue.setText(context.getResources().getString(R.string.debit));

        }

        if(status.equals("0")){
            statusValue.setTextColor(context.getResources().getColor(R.color.colorWarning));
            statusValue.setText(context.getResources().getString(R.string.pending));

        }else if(status.equals("1")){

            statusValue.setTextColor(context.getResources().getColor(R.color.colorSuccess));
            statusValue.setText(context.getResources().getString(R.string.success));

        }else if(status.equals("2")){

            statusValue.setTextColor(context.getResources().getColor(R.color.colorError));
            statusValue.setText(context.getResources().getString(R.string.rejected));

        }

        Button okay = (Button) dialog.findViewById(R.id.okay);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        okay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void showFunGameJoinDialog(final Context context,OnFunGameJoinInterface listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fun_game_join);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button button = (Button) dialog.findViewById(R.id.next);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);

        final TextView textError = (TextView) dialog.findViewById(R.id.textError);
        TextView accessCodeInfoText = (TextView) dialog.findViewById(R.id.accessCodeInfoText);

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onCancelBtnClick();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onOkayBtnClick();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public interface OnFunGameJoinInterface{
        void onOkayBtnClick();
        void onCancelBtnClick();
    }

    public static void showInsufficientBalanceDialog(final Context context,OnFunGameJoinInterface listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_insufficient_balance);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Button button = (Button) dialog.findViewById(R.id.next);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onCancelBtnClick();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onOkayBtnClick();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public interface OnInsufficientInterface{
        void onOkayBtnClick();
        void onCancelBtnClick();
    }

    public static void showSingleBtnDialog(final Context context,String title,String message,int image,OnSingleBtnInterface listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_single_button);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleTextView = (TextView) dialog.findViewById(R.id.title);
        TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.image);

        titleTextView.setText(title);
        messageTextView.setText(message);
        if (image!=0) {
            imageView.setImageResource(image);
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }

        Button okBtn = (Button) dialog.findViewById(R.id.okBtn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onOkayBtnClick();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void showTwoBtnDialog(final Context context,String title,String message,int image,OnFunGameJoinInterface listener) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_two_button);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView titleTextView = (TextView) dialog.findViewById(R.id.title);
        final TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
        final ImageView imageView = (ImageView) dialog.findViewById(R.id.image);

        Button button = (Button) dialog.findViewById(R.id.next);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);

        titleTextView.setText(title);
        messageTextView.setText(message);
        if (image!=0) {
            imageView.setImageResource(image);
            imageView.setVisibility(View.VISIBLE);
        }else {
            imageView.setVisibility(View.GONE);
        }

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onCancelBtnClick();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                listener.onOkayBtnClick();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public interface OnSingleBtnInterface{
        void onOkayBtnClick();
    }
}
