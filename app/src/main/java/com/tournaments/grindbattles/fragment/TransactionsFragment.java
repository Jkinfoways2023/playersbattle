package com.tournaments.grindbattles.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.adapter.TransactionAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.TransactionPojo;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ExtraOperations;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends androidx.fragment.app.Fragment {

    private View view;
    private ShimmerFrameLayout mShimmerViewContainer;
    private boolean isLoaded =false,isVisibleToUser;

    private LinearLayout noTxnsLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<TransactionPojo> transactionPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private SessionManager session;
    private String id,name,firstname,lastname,email,mnumber,username,password;
    private String isFrom;
    private RelativeLayout toolbar;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            isFrom = getArguments().getString("isFrom");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_transactions, container, false);

        initView();
        initPreference();

        if (isFrom!=null && isFrom.equals("MainActivity")){
            toolbar.setVisibility(View.GONE);
        }

        this.transactionPojoList = new ArrayList();
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if((!isLoaded)){
            if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                loadMyTransactions();
            }else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            isLoaded=true;
        }
    }

    private void loadMyTransactions() {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();
        recyclerView.setVisibility(View.GONE);
        noTxnsLayout.setVisibility(View.GONE);
        Uri.Builder builder = Uri.parse(Constant.MY_TRANSACTIONS_URL).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("user_id",id);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_TRANSACTIONS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noTxnsLayout.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_MY_TRANSACTIONS(JSONArray response) {
        transactionPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            TransactionPojo transactionPojo = new TransactionPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                transactionPojo.setId(json.getString("id"));
                transactionPojo.setUser_id(json.getString("user_id"));
                transactionPojo.setPayment_id(json.getString("payment_id"));
                transactionPojo.setReq_amount(json.getString("req_amount"));
                transactionPojo.setRemark(json.getString("remark"));
                transactionPojo.setType(json.getString("type"));
                transactionPojo.setDate(json.getString("date"));
                transactionPojo.setGetway_name(json.getString("getway_name"));
                transactionPojo.setCoins_used(json.getString("coins_used"));
                transactionPojo.setStatus(json.getString("status"));
                transactionPojo.setRequest_name(json.getString("request_name"));
                transactionPojo.setReq_from(json.getString("req_from"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            transactionPojoList.add(transactionPojo);
        }
        if (!transactionPojoList.isEmpty()){
            adapter = new TransactionAdapter(transactionPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noTxnsLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noTxnsLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initPreference() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);
    }

    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_container);
        this.noTxnsLayout = (LinearLayout) view.findViewById(R.id.noTxnLayout);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.txnListRecyclerView);
        this.toolbar = (RelativeLayout) view.findViewById(R.id.toolbar);
    }

    public void onResume() {
        initPreference();
        super.onResume();
    }

    public void onPause() {
        this.mShimmerViewContainer.stopShimmer();
        super.onPause();
    }
}
