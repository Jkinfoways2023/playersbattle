package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.LotteryResultsPojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyLotteryAdapter extends RecyclerView.Adapter<MyLotteryAdapter.ViewHolder> {

    private Context context;
    private List<LotteryResultsPojo> lotteryResultsPojoList;

    public MyLotteryAdapter(List<LotteryResultsPojo> lotteryResultsPojoList, Context context){
        super();
        this.lotteryResultsPojoList = lotteryResultsPojoList;
        this.context = context;
    }

    @Override
    public MyLotteryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_participants, parent, false);
        MyLotteryAdapter.ViewHolder viewHolder = new MyLotteryAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyLotteryAdapter.ViewHolder holder, int position) {
        final LotteryResultsPojo lotteryResultsPojo =  lotteryResultsPojoList.get(position);

        TextView textView = holder.nameTv;
        StringBuilder sb = new StringBuilder();
        sb.append("• ");
        sb.append(lotteryResultsPojo.getName());
        textView.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return lotteryResultsPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            this.nameTv = (TextView) itemView.findViewById(R.id.nameIv);
        }

    }
}