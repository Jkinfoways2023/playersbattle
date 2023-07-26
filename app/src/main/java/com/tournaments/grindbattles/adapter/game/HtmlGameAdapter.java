package com.tournaments.grindbattles.adapter.game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.HtmlGamePojo;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class HtmlGameAdapter extends RecyclerView.Adapter<HtmlGameAdapter.ViewHolder> {

    private Context context;
    private List<HtmlGamePojo> gamePojoList;
    private OnItemClickListener onItemClickListener;

    public HtmlGameAdapter(List<HtmlGamePojo> gamePojoList, Context context){
        super();
        this.gamePojoList = gamePojoList;
        this.context = context;
    }

    @Override
    public HtmlGameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_games, parent, false);
        HtmlGameAdapter.ViewHolder viewHolder = new HtmlGameAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HtmlGameAdapter.ViewHolder holder, int position) {
        final HtmlGamePojo model =  gamePojoList.get(position);

        holder.gameTitle.setText(model.getTitle());

        /*if (!gamePojo.getBanner().isEmpty()) {
            holder.gameImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+gamePojo.getBanner()).resize(720,480).placeholder(R.drawable.bg_game_holder).into(holder.gameImage);
        }
        else {
            holder.gameImage.setVisibility(View.GONE);
        }*/
        holder.gameImage.setImageResource(model.getImage());

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (onItemClickListener!=null){
                    onItemClickListener.OnItemClick(position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return gamePojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout mainCard;
        TextView gameTitle;
        ImageView gameImage;

        public ViewHolder(View itemView) {
            super(itemView);

            this.gameImage = (ImageView) itemView.findViewById(R.id.gameImage);
            this.gameTitle = (TextView) itemView.findViewById(R.id.gameTitle);
            this.mainCard = (RelativeLayout) itemView.findViewById(R.id.mainCard);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;

    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}

