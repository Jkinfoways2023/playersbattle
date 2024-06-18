package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tournaments.grindbattles.activity.RoomAuthActivity;
import com.tournaments.grindbattles.activity.SlotSelectionActivity;
import com.tournaments.grindbattles.session.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import com.tournaments.grindbattles.bottomsheet.BottomSheetPricePool;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.JoiningMatchActivity;
import com.tournaments.grindbattles.activity.PlayDetailsActivity;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.model.PlayPojo;
import com.tournaments.grindbattles.utils.ExtraOperations;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.ViewHolder> {

    private Context context;
    private List<PlayPojo> playPojoList;
    private String left_spots,matchStartTime,currentTime;
    private boolean rotate = false;
    private String id, password;
    private SessionManager session;

    public PlayAdapter(List<PlayPojo> playPojoList, Context context){
        super();
        this.playPojoList = playPojoList;
        this.context = context;

        try {
            session = new SessionManager(context);
            HashMap<String, String> user = session.getUserDetails();
            id = user.get(SessionManager.KEY_ID);
            password = user.get(SessionManager.KEY_PASSWORD);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public PlayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_play, parent, false);
        PlayAdapter.ViewHolder viewHolder = new PlayAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PlayAdapter.ViewHolder holder, int position) {
        final PlayPojo playPojo =  playPojoList.get(position);

        holder.title.setText(playPojo.getTitle());
        TextView textView = holder.timedate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ");
        stringBuilder.append(playPojo.getTime());
        textView.setText(stringBuilder.toString());

        if (playPojo.getPool_type().equals("1") && playPojo.getEntry_type().equals("Paid")) {
            int share = 100-playPojo.getAdmin_share();
            int pricepool = ((playPojo.getEntry_fee()*playPojo.getTotal_joined())*share)/100;
            if (pricepool > playPojo.getPrize_pool()){
                holder.prize.setText(String.valueOf(pricepool));
            }
            else {
                holder.prize.setText(String.valueOf(playPojo.getPrize_pool()));
            }
        }
        else
        {
            holder.prize.setText(String.valueOf(playPojo.getPrize_pool()));
        }

        holder.prizePoolLL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new BottomSheetPricePool(playPojo.getTitle(), String.valueOf(playPojo.getMatch_desc())).show(((FragmentActivity)context).getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        holder.perkill.setText(String.valueOf(playPojo.getPer_kill()));
        String entryType = playPojo.getEntry_type();
        if (entryType.equals("Free")) {
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
            holder.sponsorTextArea.setVisibility(View.GONE);
        } else if (entryType.equals("Sponsored")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Sponsored by ");
            stringBuilder.append(playPojo.getSponsored_by());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (entryType.equals("Giveaway")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Giveaway by ");
            stringBuilder.append(playPojo.getSponsored_by());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.fee.setText(String.valueOf(playPojo.getEntry_fee()));
            holder.sponsorTextArea.setVisibility(View.GONE);
        }

        if (playPojo.getIs_private().equals("yes")) {
            holder.privateTextArea.setVisibility(View.VISIBLE);
        } else {
            holder.privateTextArea.setVisibility(View.GONE);
        }


        holder.type.setText(playPojo.getMatch_type());
        holder.version.setText(playPojo.getVersion());
        holder.map.setText(playPojo.getMap());

        if (!playPojo.getImage().equals("null")) {
            holder.topImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+playPojo.getImage()).resize(720,480).into(holder.topImage);
        }
        else {
            holder.topImage.setVisibility(View.GONE);
        }

        holder.materialProgressBar.setMax(playPojo.getRoom_size());
        holder.materialProgressBar.setProgress(playPojo.getTotal_joined());

        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(playPojo.getTotal_joined());
        stringBuilder1.append("/");
        stringBuilder1.append(playPojo.getRoom_size());
        holder.size.setText(stringBuilder1.toString());

        if (playPojo.getTotal_joined() >= playPojo.getRoom_size()) {
            holder.spot.setTextColor(Color.parseColor("#ff0000"));
            holder.spot.setText("No Spots Left! Match is Full.");
            holder.joinBtn.setText("MATCH FULL");
            holder.joinBtn.setTextColor(Color.parseColor("#ffffff"));
            holder.joinBtn.setBackgroundResource(R.drawable.buttonbackactive);
            holder.joinBtn.setEnabled(false);
            holder.joinBtn.setClickable(false);
        } else {
            left_spots = String.valueOf(playPojo.getRoom_size() - playPojo.getTotal_joined());
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Only ");
            stringBuilder2.append(left_spots);
            stringBuilder2.append(" spots left");
            holder.spot.setText(stringBuilder2.toString());
            holder.joinBtn.setEnabled(true);
            holder.joinBtn.setClickable(true);
        }

        try {
            if (playPojo.getIs_cancel().equals("1")){
                holder.spot.setTextColor(Color.parseColor("#ff0000"));
                holder.spot.setText("Can't Join Match! Match is Canceled.");

                holder.joinBtn.setText("CANCELED");
                holder.joinBtn.setTextColor(Color.parseColor("#ffffff"));
                holder.joinBtn.setBackgroundResource(R.drawable.buttonbackactive);
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else if (playPojo.getUser_joined().equals("0") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (!playPojo.getUser_joined().equals("1") && playPojo.getMatch_type().equals("Solo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("+ Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (!playPojo.getUser_joined().equals("2") && playPojo.getMatch_type().equals("Duo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("+ Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (!playPojo.getUser_joined().equals("4") && playPojo.getMatch_type().equals("Squad") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("+ Join");
                holder.joinBtn.setEnabled(true);
                holder.joinBtn.setClickable(true);
            } else if (playPojo.getUser_joined().equals("1") && playPojo.getMatch_type().equals("Solo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Joined");
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else if (playPojo.getUser_joined().equals("2") && playPojo.getMatch_type().equals("Duo") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Joined");
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else if (playPojo.getUser_joined().equals("4") && playPojo.getMatch_type().equals("Squad") && playPojo.getTotal_joined() < playPojo.getRoom_size()) {
                holder.joinBtn.setText("Joined");
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            } else {
                holder.spot.setTextColor(Color.parseColor("#ff0000"));
                holder.spot.setText("No Spots Left! Match is Full.");

                holder.joinBtn.setText("MATCH FULL");
                holder.joinBtn.setTextColor(Color.parseColor("#ffffff"));
                holder.joinBtn.setBackgroundResource(R.drawable.buttonbackactive);
                holder.joinBtn.setEnabled(false);
                holder.joinBtn.setClickable(false);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (playPojo.getUser_joined().equals("0")) {
                    if (new ExtraOperations().haveNetworkConnection(context)) {
                        Intent intent;
                 /*       if(playPojo.getGame_type().equalsIgnoreCase("1"))
                        {
                            intent = new Intent(context, PlayDetailsActivity.class);
                        }
                        else{

                        }*/
                        intent = new Intent(context, PlayDetailsActivity.class);
                        intent.putExtra("EntryFee_KEY", playPojo.getEntry_fee());
                        intent.putExtra("ID_KEY", playPojo.getId());
                        intent.putExtra("Map_KEY", playPojo.getMap());
                        intent.putExtra("Rules_KEY", playPojo.getRules());
                        intent.putExtra("PerKill_KEY", playPojo.getPer_kill());
                        intent.putExtra("StartTime_KEY", playPojo.getTime());
                        intent.putExtra("Match_Status_KEY", playPojo.getMatch_status());
                        intent.putExtra("Title_KEY", playPojo.getTitle());
                        intent.putExtra("TopImage_KEY", playPojo.getImage());
                        intent.putExtra("Entry_Type_KEY", playPojo.getEntry_type());
                        intent.putExtra("Match_Type_KEY", playPojo.getMatch_type());
                        intent.putExtra("Version_KEY", playPojo.getVersion());
                        intent.putExtra("WinPrize_KEY", playPojo.getPrize_pool());
                        intent.putExtra("PrizePool_KEY", playPojo.getMatch_desc());
                        intent.putExtra("Private_Status_KEY", playPojo.getIs_private());
                        intent.putExtra("MATCH__KEY", playPojo.getMatch_id());
                        intent.putExtra("ROOM_ID_KEY", playPojo.getRoom_id());
                        intent.putExtra("ROOM_PASS_KEY", playPojo.getRoom_pass());
                        intent.putExtra("ROOM_SIZE_KEY", playPojo.getRoom_size());
                        intent.putExtra("TOTAL_JOINED_KEY", playPojo.getTotal_joined());
                        intent.putExtra("JOINED_STATUS_KEY", playPojo.getJoined_status());
                        intent.putExtra("USER_JOINED_KEY", playPojo.getUser_joined());
                        intent.putExtra("IS_CANCELED_KEY", playPojo.getIs_cancel());
                        intent.putExtra("CANCELED_DESC_KEY", playPojo.getCancel_reason());
                        intent.putExtra("SECRET_CODE_KEY", playPojo.getAccess_key());
                        intent.putExtra("PLATFORM_KEY", playPojo.getPlatform());
                        intent.putExtra("POOL_TYPE_KEY", playPojo.getPool_type());
                        intent.putExtra("ADMIN_SHARE_KEY", playPojo.getAdmin_share());
                        intent.putExtra("SLOT_KEY", playPojo.getSlot());
                        intent.putExtra("GAME_TYPE", playPojo.getGame_type());
                        Log.e("gametypeisssss",playPojo.getGame_type());
                        context.startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Intent intent = new Intent(context, RoomAuthActivity.class);
                    intent.putExtra("EntryFee_KEY", playPojo.getEntry_fee());
                    intent.putExtra("ID_KEY", playPojo.getId());
                    intent.putExtra("Map_KEY", playPojo.getMap());
                    intent.putExtra("Rules_KEY", playPojo.getRules());
                    intent.putExtra("PerKill_KEY", playPojo.getPer_kill());
                    //intent.putExtra("CurrentTime_KEY", playPojo.getCurrent_time());
                    intent.putExtra("StartTime_KEY", playPojo.getTime());
                    intent.putExtra("Match_Status_KEY", playPojo.getMatch_status());
                    intent.putExtra("Title_KEY", playPojo.getTitle());
                    intent.putExtra("TopImage_KEY", playPojo.getImage());
                    intent.putExtra("Entry_Type_KEY", playPojo.getEntry_type());
                    intent.putExtra("Match_Type_KEY", playPojo.getMatch_type());
                    intent.putExtra("Version_KEY", playPojo.getVersion());
                    intent.putExtra("WinPrize_KEY", playPojo.getPrize_pool());
                    intent.putExtra("PrizePool_KEY", playPojo.getMatch_desc());
                    intent.putExtra("Private_Status_KEY", playPojo.getIs_private());
                    intent.putExtra("MATCH__KEY", playPojo.getMatch_id());
                    intent.putExtra("ROOM_ID_KEY", playPojo.getRoom_id());
                    intent.putExtra("ROOM_PASS_KEY", playPojo.getRoom_pass());
                    intent.putExtra("ROOM_SIZE_KEY", playPojo.getRoom_size());
                    intent.putExtra("TOTAL_JOINED_KEY", playPojo.getTotal_joined());
                    intent.putExtra("JOINED_STATUS_KEY", playPojo.getJoined_status());
                    intent.putExtra("USER_JOINED_KEY", playPojo.getUser_joined());
                    intent.putExtra("IS_CANCELED_KEY", playPojo.getIs_cancel());
                    intent.putExtra("CANCELED_DESC_KEY", playPojo.getCancel_reason());
                    intent.putExtra("SECRET_CODE_KEY", playPojo.getAccess_key());
                    intent.putExtra("BACK_TAG_KEY", "home");
                    //intent.putExtra("SpectateURL_KEY", playPojo.get);
                    intent.putExtra("PLATFORM_KEY", playPojo.getPlatform());
                    intent.putExtra("POOL_TYPE_KEY", playPojo.getPool_type());
                    intent.putExtra("ADMIN_SHARE_KEY", playPojo.getAdmin_share());
                    intent.putExtra("SLOT_KEY", playPojo.getSlot());
                    context.startActivity(intent);
                }

            }
        });

        holder.joinBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(context, JoiningMatchActivity.class);
                intent.putExtra("matchType", playPojo.getMatch_type());
                intent.putExtra("matchID", playPojo.getId());
                intent.putExtra("matchName", playPojo.getTitle());
                intent.putExtra("entryFee", playPojo.getEntry_fee());
                intent.putExtra("entryType", playPojo.getEntry_type());
                intent.putExtra("JoinStatus", playPojo.getJoined_status());
                intent.putExtra("isPrivate", playPojo.getIs_private());
                intent.putExtra("matchRules", playPojo.getRules());
                intent.putExtra("ROOM_SIZE_KEY", playPojo.getRoom_size());
                intent.putExtra("TOTAL_JOINED_KEY", playPojo.getTotal_joined());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return playPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fee;
        ImageView img;
        Button joinBtn;
        TextView map;
        ProgressBar materialProgressBar;
        TextView perkill;
        RelativeLayout privateTextArea;
        TextView prize;
        TextView size;
        TextView sponsorText;
        RelativeLayout sponsorTextArea;
        TextView spot;
        TextView timedate;
        TextView title;
        ImageView topImage;
        TextView type;
        TextView version;
        LinearLayout prizePoolLL;

        public ViewHolder(View itemView) {
            super(itemView);

            this.cardView = (CardView) itemView.findViewById(R.id.mainCard);
            this.img = (ImageView) itemView.findViewById(R.id.img);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.timedate = (TextView) itemView.findViewById(R.id.timedate);
            this.prize = (TextView) itemView.findViewById(R.id.winPrize);
            this.prizePoolLL = (LinearLayout) itemView.findViewById(R.id.prizePoolLL);
            this.perkill = (TextView) itemView.findViewById(R.id.perKill);
            this.fee = (TextView) itemView.findViewById(R.id.entryFee);
            this.spot = (TextView) itemView.findViewById(R.id.spots);
            this.size = (TextView) itemView.findViewById(R.id.size);
            this.materialProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            this.joinBtn = (Button) itemView.findViewById(R.id.joinButton);
            this.sponsorTextArea = (RelativeLayout) itemView.findViewById(R.id.sponsorTextArea);
            this.sponsorText = (TextView) itemView.findViewById(R.id.sponsorText);
            this.type = (TextView) itemView.findViewById(R.id.matchType);
            this.version = (TextView) itemView.findViewById(R.id.matchVersion);
            this.map = (TextView) itemView.findViewById(R.id.matchMap);
            this.topImage = (ImageView) itemView.findViewById(R.id.mainTopBanner);
            this.privateTextArea = (RelativeLayout) itemView.findViewById(R.id.privateTextArea);

        }

    }

}
