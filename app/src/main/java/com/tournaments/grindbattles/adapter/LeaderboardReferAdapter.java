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
import com.tournaments.grindbattles.model.LeaderboardReferPojo;

public class LeaderboardReferAdapter extends RecyclerView.Adapter<LeaderboardReferAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<LeaderboardReferPojo> leaderboardPojoList;

    public LeaderboardReferAdapter(List<LeaderboardReferPojo> leaderboardPojoList, Context context){
        super();
        this.leaderboardPojoList = leaderboardPojoList;
        this.context = context;
    }

    @Override
    public LeaderboardReferAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_refer_leaderboard, parent, false);
        LeaderboardReferAdapter.ViewHolder viewHolder = new LeaderboardReferAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LeaderboardReferAdapter.ViewHolder holder, int position) {
        final LeaderboardReferPojo leaderboardPojo =  leaderboardPojoList.get(position);

        i = position+1;
        String stringBuilder = "" + i;
        holder.sNo.setText(stringBuilder);

        holder.sNo.setTypeface(null, Typeface.BOLD);

        holder.name.setText((leaderboardPojo.getFname()+" "+leaderboardPojo.getLname()));
        holder.name.setTypeface(null, Typeface.BOLD);

        String stringBuilder2 = leaderboardPojo.getRefer_points();
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
