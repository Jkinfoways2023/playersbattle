package com.GlaDius.war.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.model.LotteryResultsPojo;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LotteryResultsAdapter extends RecyclerView.Adapter<LotteryResultsAdapter.ViewHolder> {

    private Context context;
    private List<LotteryResultsPojo>lotteryResultsPojoList;

    public LotteryResultsAdapter(List<LotteryResultsPojo> lotteryResultsPojoList, Context context){
        super();
        this.lotteryResultsPojoList = lotteryResultsPojoList;
        this.context = context;
    }

    @Override
    public LotteryResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_lottery_result, parent, false);
        LotteryResultsAdapter.ViewHolder viewHolder = new LotteryResultsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final LotteryResultsAdapter.ViewHolder holder, int position) {
        final LotteryResultsPojo lotteryResultsPojo =  lotteryResultsPojoList.get(position);

        holder.date.setText(lotteryResultsPojo.getTime());
        holder.title.setText(lotteryResultsPojo.getTitle());
        holder.prize.setText(lotteryResultsPojo.getPrize());
        holder.winner.setText(lotteryResultsPojo.getName());
        holder.winningNo.setText(lotteryResultsPojo.getLottery_no());
        Picasso.get().load(Config.FILE_PATH_URL+lotteryResultsPojo.getImage().replace(" ", "%20")).resize(500,250).placeholder(R.drawable.lottery).into(holder.lotteryBanner);
    }


    @Override
    public int getItemCount() {
        return lotteryResultsPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView date;
        TextView title;
        TextView prize;
        TextView winner;
        TextView winningNo;
        ImageView lotteryBanner;

        public ViewHolder(View itemView) {
            super(itemView);

            this.date = (TextView) itemView.findViewById(R.id.date);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.prize = (TextView) itemView.findViewById(R.id.prize);
            this.winner = (TextView) itemView.findViewById(R.id.winner);
            lotteryBanner = (ImageView) itemView.findViewById(R.id.lotteryBanner);
            this.winningNo = (TextView) itemView.findViewById(R.id.winningNo);
        }

    }

}
