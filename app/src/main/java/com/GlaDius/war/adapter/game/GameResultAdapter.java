package com.GlaDius.war.adapter.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.GlaDius.war.R;
import com.GlaDius.war.databinding.ListGameResultBinding;
import com.GlaDius.war.model.HtmlGameContestsPojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GameResultAdapter extends RecyclerView.Adapter<GameResultAdapter.ViewHolder> {

    private int i = 0;
    private Context context;
    private List<HtmlGameContestsPojo.ResultBean> arrayList;
    private GameContestsAdapter.OnItemClickListener onItemClickListener;

    public GameResultAdapter(List<HtmlGameContestsPojo.ResultBean> arrayList, Context context){
        super();
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public GameResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ListGameResultBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(final GameResultAdapter.ViewHolder holder, int position) {

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
        private ListGameResultBinding binding;

        public ViewHolder(ListGameResultBinding binding) {
            super(binding.getRoot());
            this.binding=binding;

            binding.getRoot().setOnClickListener(view -> {
                if (onItemClickListener!=null){
                    onItemClickListener.OnItemClick(getAdapterPosition());
                }
            });
        }
    }

    public void setOnItemClickListener(GameContestsAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
