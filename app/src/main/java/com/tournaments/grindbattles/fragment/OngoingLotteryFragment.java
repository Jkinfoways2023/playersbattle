package com.tournaments.grindbattles.fragment;


import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.LotteryDetailsAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.LotteryDetailsPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OngoingLotteryFragment extends Fragment {

    private View view;
    private boolean isLoaded =false,isVisibleToUser;

    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout noOngoing;

    String id;
    String username;
    String token;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<LotteryDetailsPojo> lotteryDetailsPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private NestedScrollView nestedScrollView;

    public OngoingLotteryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ongoing_lottery, container, false);
        initView();
        initSession();

        lotteryDetailsPojoList = new ArrayList<>();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        if(isVisibleToUser && isAdded() ){
            if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                loadLottery();
            }else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            isLoaded =true;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(isVisibleToUser && (!isLoaded)){
            if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                loadLottery();
            }else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            isLoaded=true;
        }
    }

    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.noOngoing = (LinearLayout) view.findViewById(R.id.noOnGoingLL);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
    }

    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
    }


    private void loadLottery() {
        recyclerView.setVisibility(View.GONE);
        noOngoing.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        Uri.Builder builder = Uri.parse(Constant.LOTTERY_DETAILS_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("user_id",id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noOngoing.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        lotteryDetailsPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            LotteryDetailsPojo lotteryDetailsPojo = new LotteryDetailsPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                lotteryDetailsPojo.setId(json.getString("id"));
                lotteryDetailsPojo.setTitle(json.getString("title"));
                lotteryDetailsPojo.setTime(json.getString("time"));
                lotteryDetailsPojo.setTimestamp(json.getString("timestamp"));
                lotteryDetailsPojo.setFee(json.getString("fee"));
                lotteryDetailsPojo.setPrize(json.getString("prize"));
                lotteryDetailsPojo.setTotal_joined(json.getInt("total_joined"));
                lotteryDetailsPojo.setSize(json.getInt("size"));
                lotteryDetailsPojo.setMy_number(json.getString("my_number"));
                lotteryDetailsPojo.setRules(json.getString("rules"));
                lotteryDetailsPojo.setImage(json.getString("image"));
                lotteryDetailsPojo.setCurrenttime(json.getString("currenttime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            lotteryDetailsPojoList.add(lotteryDetailsPojo);
        }
        if (!lotteryDetailsPojoList.isEmpty()){
            adapter = new LotteryDetailsAdapter(lotteryDetailsPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noOngoing.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noOngoing.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        super.onResume();
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            mShimmerViewContainer.startShimmer();
        }
    }

    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

}
