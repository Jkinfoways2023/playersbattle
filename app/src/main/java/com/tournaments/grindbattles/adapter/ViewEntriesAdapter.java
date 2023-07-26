package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.ParticipantPojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ViewEntriesAdapter extends RecyclerView.Adapter<ViewEntriesAdapter.ViewHolder> {

    private Context context;
    private List<ParticipantPojo> participantPojoList;

    public ViewEntriesAdapter(List<ParticipantPojo> participantPojoList, Context context){
        super();
        this.participantPojoList = participantPojoList;
        this.context = context;
    }

    @Override
    public ViewEntriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_entries, parent, false);
        ViewEntriesAdapter.ViewHolder viewHolder = new ViewEntriesAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewEntriesAdapter.ViewHolder holder, int position) {
        final ParticipantPojo myEntriesPojo =  participantPojoList.get(position);

        /*if (myEntriesPojo.getIs_canceled().equals("1")) {
            holder.editButton.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.GONE);
        } else {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setVisibility(View.VISIBLE);
        }*/

        holder.editButton.setVisibility(View.GONE);
        holder.cancelButton.setVisibility(View.GONE);


        TextView textView = holder.ingamename;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("• ");
        stringBuilder.append(myEntriesPojo.getPubg_id());
        textView.setText(stringBuilder.toString());
        //holder.cancelButton.setOnClickListener(new BottomSheetMyEntries.CustomList.C2182a(myEntriesPojo));
    }


    @Override
    public int getItemCount() {
        return participantPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingamename;
        ImageView editButton,cancelButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.ingamename = (TextView) itemView.findViewById(R.id.ingamename);
            this.editButton = (ImageView) itemView.findViewById(R.id.editButton);
            this.cancelButton = (ImageView) itemView.findViewById(R.id.cancelButton);
        }

    }
}