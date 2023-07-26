package com.tournaments.grindbattles.activity.game;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.MyWalletActivity;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.common.Toolbox;
import com.tournaments.grindbattles.databinding.ActivityHtmlGameRulesBinding;
import com.tournaments.grindbattles.model.HtmlGameContestsPojo;
import com.tournaments.grindbattles.model.HtmlGamePojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ActionAlertMessage;
import com.tournaments.grindbattles.utils.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.tournaments.grindbattles.common.Toolbox.HTML_GAME_REQUEST_CODE;

public class HtmlGameRulesActivity extends AppCompatActivity {

    private ActivityHtmlGameRulesBinding binding;
    private Context context;
    private HtmlGameContestsPojo.ResultBean model;
    private SessionManager session;
    private HashMap<String, String> user;
    private HtmlGamePojo gameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHtmlGameRulesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;
        session = new SessionManager(context);
        user = session.getUserDetails();

        gameModel=getIntent().getParcelableExtra(Toolbox.GAME_MODEL);
        model=getIntent().getParcelableExtra(Toolbox.MODEL);

        initData();
    }

    private void initData() {
        binding.title.setText(model.getGame_title());
        binding.gameType.setText(model.getGame_type());
        binding.entryFee.setText(model.getEntry_fee());
        binding.winningFee.setText(model.getWinning_fee());
        binding.noOfUser.setText(model.getNo_of_user());

        binding.title.setText(gameModel.getTitle());
        binding.gameImage.setImageResource(gameModel.getImage());

        binding.webViewGameDescription.loadData(Base64.encodeToString(model.getGame_description().getBytes(), Base64.NO_PADDING),"text/html", "base64");
        binding.webViewAbout.loadData(Base64.encodeToString(model.getRule().getBytes(), Base64.NO_PADDING),"text/html", "base64");

        // set web view client
        binding.progressBar.setVisibility(View.VISIBLE);

        binding.webViewAbout.setWebViewClient(new MyWebViewClient());
        binding.webViewGameDescription.setWebViewClient(new MyWebViewClient());

        binding.backPress.setOnClickListener(v->onBackPressed());

        binding.joinNow.setOnClickListener(v->{
           /* model.setHtml_game_url("http://gamesworldpro.xyz//game//index.php?id=2&user_id=2704");
            startActivityForResult(new Intent(context,HtmlGameActivity.class)
                    .putExtra(Toolbox.MODEL,model),HTML_GAME_REQUEST_CODE);*/
            callJoinMatchApi();
        });
    }

    // custom web view client class who extends WebViewClient
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // load the url
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            binding.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(context, "Error:" + description, Toast.LENGTH_SHORT).show();
        }
    }

    private void callJoinMatchApi() {
        binding.progressBar.setVisibility(View.VISIBLE);

        Uri.Builder builder = Uri.parse(Constant.JOIN_GAME).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("entry_fee", model.getEntry_fee());
        builder.appendQueryParameter("user_id",user.get(SessionManager.KEY_ID));
        builder.appendQueryParameter("game_id",model.getId());

        StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), response -> {
            try {
                binding.progressBar.setVisibility(View.GONE);
                Log.e("Res", response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("result");
                JSONObject jsonObject1=jsonArray.getJSONObject(0);

                String success = jsonObject1.getString("success");
                String msg = jsonObject1.getString("msg");

                if (success.equals("1")) {
                    if (jsonObject1.has("data")) {
                        String data = jsonObject1.getString("data");
                        if (!data.isEmpty()){
                            model.setHtml_game_url(data);
                            startActivityForResult(new Intent(context,FindUserGameActivity.class)
                                    .putExtra(Toolbox.MODEL,model)
                                    .putExtra(Toolbox.GAME_MODEL,gameModel),HTML_GAME_REQUEST_CODE);
                        }
                    }
                } else if(success.equals("2")){
                    ActionAlertMessage.showInsufficientBalanceDialog(context, new ActionAlertMessage.OnFunGameJoinInterface() {
                        @Override
                        public void onOkayBtnClick() {
                            startActivity(new Intent(context, MyWalletActivity.class));
                        }

                        @Override
                        public void onCancelBtnClick() {

                        }
                    });
                }
                else if (success.equals("3")){
                    ActionAlertMessage.showSingleBtnDialog(context,getResources().getString(R.string.all_slot_are_full),
                            getResources().getString(R.string.you_can_not_join_this_match_because_all_slot_are_full),
                            R.drawable.ic_full_slot, new ActionAlertMessage.OnSingleBtnInterface() {
                                @Override
                                public void onOkayBtnClick() {

                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(),!msg.isEmpty() ? msg :getResources().getString(R.string.something_wrong) , Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                binding.progressBar.setVisibility(View.GONE);
            }
        }, error -> {
            error.printStackTrace();
            Log.e("Error", Objects.requireNonNull(error.getMessage()));
            binding.progressBar.setVisibility(View.GONE);
        }) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        MySingleton.getInstance(context).addToRequestque(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== RESULT_OK && requestCode==HTML_GAME_REQUEST_CODE){
            setResult(RESULT_OK);
            onBackPressed();
        }
    }
}