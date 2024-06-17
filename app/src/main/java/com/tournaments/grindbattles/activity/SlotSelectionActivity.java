package com.tournaments.grindbattles.activity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tournaments.grindbattles.MyApplication;
import com.tournaments.grindbattles.adapter.SlotListAdapter;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.databinding.ActivitySlotSelectionBinding;
import com.tournaments.grindbattles.model.SlotListModel;
import com.tournaments.grindbattles.model.SlotListOrganizedModel;
import com.tournaments.grindbattles.session.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    public  String user_id="";
    List<SlotListOrganizedModel.Data> slotmodel;
    public int selected_slot=0;
    private SessionManager session;
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
        session = new SessionManager(getApplicationContext());

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
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);
    }

    private void getslotdetails()
    {
        Uri.Builder builder= Uri.parse(Constant.get_match_slot_list).buildUpon();
        builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
        builder.appendQueryParameter("match_id", matchID);
        builder.appendQueryParameter("user_id", user_id);
        Log.e("urlisssssss",builder.toString());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest( Request.Method.GET,builder.toString(),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json_response) {
                        SlotListModel model = new Gson().fromJson(json_response.toString(), SlotListModel.class);
                        Log.e("sizeisssss", String.valueOf(model.data.size()));
                        organizeslots(model);

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

    private void organizeslots(SlotListModel model)
    {
        slotmodel=new ArrayList<SlotListOrganizedModel.Data>();
        ArrayList<Integer> slot_ids=new ArrayList<>();
        for (int a=0;a<model.data.size();a++)
        {


            int slot_position=-1;

            for(int b=0;b<slotmodel.size();b++)
            {
                if(slotmodel.get(b).slot==model.data.get(a).slot)
                {
                    slot_position=b;
                }
            }
            if(slot_position!=-1)
            {
                SlotListOrganizedModel.Position pos=new SlotListOrganizedModel.Position();
                pos.position = model.data.get(a).position;
                if(model.data.get(a).user_id==null)
                {
                    pos.selected=false;
                }
                else{
                    pos.selected=true;
                    if(model.data.get(a).user_id.equalsIgnoreCase(user_id))
                    {
                        selected_slot=selected_slot+1;
                    }
                }
                pos.user_id=model.data.get(a).user_id;
                pos.id=model.data.get(a).id;
                slotmodel.get(slot_position).position.add(pos);
            }
            else{
                SlotListOrganizedModel.Data datas=new SlotListOrganizedModel.Data();

                datas.slot=model.data.get(a).slot;
                SlotListOrganizedModel.Position pos=new SlotListOrganizedModel.Position();
                pos.position=model.data.get(a).position;
                if(model.data.get(a).user_id==null)
                {
                    pos.selected=false;
                }
                else{
                    pos.selected=true;
                    if(model.data.get(a).user_id.equalsIgnoreCase(user_id))
                    {
                        selected_slot=selected_slot+1;
                    }
                }
                pos.user_id=model.data.get(a).user_id;
                pos.id=model.data.get(a).id;
                datas.position.add(pos);
                slot_ids.add(model.data.get(a).id);
                slotmodel.add(datas);
        }
        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(SlotSelectionActivity.this));
        SlotListAdapter adapter=new SlotListAdapter(slotmodel,SlotSelectionActivity.this,selected_slot);
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(pos->{
            Toast.makeText(this, ""+pos, Toast.LENGTH_SHORT).show();
        });
        if(slotmodel.get(0).position.size()==1)
        {
            binding.titileB.setVisibility(View.GONE);
            binding.titileC.setVisibility(View.GONE);
            binding.titileD.setVisibility(View.GONE);
        }
        else if(slotmodel.get(0).position.size()==2)
        {
            binding.titileC.setVisibility(View.GONE);
            binding.titileD.setVisibility(View.GONE);
        }
    }

}