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
import com.tournaments.grindbattles.model.RewardPojo;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<RewardPojo> rewardPojoList;

    public RewardAdapter(List<RewardPojo> rewardPojoList, Context context){
        super();
        this.rewardPojoList = rewardPojoList;
        this.context = context;
    }

    @Override
    public RewardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_myrewards, parent, false);
        RewardAdapter.ViewHolder viewHolder = new RewardAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RewardAdapter.ViewHolder holder, int position) {
        final RewardPojo rewardPojo =  rewardPojoList.get(position);

        holder.dateTv.setText(rewardPojo.getReward_date());
        holder.dateTv.setTypeface(null, Typeface.BOLD);

        holder.rewardTv.setText(rewardPojo.getReward_count());
        holder.rewardTv.setTypeface(null, Typeface.BOLD);

        holder.earningTv.setText(rewardPojo.getReward_points());
        holder.earningTv.setTypeface(null, Typeface.BOLD);
    }


    @Override
    public int getItemCount() {
        return rewardPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView dateTv;
        TextView rewardTv;
        TextView earningTv;

        public ViewHolder(View itemView) {
            super(itemView);

            this.dateTv = (TextView) itemView.findViewById(R.id.dateTv);
            this.rewardTv = (TextView) itemView.findViewById(R.id.rewardTv);
            this.earningTv = (TextView) itemView.findViewById(R.id.earningTv);
        }

    }
}
