package com.tournaments.grindbattles.adapter.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.databinding.ListGameContestsBinding;
import com.tournaments.grindbattles.model.HtmlGameContestsPojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GameContestsAdapter extends RecyclerView.Adapter<GameContestsAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<HtmlGameContestsPojo.ResultBean> arrayList;
    private OnItemClickListener onItemClickListener;

    public GameContestsAdapter(List<HtmlGameContestsPojo.ResultBean> arrayList, Context context){
        super();
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public GameContestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ListGameContestsBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(final GameContestsAdapter.ViewHolder holder, int position) {

        if (position%6==0){
            holder.binding.mainLayout.setBackgroundResource(R.drawable.grad6);
        }else if (position%5==0){
            holder.binding.mainLayout.setBackgroundResource(R.drawable.grad5);
        }else if (position%4==0){
            holder.binding.mainLayout.setBackgroundResource(R.drawable.grad4);
        }else if (position%3==0){
            holder.binding.mainLayout.setBackgroundResource(R.drawable.grad3);
        }else if (position%2==0){
            holder.binding.mainLayout.setBackgroundResource(R.drawable.grad2);
        }else {
            holder.binding.mainLayout.setBackgroundResource(R.drawable.grad1);
        }

        final HtmlGameContestsPojo.ResultBean model =  arrayList.get(position);

        holder.binding.gameType.setText(model.getGame_type());
        holder.binding.title.setText(model.getGame_title());
        holder.binding.entryFee.setText(String.format("%s%s%s","Entry Fee:",context.getResources().getString(R.string.rupee_sign),model.getEntry_fee()));
        holder.binding.winningAmount.setText(String.format("%s %s","Win : ",model.getWinning_fee()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ListGameContestsBinding binding;

        public ViewHolder(ListGameContestsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

            binding.getRoot().setOnClickListener(view -> {
                if (onItemClickListener!=null){
                    onItemClickListener.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;

    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
