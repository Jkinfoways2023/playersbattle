package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.LeaderboardRewardPojo;

public class LeaderboardRewardAdapter extends RecyclerView.Adapter<LeaderboardRewardAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<LeaderboardRewardPojo> leaderboardPojoList;

    public LeaderboardRewardAdapter(List<LeaderboardRewardPojo> leaderboardPojoList, Context context){
        super();
        this.leaderboardPojoList = leaderboardPojoList;
        this.context = context;
    }

    @Override
    public LeaderboardRewardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reward_leaderboard, parent, false);
        LeaderboardRewardAdapter.ViewHolder viewHolder = new LeaderboardRewardAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LeaderboardRewardAdapter.ViewHolder holder, int position) {
        final LeaderboardRewardPojo leaderboardPojo =  leaderboardPojoList.get(position);

        i = position+1;
        String stringBuilder = "" + i;
        holder.sNo.setText(stringBuilder);

        holder.sNo.setTypeface(null, Typeface.BOLD);

        holder.name.setText((leaderboardPojo.getFname()+" "+leaderboardPojo.getLname()));
        holder.name.setTypeface(null, Typeface.BOLD);

        String stringBuilder2 = leaderboardPojo.getReward_points();
        holder.winning.setText(stringBuilder2);
        holder.winning.setTypeface(null, Typeface.BOLD);
    }


    @Override
    public int getItemCount() {
        return leaderboardPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView sNo;
        TextView winning;

        public ViewHolder(View itemView) {
            super(itemView);

            this.sNo = (TextView) itemView.findViewById(R.id.playerPosition);
            this.name = (TextView) itemView.findViewById(R.id.playerName);
            this.winning = (TextView) itemView.findViewById(R.id.playerWinning);
        }

    }
}
