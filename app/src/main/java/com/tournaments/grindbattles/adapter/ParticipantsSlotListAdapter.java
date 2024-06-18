package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.ParticipantPojo;

import java.util.List;

public class ParticipantsSlotListAdapter extends RecyclerView.Adapter<ParticipantsSlotListAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<ParticipantPojo> participantPojoList;

    public ParticipantsSlotListAdapter(List<ParticipantPojo> participantPojoList, Context context){
        super();
        this.participantPojoList = participantPojoList;
        this.context = context;
    }

    @Override
    public ParticipantsSlotListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_participants, parent, false);
        ParticipantsSlotListAdapter.ViewHolder viewHolder = new ParticipantsSlotListAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ParticipantsSlotListAdapter.ViewHolder holder, int position) {
        final ParticipantPojo participantPojo =  participantPojoList.get(position);

        i = position+1;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("=> ");
        stringBuilder.append(participantPojo.getPos_string());
        stringBuilder.append("  ");
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
