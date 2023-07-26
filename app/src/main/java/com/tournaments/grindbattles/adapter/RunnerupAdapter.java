package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.WinnerPojo;

public class RunnerupAdapter extends RecyclerView.Adapter<RunnerupAdapter.ViewHolder> {

    private String decodeName;
    private String encodedName;
    private Context context;
    private List<WinnerPojo> runnerupPojoList;

    public RunnerupAdapter(List<WinnerPojo> runnerupPojoList, Context context){
        super();
        this.runnerupPojoList = runnerupPojoList;
        this.context = context;
    }

    @Override
    public RunnerupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result_details, parent, false);
        RunnerupAdapter.ViewHolder viewHolder = new RunnerupAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RunnerupAdapter.ViewHolder holder, int position) {
        final WinnerPojo runnerupPojo =  runnerupPojoList.get(position);

        holder.position.setText(String.valueOf(runnerupPojo.getPosition()));
        this.encodedName = runnerupPojo.getPubg_id();
        try {
            this.decodeName = URLDecoder.decode(this.encodedName, "UTF8");
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        holder.pName.setText(this.decodeName);
        holder.pKills.setText(String.valueOf(runnerupPojo.getKills()));
        holder.pWinning.setText(String.valueOf(runnerupPojo.getPrize()));
    }


    @Override
    public int getItemCount() {
        return runnerupPojoList.size();
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
