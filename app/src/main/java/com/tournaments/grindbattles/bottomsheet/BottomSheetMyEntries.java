package com.tournaments.grindbattles.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tournaments.grindbattles.MyApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tournaments.grindbattles.utils.ExtraOperations;
import com.tournaments.grindbattles.utils.MySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Constant;
import com.tournaments.grindbattles.model.MyEntriesPojo;
import com.google.android.material.textfield.TextInputEditText;

@SuppressLint({"ValidFragment"})
public class BottomSheetMyEntries extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private NestedScrollView scroll;

    private TextView noEntriesText;
    private TextView matchID;
    private ImageView closeBtn;

    private ArrayList<MyEntriesPojo> myEntriesPojoList;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private String matchTitle;
    private String userName;
    private String password;
    private String roomID;
    private String isCanceled;
    private String currentTime,matchStartTime;


    public BottomSheetMyEntries() {
    }

    public BottomSheetMyEntries(String str, String str2, String str3, String str4, String str5) {
        this.matchTitle = str;
        this.userName = str2;
        this.password = str3;
        this.roomID = str4;
        this.isCanceled = str5;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottomsheet_my_entries, viewGroup, false);

        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.closeBtn = (ImageView) inflate.findViewById(R.id.closeBtn);
        this.noEntriesText = (TextView) inflate.findViewById(R.id.noEntriesText);
        this.matchID = (TextView) inflate.findViewById(R.id.matchID);
        this.scroll = (NestedScrollView) inflate.findViewById(R.id.scroll);

        this.closeBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BottomSheetMyEntries.this.dismiss();
            }
        });


        myEntriesPojoList = new ArrayList();
        TextView textView = this.matchID;
        StringBuilder sb = new StringBuilder();
        sb.append("Match #");
        sb.append(this.matchTitle);
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

            Uri.Builder builder = Uri.parse(Constant.MY_ENTRIES_URL).buildUpon();
            builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
            builder.appendQueryParameter("match_id", matchTitle);
            builder.appendQueryParameter("user_id", userName);
            jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            myEntriesPojoList.clear();
                            for(int i = 0; i<response.length(); i++) {
                                MyEntriesPojo myEntriesPojo = new MyEntriesPojo();
                                JSONObject json = null;
                                try {
                                    json = response.getJSONObject(i);
                                    myEntriesPojo.setId(json.getString("id"));
                                    myEntriesPojo.setUser_id(json.getString("user_id"));
                                    myEntriesPojo.setMatch_id(json.getString("match_id"));
                                    myEntriesPojo.setPubg_id(json.getString("pubg_id"));
                                    //myEntriesPojo.setSlot(json.getString("slot"));
                                    //myEntriesPojo.setIs_canceled(json.getString("is_canceled"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                myEntriesPojoList.add(myEntriesPojo);
                            }
                            if (!myEntriesPojoList.isEmpty()){
                                adapter = new MyEntriesAdapter(myEntriesPojoList,getActivity());
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
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsonArrayRequest.setShouldCache(false);
            requestQueue.getCache().clear();
            requestQueue.add(jsonArrayRequest);
        }

    }

    public class MyEntriesAdapter extends RecyclerView.Adapter<MyEntriesAdapter.ViewHolder> {

        private Context context;
        private List<MyEntriesPojo> myEntriesPojoList;
        private String encodeGameUserID1;
        private String encodeCharacterID;

        public MyEntriesAdapter(List<MyEntriesPojo> myEntriesPojoList, Context context){
            super();
            this.myEntriesPojoList = myEntriesPojoList;
            this.context = context;
        }

        @Override
        public MyEntriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_entries, parent, false);
            MyEntriesAdapter.ViewHolder viewHolder = new MyEntriesAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyEntriesAdapter.ViewHolder holder, int position) {
            final MyEntriesPojo myEntriesPojo =  myEntriesPojoList.get(position);

            TextView textView = holder.ingamename;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("• ");
            stringBuilder.append(myEntriesPojo.getPubg_id());
            textView.setText(stringBuilder.toString());

            if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                Uri.Builder builder = Uri.parse(Constant.TIMER_MATCH_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                builder.appendQueryParameter("match_id", matchTitle);
                StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");

                            if (success.equals("1")) {
                                currentTime = jsonObject1.getString("msg");
                                matchStartTime = jsonObject1.getString("time");

                                try {
                                    //currentTime = "1578499200";
                                    //matchStartTime = "1578655554";
                                    int diff = Integer.parseInt(matchStartTime) - Integer.parseInt(currentTime);
                                    if (diff <= 3600){
                                        holder.cancelButton.setVisibility(View.GONE);
                                        holder.editButton.setVisibility(View.GONE);
                                        //Toast.makeText(context, "You can't cancel entry now. Match is start very shortly.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        holder.cancelButton.setVisibility(View.GONE);
                                        holder.editButton.setVisibility(View.VISIBLE);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            else {
                                Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setShouldCache(false);
                MySingleton.getInstance(getActivity()).addToRequestque(request);
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }

            if (!(roomID.equals("null") || roomID.isEmpty()) || !isCanceled.equals("0")) {
                holder.editButton.setVisibility(View.GONE);
                holder.cancelButton.setVisibility(View.GONE);
            }


            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetMyEntries.this.dismiss();
                    openDialog(myEntriesPojo.getId(),myEntriesPojo.getUser_id(),myEntriesPojo.getPubg_id(),myEntriesPojo.getMatch_id());
                }
            });

            holder.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetMyEntries.this.dismiss();
                    openCancelDialog(myEntriesPojo.getId(), myEntriesPojo.getUser_id(), myEntriesPojo.getPubg_id(), myEntriesPojo.getMatch_id());
                }
            });
        }


        private void openCancelDialog(String id, String user_id, String pubg_id, String match_id) {
            if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                Uri.Builder builder = Uri.parse(Constant.CANCEL_MY_ENTRIES_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                builder.appendQueryParameter("id", id);
                builder.appendQueryParameter("match_id", match_id);
                builder.appendQueryParameter("user_id", user_id);
                builder.appendQueryParameter("pubg_id", pubg_id);
                StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");

                            if (success.equals("1")) {
                                //displayInterstitial();
                                Toast.makeText(context,"This entry successfully cancelled and fee of this match refunded to your wallet.", Toast.LENGTH_LONG).show();
                                //displayUnityReward();
                            }
                            else {
                                BottomSheetMyEntries.this.dismiss();
                                Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getActivity()).addToRequestque(request);
            } else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }

        private void openDialog(final String id, final String user_id, String pubg_id, final String match_id) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_user);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final TextInputEditText gameID = (TextInputEditText) dialog.findViewById(R.id.gameID);
            gameID.setText(pubg_id);

            Button button = (Button) dialog.findViewById(R.id.next);
            Button button2 = (Button) dialog.findViewById(R.id.cancel);

            final TextView textError = (TextView) dialog.findViewById(R.id.textError);

            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    encodeGameUserID1 = gameID.getText().toString().trim();
                    if (!encodeGameUserID1.isEmpty()) {
                        EditUsername(id,user_id,match_id,encodeGameUserID1);
                        dialog.dismiss();
                    }
                    else {
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Invalid Game Username. Retry.");
                    }
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void EditUsername( String id, String user_id, String match_id,String encodeGameUserID1) {
            if (new ExtraOperations().haveNetworkConnection(context)) {
                Uri.Builder builder = Uri.parse(Constant.UPDATE_MY_ENTRIES_URL).buildUpon();
                builder.appendQueryParameter("access_key", MyApplication.getInstance().testsignin());
                builder.appendQueryParameter("id", id);
                builder.appendQueryParameter("match_id", match_id);
                builder.appendQueryParameter("user_id", user_id);
                builder.appendQueryParameter("pubg_id", encodeGameUserID1);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("0")) {
                                Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
                            } else if (success.equals("1")) {
                                Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(context).addToRequestque(request);
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public int getItemCount() {
            return myEntriesPojoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView ingamename;
            public ImageView editButton,cancelButton;

            public ViewHolder(View itemView) {
                super(itemView);
                this.ingamename = (TextView) itemView.findViewById(R.id.ingamename);
                this.editButton = (ImageView) itemView.findViewById(R.id.editButton);
                this.cancelButton = (ImageView) itemView.findViewById(R.id.cancelButton);

                editButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
            }

        }
    }
}
