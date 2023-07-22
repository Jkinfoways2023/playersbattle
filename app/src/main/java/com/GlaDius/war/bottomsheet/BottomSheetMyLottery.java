package com.GlaDius.war.bottomsheet;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.GlaDius.war.MyApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.GlaDius.war.R;
import com.GlaDius.war.adapter.MyLotteryAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.GlaDius.war.model.LotteryResultsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetMyLottery extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private NestedScrollView scroll;

    private TextView noEntriesText;
    private TextView matchID;
    private ImageView closeBtn;

    private ArrayList<LotteryResultsPojo> lotteryResultsPojoList;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;

    private String lotteryID;
    private String userId;
    private String totalJoined;


    public BottomSheetMyLottery() {
    }

    public BottomSheetMyLottery(String str, String str2, String str3) {
        this.lotteryID = str;
        this.userId = str2;
        this.totalJoined = str3;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottom_sheet_my_lottery, viewGroup, false);

        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        //this.cancelButton = (TextView) inflate.findViewById(R.id.cancelButton);
        this.closeBtn = (ImageView) inflate.findViewById(R.id.closeBtn);
        this.noEntriesText = (TextView) inflate.findViewById(R.id.noEntriesText);
        this.matchID = (TextView) inflate.findViewById(R.id.myTotalBidsText);
        this.scroll = (NestedScrollView) inflate.findViewById(R.id.scroll);

        this.closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BottomSheetMyLottery.this.dismiss();
            }
        });


        lotteryResultsPojoList = new ArrayList();
        TextView textView = this.matchID;
        StringBuilder sb = new StringBuilder();
        sb.append("Total Members: ");
        sb.append(this.totalJoined);
        textView.setText(sb.toString());

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new MyEntriesList().execute();

        return inflate;
    }


    private class MyEntriesList {

        public MyEntriesList() {
        }

        public void execute() {
            noEntriesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            //cancelButton.setVisibility(View.GONE);

            Uri.Builder builder = Uri.parse(Constant.LOTTERY_MY_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("lottery_id", lotteryID);
            builder.appendQueryParameter("user_id", userId);
            jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            lotteryResultsPojoList.clear();
                            for(int i = 0; i<response.length(); i++) {
                                LotteryResultsPojo lotteryResultsPojo = new LotteryResultsPojo();
                                JSONObject json = null;
                                try {
                                    json = response.getJSONObject(i);
                                    lotteryResultsPojo.setId(json.getString("id"));
                                    lotteryResultsPojo.setName(json.getString("name"));
                                    lotteryResultsPojo.setLottery_no(json.getString("lottery_no"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                lotteryResultsPojoList.add(lotteryResultsPojo);
                            }
                            if (!lotteryResultsPojoList.isEmpty()){
                                adapter = new MyLotteryAdapter(lotteryResultsPojoList,getActivity());
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);

                                noEntriesText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                //cancelButton.setVisibility(View.VISIBLE);
                            }
                            else {
                                noEntriesText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                //cancelButton.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            requestQueue = Volley.newRequestQueue(getActivity());
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsonArrayRequest.setShouldCache(false);
            requestQueue.getCache().clear();
            requestQueue.add(jsonArrayRequest);
        }

    }

}