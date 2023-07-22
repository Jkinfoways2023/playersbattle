package com.GlaDius.war.fragment.game;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.GlaDius.war.MyApplication;
import com.GlaDius.war.activity.game.HtmlGameRulesActivity;
import com.GlaDius.war.adapter.game.GameContestsAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.common.Toolbox;
import com.GlaDius.war.databinding.FragmentGameContestsBinding;
import com.GlaDius.war.model.HtmlGameContestsPojo;
import com.GlaDius.war.model.HtmlGamePojo;
import com.GlaDius.war.utils.MySingleton;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameContestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameContestsFragment extends Fragment {

    private FragmentGameContestsBinding binding;
    private Context context;
    private GameContestsAdapter adapter;
    private final ArrayList<HtmlGameContestsPojo.ResultBean> arrayList=new ArrayList();
    private HtmlGamePojo model;

    public GameContestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameContestsFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static Fragment newInstance(HtmlGamePojo model) {
        GameContestsFragment fragment = new GameContestsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Toolbox.MODEL, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            model = getArguments().getParcelable(Toolbox.MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding=FragmentGameContestsBinding.inflate(getLayoutInflater(), container, false);

       initData();

       return binding.getRoot();
    }

    private void initData() {
        setAdapter();
        getContestsList();
        if (adapter!=null) {
            adapter.setOnItemClickListener(new GameContestsAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(int position) {
                    startActivity(new Intent(context, HtmlGameRulesActivity.class)
                    .putExtra(Toolbox.GAME_MODEL,model)
                    .putExtra(Toolbox.MODEL,arrayList.get(position)));
                }
            });
        }
    }

    private void setAdapter() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter=new GameContestsAdapter(arrayList,context);
        binding.recyclerView.setAdapter(adapter);
    }

    private void getContestsList() {
        binding.shimmerView.setVisibility(View.VISIBLE);
        binding.shimmerView.startShimmer();

        Uri.Builder builder = Uri.parse(Constant.LIST_HTML_GAME_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("typeof_game", model.getId());

        StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), response -> {
            try {
                Log.e("Res", response);
                binding.shimmerView.stopShimmer();
                binding.shimmerView.setVisibility(View.GONE);

                arrayList.clear();
                HtmlGameContestsPojo model = new Gson().fromJson(response, HtmlGameContestsPojo.class);
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
            }
        }, error -> {
            error.printStackTrace();
            Log.e("Error", Objects.requireNonNull(error.getMessage()));
            binding.shimmerView.stopShimmer();
            binding.shimmerView.setVisibility(View.GONE);
            binding.noDataLayout.setVisibility(View.VISIBLE);
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