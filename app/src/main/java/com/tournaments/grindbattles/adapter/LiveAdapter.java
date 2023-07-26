package com.tournaments.grindbattles.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.tournaments.grindbattles.bottomsheet.BottomSheetPricePool;
import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.LiveDetailsActivity;
import com.tournaments.grindbattles.model.LivePojo;
import com.tournaments.grindbattles.utils.ExtraOperations;

public class LiveAdapter extends RecyclerView.Adapter<LiveAdapter.ViewHolder> {

    private Context context;
    private List<LivePojo> livePojoList;

    public LiveAdapter(List<LivePojo> livePojoList, Context context){
        super();
        this.livePojoList = livePojoList;
        this.context = context;
    }

    @Override
    public LiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_live, parent, false);
        LiveAdapter.ViewHolder viewHolder = new LiveAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LiveAdapter.ViewHolder holder, int position) {
        final LivePojo livePojo =  livePojoList.get(position);

        holder.title.setText(livePojo.getTitle());
        TextView textView = holder.timedate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ");
        stringBuilder.append(livePojo.getTime());
        textView.setText(stringBuilder.toString());

        if (livePojo.getPool_type().equals("1") && livePojo.getEntry_type().equals("Paid")) {
            int share = 100-livePojo.getAdmin_share();
            int pricepool = ((livePojo.getEntry_fee()*livePojo.getTotal_joined())*share)/100;
            if (pricepool > livePojo.getPrize_pool()){
                holder.prize.setText(String.valueOf(pricepool));
            }
            else {
                holder.prize.setText(String.valueOf(livePojo.getPrize_pool()));
            }
        }
        else
        {
            holder.prize.setText(String.valueOf(livePojo.getPrize_pool()));
        }
        holder.perkill.setText(String.valueOf(livePojo.getPer_kill()));

        holder.prizePoolLL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new BottomSheetPricePool(livePojo.getTitle(), String.valueOf(livePojo.getMatch_desc())).show(((FragmentActivity)context).getSupportFragmentManager(), "exampleBottomSheet");
            }
        });

        String matchType = livePojo.getEntry_type();
        if (matchType.equals("Free")) {
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
            holder.sponsorTextArea.setVisibility(View.GONE);
        } else if (matchType.equals("Sponsored")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Sponsored by ");
            stringBuilder.append(livePojo.getSponsored_by());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (matchType.equals("Giveaway")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Giveaway by ");
            stringBuilder.append(livePojo.getSponsored_by());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.fee.setText(String.valueOf(livePojo.getEntry_fee()));
            holder.sponsorTextArea.setVisibility(View.GONE);
        }

        holder.type.setText(livePojo.getMatch_type());
        holder.version.setText(livePojo.getVersion());
        holder.map.setText(livePojo.getMap());

       /* if (!livePojo.getImage().equals("null")) {
            holder.topImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+livePojo.getImage().replace(" ", "%20")).resize(500,250).into(holder.topImage);
        }
        else {
            holder.topImage.setVisibility(View.GONE);
        }*/

        try{
            if (livePojo.getIs_cancel().equals("1")){
                holder.playbtnLL.setVisibility(View.VISIBLE);
                holder.playBtn.setText("CANCELED");
                holder.playBtn.setEnabled(false);
                holder.playBtn.setClickable(false);
            }
            else if (livePojo.getJoined_status().equals("null")) {
                holder.playbtnLL.setVisibility(View.GONE);
            }
            else {
                holder.playbtnLL.setVisibility(View.VISIBLE);
                holder.playBtn.setText("PLAY NOW");
                holder.playBtn.setEnabled(true);
                holder.playBtn.setClickable(true);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        
        holder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(livePojo.getUrl());
                if (launchIntentForPackage != null) {
                    context.startActivity(launchIntentForPackage);
                } else {
                    Toast.makeText(context, livePojo.getGame()+" is Not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        holder.spectateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (!livePojo.getSpectate_url().startsWith("http://") && !livePojo.getSpectate_url().startsWith("https://")){
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+livePojo.getSpectate_url())));
                    }
                    else {
                        context. startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(livePojo.getSpectate_url())));
                    }
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (new ExtraOperations().haveNetworkConnection(context)) {
                    Intent intent = new Intent(context, LiveDetailsActivity.class);
                    intent.putExtra("EntryFee_KEY", livePojo.getEntry_fee());
                    intent.putExtra("ID_KEY", livePojo.getId());
                    intent.putExtra("GAME_NAME_KEY", livePojo.getGame());
                    intent.putExtra("GAME_URL_KEY", livePojo.getUrl());
                    intent.putExtra("Map_KEY", livePojo.getMap());
                    intent.putExtra("Rules_KEY", livePojo.getRules());
                    intent.putExtra("PerKill_KEY", livePojo.getPer_kill());
                    intent.putExtra("StartTime_KEY", livePojo.getTime());
                    intent.putExtra("Match_Status_KEY", livePojo.getMatch_status());
                    intent.putExtra("Title_KEY", livePojo.getTitle());
                    //intent.putExtra("TopImage_KEY", livePojo.getImage());
                    intent.putExtra("Entry_Type_KEY", livePojo.getEntry_type());
                    intent.putExtra("Match_Type_KEY", livePojo.getMatch_type());
                    intent.putExtra("Version_KEY", livePojo.getVersion());
                    intent.putExtra("WinPrize_KEY", livePojo.getPrize_pool());
                    intent.putExtra("PrizePool_KEY", livePojo.getMatch_desc());
                    intent.putExtra("Private_Status_KEY", livePojo.getIs_private());
                    intent.putExtra("SpectateURL_KEY", livePojo.getSpectate_url());
                    intent.putExtra("MATCH__KEY", livePojo.getMatch_id());
                    intent.putExtra("ROOM_ID_KEY", livePojo.getRoom_id());
                    intent.putExtra("ROOM_PASS_KEY", livePojo.getRoom_pass());
                    intent.putExtra("ROOM_SIZE_KEY", livePojo.getRoom_size());
                    intent.putExtra("TOTAL_JOINED_KEY", livePojo.getTotal_joined());
                    intent.putExtra("JOINED_STATUS_KEY", livePojo.getJoined_status());
                    intent.putExtra("IS_CANCELED_KEY", livePojo.getIs_cancel());
                    intent.putExtra("CANCELED_DESC_KEY", livePojo.getCancel_reason());
                    intent.putExtra("PLATFORM_KEY", livePojo.getPlatform());
                    intent.putExtra("POOL_TYPE_KEY", livePojo.getPool_type());
                    intent.putExtra("ADMIN_SHARE_KEY", livePojo.getAdmin_share());
                    intent.putExtra("SLOT_KEY", livePojo.getSlot());
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return livePojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fee;
        ImageView img;
        TextView map;
        TextView perkill;
        Button playBtn;
        LinearLayout playbtnLL;
        TextView prize;
        TextView size;
        Button spectateBtn;
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
            this.playBtn = (Button) itemView.findViewById(R.id.playButton);
            this.spectateBtn = (Button) itemView.findViewById(R.id.spectateButton);
            this.sponsorTextArea = (RelativeLayout) itemView.findViewById(R.id.sponsorTextArea);
            this.sponsorText = (TextView) itemView.findViewById(R.id.sponsorText);
            this.type = (TextView) itemView.findViewById(R.id.matchType);
            this.version = (TextView) itemView.findViewById(R.id.matchVersion);
            this.map = (TextView) itemView.findViewById(R.id.matchMap);
            this.playbtnLL = (LinearLayout) itemView.findViewById(R.id.playButtonLL);
            this.topImage = (ImageView) itemView.findViewById(R.id.mainTopBanner);
        }

    }
}
