package com.tournaments.grindbattles.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.databinding.ActivitySlotSelectionBinding;
import com.tournaments.grindbattles.model.SlotListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import retrofit2.http.GET;

public class SlotSelectionActivity extends AppCompatActivity {

    ActivitySlotSelectionBinding binding;

    private int entryFee;
    private int roomSize;
    private int totalJoined;
    private String privateStatus;
    private String type = "";
    private String joinStatus;
    private String matchID;
    private String matchRules;
    private String matchType;
    private String matchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySlotSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getintentdata();
        getslotdetails();

        binding.backimg.setOnClickListener(v->{
            finish();
        });
    }

    private void getintentdata()
    {
        this.matchID = getIntent().getStringExtra("matchID");
        this.matchName = getIntent().getStringExtra("matchName");
        this.matchType = getIntent().getStringExtra("matchType");
        this.entryFee = getIntent().getIntExtra("entryFee",0);
        this.type = getIntent().getStringExtra("entryType");
        this.joinStatus = getIntent().getStringExtra("JoinStatus");
        this.privateStatus = getIntent().getStringExtra("isPrivate");
        this.matchRules = getIntent().getStringExtra("matchRules");
        this.roomSize = getIntent().getIntExtra("ROOM_SIZE_KEY",100);
        this.totalJoined = getIntent().getIntExtra("TOTAL_JOINED_KEY",0);
    }

    private void getslotdetails()
    {
        Uri.Builder builder= Uri.parse(Constant.get_match_slot_list).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("match_id", matchID);
        Log.e("urlisssssss",builder.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest( Request.Method.GET,builder.toString(),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json_response) {
                        SlotListModel model = new Gson().fromJson(json_response.toString(), SlotListModel.class);
                        Log.e("sizeisssss", String.valueOf(model.data.size()));
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_Response", String.valueOf(error));
            }
        });


        // Adding request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(SlotSelectionActivity.this);
        requestQueue.add(jsonObjReq);
    }

}