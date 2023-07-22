package com.GlaDius.war.bottomsheet;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.GlaDius.war.MyApplication;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.GlaDius.war.R;
import com.GlaDius.war.adapter.ViewEntriesAdapter;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.common.Constant;
import com.GlaDius.war.model.ParticipantPojo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetViewEntries extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private NestedScrollView scroll;

    private TextView noEntriesText;
    private TextView matchID;
    private ImageView closeBtn;

    private ArrayList<ParticipantPojo> participantPojoList;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private String matchTitle;
    private String userName;
    private String password;


    public BottomSheetViewEntries() {
    }

    public BottomSheetViewEntries(String str, String str2, String str3) {
        this.matchTitle = str;
        this.userName = str2;
        this.password = str3;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottomsheet_view_entries, viewGroup, false);

        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.closeBtn = (ImageView) inflate.findViewById(R.id.closeBtn);
        this.noEntriesText = (TextView) inflate.findViewById(R.id.noEntriesText);
        this.matchID = (TextView) inflate.findViewById(R.id.matchID);
        this.scroll = (NestedScrollView) inflate.findViewById(R.id.scroll);

        this.closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BottomSheetViewEntries.this.dismiss();
            }
        });


        participantPojoList = new ArrayList();
        TextView textView = this.matchID;
        StringBuilder sb = new StringBuilder();
        sb.append("Match #");
        sb.append(this.matchTitle);
        textView.setText(sb.toString());

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new BottomSheetViewEntries.MyEntriesList().execute();

        return inflate;
    }

    private class MyEntriesList {

        public MyEntriesList() {
        }

        public void execute() {
            noEntriesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            Uri.Builder builder = Uri.parse(Constant.PARTICIPANTS_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("match_id", matchTitle);
            jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            participantPojoList.clear();
                            for(int i = 0; i<response.length(); i++) {
                                ParticipantPojo participantPojo = new ParticipantPojo();
                                JSONObject json = null;
                                try {
                                    json = response.getJSONObject(i);
                                    //participantPojo.setId(json.getString("id"));
                                    //participantPojo.setUser_id(json.getString("user_id"));
                                    participantPojo.setPubg_id(json.getString("pubg_id"));
                                    participantPojo.setSlot(json.getInt("slot"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                participantPojoList.add(participantPojo);
                            }
                            if (!participantPojoList.isEmpty()){
                                adapter = new ViewEntriesAdapter(participantPojoList,getActivity());
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);

                                noEntriesText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                            }
                            else {
                                noEntriesText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
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
            jsonArrayRequest.setShouldCache(false);
            requestQueue.getCache().clear();
            requestQueue.add(jsonArrayRequest);
        }

    }

}
