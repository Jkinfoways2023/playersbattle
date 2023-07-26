package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.LotteryResultsPojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LotteryParticipantAdapter extends RecyclerView.Adapter<LotteryParticipantAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<LotteryResultsPojo> lotteryResultsPojoList;

    public LotteryParticipantAdapter(List<LotteryResultsPojo> lotteryResultsPojoList, Context context){
        super();
        this.lotteryResultsPojoList = lotteryResultsPojoList;
        this.context = context;
    }

    @Override
    public LotteryParticipantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_participants, parent, false);
        LotteryParticipantAdapter.ViewHolder viewHolder = new LotteryParticipantAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LotteryParticipantAdapter.ViewHolder holder, int position) {
        final LotteryResultsPojo lotteryResultsPojo =  lotteryResultsPojoList.get(position);

        i = position+1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        stringBuilder.append(". ");
        stringBuilder.append(lotteryResultsPojo.getName());
        holder.nameIv.setText(stringBuilder.toString());
    }


    @Override
    public int getItemCount() {
        return lotteryResultsPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameIv;

        public ViewHolder(View itemView) {
            super(itemView);

            this.nameIv = (TextView) itemView.findViewById(R.id.nameIv);
        }

    }
}
