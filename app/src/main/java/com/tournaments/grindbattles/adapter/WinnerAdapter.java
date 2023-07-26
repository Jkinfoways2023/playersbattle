package com.tournaments.grindbattles.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.WinnerPojo;

public class WinnerAdapter extends RecyclerView.Adapter<WinnerAdapter.ViewHolder> {

    private String decodeName;
    private String encodedName;
    private Context context;
    private List<WinnerPojo> winnerPojoList;

    public WinnerAdapter(List<WinnerPojo> winnerPojoList, Context context){
        super();
        this.winnerPojoList = winnerPojoList;
        this.context = context;
    }

    @Override
    public WinnerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result_details, parent, false);
        WinnerAdapter.ViewHolder viewHolder = new WinnerAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final WinnerAdapter.ViewHolder holder, int position) {
        final WinnerPojo WinnerPojo =  winnerPojoList.get(position);

        holder.position.setText(String.valueOf(WinnerPojo.getPosition()));
        this.encodedName = WinnerPojo.getPubg_id();
        try {
            this.decodeName = URLDecoder.decode(this.encodedName, "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.pName.setText(this.decodeName);
        holder.pKills.setText(String.valueOf(WinnerPojo.getKills()));
        holder.pWinning.setText(String.valueOf(WinnerPojo.getPrize()));
    }


    @Override
    public int getItemCount() {
        return winnerPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView pKills;
        TextView pName;
        TextView pWinning;
        TextView position;

        public ViewHolder(View itemView) {
            super(itemView);

            this.position = (TextView) itemView.findViewById(R.id.srNo);
            this.pName = (TextView) itemView.findViewById(R.id.playerName);
            this.pKills = (TextView) itemView.findViewById(R.id.totalKills);
            this.pWinning = (TextView) itemView.findViewById(R.id.totalAmountWon);
        }

    }
}
