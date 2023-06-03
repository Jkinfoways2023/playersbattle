package com.GlaDius.war.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.GlaDius.war.R;
import com.GlaDius.war.model.StatisticsPojo;

public class MyStatisticsAdapter extends RecyclerView.Adapter<MyStatisticsAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<StatisticsPojo> statisticsPojoList;

    public MyStatisticsAdapter(List<StatisticsPojo> statisticsPojoList, Context context){
        super();
        this.statisticsPojoList = statisticsPojoList;
        this.context = context;
    }

    @Override
    public MyStatisticsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_mystatistics, parent, false);
        MyStatisticsAdapter.ViewHolder viewHolder = new MyStatisticsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyStatisticsAdapter.ViewHolder holder, int position) {
        final StatisticsPojo statisticsPojo =  statisticsPojoList.get(position);

        i = position+1;

        TextView textView = holder.sNo;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(i);
        textView.setText(stringBuilder.toString());
        holder.matchTitle.setText(statisticsPojo.getTitle());
        textView = holder.matchDate;
        stringBuilder = new StringBuilder();
        stringBuilder.append("Played on ");
        stringBuilder.append(statisticsPojo.getTime());
        textView.setText(stringBuilder.toString());

        if (statisticsPojo.getEntry_fee()==0) {
            holder.amountPaid.setText("FREE");
            holder.amountPaid.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.amountPaid.setText(String.valueOf(statisticsPojo.getEntry_fee()));
        }
        holder.amountWon.setText(String.valueOf(statisticsPojo.getPrize()));
    }


    @Override
    public int getItemCount() {
        return statisticsPojoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView amountPaid;
        TextView amountWon;
        TextView matchDate;
        TextView matchTitle;
        TextView sNo;

        public ViewHolder(View itemView) {
            super(itemView);

            this.sNo = (TextView) itemView.findViewById(R.id.srNo);
            this.matchTitle = (TextView) itemView.findViewById(R.id.matchTitle);
            this.matchDate = (TextView) itemView.findViewById(R.id.matchDate);
            this.amountPaid = (TextView) itemView.findViewById(R.id.amountPaid);
            this.amountWon = (TextView) itemView.findViewById(R.id.amountWon);
        }

    }
}
