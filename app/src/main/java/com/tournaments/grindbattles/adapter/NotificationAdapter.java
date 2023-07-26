package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.activity.NotificationDetailsActivity;
import com.tournaments.grindbattles.model.NotificationPojo;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<NotificationPojo> notificationPojoList;

    public NotificationAdapter(List<NotificationPojo> notificationPojoList, Context context){
        super();
        this.notificationPojoList = notificationPojoList;
        this.context = context;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        NotificationAdapter.ViewHolder viewHolder = new NotificationAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, int position) {
        final NotificationPojo notificationPojo =  notificationPojoList.get(position);

        holder.titleTv.setText(notificationPojo.getTitle());
        holder.titleTv.setTypeface(null, Typeface.BOLD);

        String stringBuilder2 =notificationPojo.getCreated();
        holder.dateTv.setText(stringBuilder2);
        //holder.dateTv.setTypeface(null, Typeface.BOLD);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotificationDetailsActivity.class);
                intent.putExtra("id", notificationPojo.getId());
                intent.putExtra("title", notificationPojo.getTitle());
                intent.putExtra("message", notificationPojo.getMessage());
                intent.putExtra("image", notificationPojo.getImage());
                intent.putExtra("url", notificationPojo.getUrl());
                intent.putExtra("created", notificationPojo.getCreated());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return notificationPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView srNo;
        TextView dateTv;
        TextView titleTv;

        public ViewHolder(View itemView) {
            super(itemView);

            this.srNo = (ImageView) itemView.findViewById(R.id.srNo);
            this.dateTv = (TextView) itemView.findViewById(R.id.dateTv);
            this.titleTv = (TextView) itemView.findViewById(R.id.titleTv);
        }

    }
}
