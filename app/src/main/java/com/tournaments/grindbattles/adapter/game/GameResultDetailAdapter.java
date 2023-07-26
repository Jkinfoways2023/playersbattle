package com.tournaments.grindbattles.adapter.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.tournaments.grindbattles.databinding.ListGameResultDetailBinding;
import com.tournaments.grindbattles.model.GameDetailResultModel;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class GameResultDetailAdapter extends RecyclerView.Adapter<GameResultDetailAdapter.ViewHolder> {

    private Context context;
    private List<GameDetailResultModel.ResultBean> arrayList;
    private GameContestsAdapter.OnItemClickListener onItemClickListener;

    public GameResultDetailAdapter(List<GameDetailResultModel.ResultBean> arrayList, Context context){
        super();
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public GameResultDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ListGameResultDetailBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(final GameResultDetailAdapter.ViewHolder holder, int position) {

        final GameDetailResultModel.ResultBean model =  arrayList.get(position);

        holder.binding.userName.setText(model.getName());

        holder.binding.entryFee.setText(model.getEntry_fee());
        holder.binding.winningAmount.setText(model.getWinning_amt());
        holder.binding.gameResult.setText(model.getGame_result());
        holder.binding.score.setText(model.getScore());
        holder.binding.type.setText(model.getGame_type());
        holder.binding.date.setText(model.getDate());
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ListGameResultDetailBinding binding;

        public ViewHolder(ListGameResultDetailBinding binding) {
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
