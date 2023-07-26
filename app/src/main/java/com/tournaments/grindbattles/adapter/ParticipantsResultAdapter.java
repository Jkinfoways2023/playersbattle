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
import com.tournaments.grindbattles.model.ParticipantPojo;

public class ParticipantsResultAdapter extends RecyclerView.Adapter<ParticipantsResultAdapter.ViewHolder> {

    private String decodeName;
    private String encodedName;
    private Context context;
    private List<ParticipantPojo> participantPojoList;

    public ParticipantsResultAdapter(List<ParticipantPojo> participantPojoList, Context context){
        super();
        this.participantPojoList = participantPojoList;
        this.context = context;
    }

    @Override
    public ParticipantsResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result_details, parent, false);
        ParticipantsResultAdapter.ViewHolder viewHolder = new ParticipantsResultAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ParticipantsResultAdapter.ViewHolder holder, int position) {
        final ParticipantPojo participantPojo =  participantPojoList.get(position);

        holder.position.setText(String.valueOf(position+1));
        this.encodedName = participantPojo.getPubg_id();
        try {
            this.decodeName = URLDecoder.decode(this.encodedName, "UTF8");
        } catch (UnsupportedEncodingException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        holder.pName.setText(this.decodeName);
        holder.pKills.setText(String.valueOf(participantPojo.getKills()));
        holder.pWinning.setText(String.valueOf(participantPojo.getPrize()));
    }


    @Override
    public int getItemCount() {
        return participantPojoList.size();
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
