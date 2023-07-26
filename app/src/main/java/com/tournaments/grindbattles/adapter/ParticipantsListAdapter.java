package com.tournaments.grindbattles.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.ParticipantPojo;

public class ParticipantsListAdapter extends RecyclerView.Adapter<ParticipantsListAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<ParticipantPojo> participantPojoList;

    public ParticipantsListAdapter(List<ParticipantPojo> participantPojoList, Context context){
        super();
        this.participantPojoList = participantPojoList;
        this.context = context;
    }

    @Override
    public ParticipantsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_participants, parent, false);
        ParticipantsListAdapter.ViewHolder viewHolder = new ParticipantsListAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ParticipantsListAdapter.ViewHolder holder, int position) {
        final ParticipantPojo participantPojo =  participantPojoList.get(position);

        i = position+1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i);
        //stringBuilder.append("     #");
        //stringBuilder.append(participantPojo.getSlot());
        stringBuilder.append(". ");
        stringBuilder.append(participantPojo.getPubg_id());
        holder.nameIv.setText(stringBuilder.toString());
    }


    @Override
    public int getItemCount() {
        return participantPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameIv;

        public ViewHolder(View itemView) {
            super(itemView);

            this.nameIv = (TextView) itemView.findViewById(R.id.nameIv);
        }

    }
}
