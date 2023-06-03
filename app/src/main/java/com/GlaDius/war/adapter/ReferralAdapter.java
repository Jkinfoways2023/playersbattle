package com.GlaDius.war.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.GlaDius.war.R;
import com.GlaDius.war.model.ReferralPojo;

public class ReferralAdapter extends RecyclerView.Adapter<ReferralAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<ReferralPojo> referralPojoList;

    public ReferralAdapter(List<ReferralPojo> referralPojoList, Context context){
        super();
        this.referralPojoList = referralPojoList;
        this.context = context;
    }

    @Override
    public ReferralAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_myreferrals, parent, false);
        ReferralAdapter.ViewHolder viewHolder = new ReferralAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ReferralAdapter.ViewHolder holder, int position) {
        final ReferralPojo referralPojo =  referralPojoList.get(position);

        holder.date.setText(referralPojo.getRefer_date());
        holder.date.setTypeface(null, Typeface.BOLD);

        holder.username.setText(referralPojo.getFname()+" "+referralPojo.getLname());
        holder.username.setTypeface(null, Typeface.BOLD);

        if (referralPojo.getRefer_status().equals("0")){
            holder.status.setText("Registered App");
            holder.status.setTypeface(null, Typeface.BOLD);
        }
        else {
            holder.status.setText("Joined Match");
            holder.status.setTypeface(null, Typeface.BOLD);
        }
    }


    @Override
    public int getItemCount() {
        return referralPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView username;
        TextView status;

        public ViewHolder(View itemView) {
            super(itemView);

            this.date = (TextView) itemView.findViewById(R.id.date);
            this.username = (TextView) itemView.findViewById(R.id.username);
            this.status = (TextView) itemView.findViewById(R.id.status);
        }

    }
}
