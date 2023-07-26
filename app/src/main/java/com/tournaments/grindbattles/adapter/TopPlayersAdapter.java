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
import com.tournaments.grindbattles.model.TopPlayersPojo;

public class TopPlayersAdapter extends RecyclerView.Adapter<TopPlayersAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<TopPlayersPojo> topPlayersPojoList;

    public TopPlayersAdapter(List<TopPlayersPojo> topPlayersPojoList, Context context){
        super();
        this.topPlayersPojoList = topPlayersPojoList;
        this.context = context;
    }

    @Override
    public TopPlayersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_topplayers, parent, false);
        TopPlayersAdapter.ViewHolder viewHolder = new TopPlayersAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TopPlayersAdapter.ViewHolder holder, int position) {
        final TopPlayersPojo topPlayersPojo =  topPlayersPojoList.get(position);

        i = position+1;

        String stringBuilder = "" + i;
        holder.sNo.setText(stringBuilder);

        holder.sNo.setTypeface(null, Typeface.BOLD);

        holder.name.setText(topPlayersPojo.getPubg_id());
        holder.name.setTypeface(null, Typeface.BOLD);

        holder.winning.setText(String.valueOf(topPlayersPojo.getPrize()));
        holder.winning.setTypeface(null, Typeface.BOLD);
    }


    @Override
    public int getItemCount() {
        return topPlayersPojoList.size();
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
