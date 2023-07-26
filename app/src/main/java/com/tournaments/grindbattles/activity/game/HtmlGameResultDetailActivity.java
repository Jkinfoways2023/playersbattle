package com.tournaments.grindbattles.activity.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.adapter.game.GameResultDetailAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.common.Toolbox;
import com.tournaments.grindbattles.databinding.ActivityHtmlGameResultDetailBinding;
import com.tournaments.grindbattles.model.GameDetailResultModel;
import com.tournaments.grindbattles.model.HtmlGameContestsPojo;
import com.tournaments.grindbattles.model.HtmlGamePojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.CustomLoader;
import com.tournaments.grindbattles.utils.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HtmlGameResultDetailActivity extends AppCompatActivity {

    private ActivityHtmlGameResultDetailBinding binding;
    private Context context;
    private CustomLoader customLoader;
    private HtmlGameContestsPojo.ResultBean model;
    private SessionManager sessionManager;
    private HashMap<String, String> user;
    private HtmlGamePojo gameModel;
    private GameResultDetailAdapter adapter;
    private final ArrayList<GameDetailResultModel.ResultBean> arrayList=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHtmlGameResultDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context=this;
        sessionManager=new SessionManager(context);
        user = sessionManager.getUserDetails();
        customLoader=new CustomLoader((Activity) context,false);

        gameModel=getIntent().getParcelableExtra(Toolbox.GAME_MODEL);
        model=getIntent().getParcelableExtra(Toolbox.MODEL);

        initData();
    }

    private void initData() {

        binding.title.setText(gameModel.getTitle());
        binding.gameImage.setImageResource(gameModel.getImage());

        setAdapter();
        callGameFullResultApi();

        binding.backPress.setOnClickListener(v->onBackPressed());
    }

    private void setAdapter() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter=new GameResultDetailAdapter(arrayList,context);
        binding.recyclerView.setAdapter(adapter);
    }

    private void callGameFullResultApi() {
        binding.shimmerView.setVisibility(View.VISIBLE);
        binding.shimmerView.startShimmer();

        Uri.Builder builder = Uri.parse(Constant.SHOW_GAME_FULL_RESULT).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("user_id",user.get(SessionManager.KEY_ID));
        builder.appendQueryParameter("game_id",gameModel.getId());

        StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), response -> {
            try {
                Log.e("Res", response);
                binding.shimmerView.stopShimmer();
                binding.shimmerView.setVisibility(View.GONE);

                arrayList.clear();
                GameDetailResultModel model = new Gson().fromJson(response, GameDetailResultModel.class);
                if (model.getSuccess().equals("1")){
                    arrayList.addAll(model.getResult());
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_LONG).show();
                }

                if (!arrayList.isEmpty()){
                    binding.noDataLayout.setVisibility(View.GONE);
                }else {
                    binding.noDataLayout.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                customLoader.dismissLoader();
            }
        }, error -> {
            error.printStackTrace();
            Log.e("Error", Objects.requireNonNull(error.getMessage()));
            customLoader.dismissLoader();
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
}