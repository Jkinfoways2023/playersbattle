package com.GlaDius.war.activity.game;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Toolbox;
import com.GlaDius.war.databinding.ActivityFindUserGameBinding;
import com.GlaDius.war.model.HtmlGameContestsPojo;
import com.GlaDius.war.model.HtmlGamePojo;
import com.GlaDius.war.session.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;

import static com.GlaDius.war.common.Toolbox.HTML_GAME_REQUEST_CODE;

public class FindUserGameActivity extends AppCompatActivity {

    private ActivityFindUserGameBinding binding;
    private Context context;
    private SessionManager sessionManager;
    private HashMap<String, String> user;
    private HtmlGameContestsPojo.ResultBean model;
    private HtmlGamePojo gameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFindUserGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;
        sessionManager=new SessionManager(context);
        user = sessionManager.getUserDetails();

        gameModel=getIntent().getParcelableExtra(Toolbox.GAME_MODEL);
        model=getIntent().getParcelableExtra(Toolbox.MODEL);

        initData();
    }

    private void initData() {
        binding.title.setText(gameModel.getTitle());
        binding.gameImage.setImageResource(gameModel.getImage());


        binding.backPress.setOnClickListener(v->onBackPressed());

        binding.playButton.setOnClickListener(v->{
            startActivityForResult(new Intent(context,HtmlGameActivity.class)
            .putExtra(Toolbox.MODEL,model),HTML_GAME_REQUEST_CODE);
        });

        String profileImage=user.get(SessionManager.KEY_PROFILE);
        if (!profileImage.equals("null")){
            Glide.with(getApplicationContext()).load(Config.FILE_PATH_URL+profileImage)
                    .apply(new RequestOptions().override(120,120))
                    .apply(new RequestOptions().placeholder(R.drawable.profile).error(R.drawable.profile))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(binding.userImage);
        }

        if (user.get(SessionManager.KEY_FIRST_NAME)!=null && user.get(SessionManager.KEY_LAST_NAME)!=null) {
            binding.userName.setText(String.format("%s %s", user.get(SessionManager.KEY_FIRST_NAME), user.get(SessionManager.KEY_LAST_NAME)));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.otherUserLayout.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.playButton.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    binding.playButton.setEnabled(true);
                    binding.playButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
                }
            }
        },2500);
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