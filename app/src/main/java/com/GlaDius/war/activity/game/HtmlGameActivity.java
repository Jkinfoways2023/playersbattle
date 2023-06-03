package com.GlaDius.war.activity.game;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Toolbox;
import com.GlaDius.war.databinding.ActivityHtmlGameBinding;
import com.GlaDius.war.model.HtmlGameContestsPojo;
import com.GlaDius.war.utils.ActionAlertMessage;
import com.GlaDius.war.utils.CustomLoader;

public class HtmlGameActivity extends AppCompatActivity {

    private ActivityHtmlGameBinding binding;
    private Context context;
    private CustomLoader customLoader;
    private HtmlGameContestsPojo.ResultBean model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHtmlGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;
        customLoader=new CustomLoader((Activity) context,false);

        model=getIntent().getParcelableExtra(Toolbox.MODEL);

        initData();
    }

    private void initData() {
        loadWebView();
    }

    private void loadWebView() {
        binding.webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (url.equals("http://gamesworldpro.xyz/gamer/exit.html")){
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                },1500);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                customLoader.showLoader();

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                customLoader.dismissLoader();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("Error", description);
                Toast.makeText(context, "Error:" + description, Toast.LENGTH_SHORT).show();
            }
        });

        // All Requires For HTML Games.
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setLoadWithOverviewMode(true);
        binding.webView.getSettings().setUseWideViewPort(true);
        binding.webView.getSettings().setSupportMultipleWindows(true);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webView.isHorizontalScrollBarEnabled();
        binding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        binding.webView.getSettings().setAllowFileAccessFromFileURLs(true);
        binding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        binding.webView.loadUrl(model.getHtml_game_url());
    }

    @Override
    public void onBackPressed() {
        ActionAlertMessage.showTwoBtnDialog(context,getResources().getString(R.string.quit_game),
                getResources().getString(R.string.are_you_sure_you_want_to_exit_the_game),
                R.drawable.ic_question,new ActionAlertMessage.OnFunGameJoinInterface() {
                    @Override
                    public void onOkayBtnClick() {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onCancelBtnClick() {

                    }
                });
    }
}