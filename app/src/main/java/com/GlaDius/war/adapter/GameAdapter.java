package com.GlaDius.war.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.GlaDius.war.R;
import com.GlaDius.war.activity.MatchActivity;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.model.GamePojo;
import com.GlaDius.war.utils.ExtraOperations;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private Context context;
    private List<GamePojo> gamePojoList;

    public GameAdapter(List<GamePojo> gamePojoList, Context context){
        super();
        this.gamePojoList = gamePojoList;
        this.context = context;
    }

    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_games, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_games_grid, parent, false);
        GameAdapter.ViewHolder viewHolder = new GameAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GameAdapter.ViewHolder holder, int position) {
        final GamePojo gamePojo =  gamePojoList.get(position);

        holder.gameTitle.setText(gamePojo.getTitle());

        if (!gamePojo.getBanner().isEmpty()) {
            holder.gameImage.setVisibility(View.VISIBLE);
            Log.e("imagepathissss",Config.FILE_PATH_URL+gamePojo.getBanner());
            Picasso.get().load(Config.FILE_PATH_URL+gamePojo.getBanner()).resize(720,480).placeholder(R.drawable.bg_game_holder).into(holder.gameImage);
        }
        else {
            holder.gameImage.setVisibility(View.GONE);
        }

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (gamePojo.getType().equals("0")){
                    if (new ExtraOperations().haveNetworkConnection(context)) {
                        Intent intent = new Intent(context, MatchActivity.class);
                        intent.putExtra("ID_KEY", gamePojo.getId());
                        intent.putExtra("TITLE_KEY", gamePojo.getTitle());
                        intent.putExtra("URL_KEY", gamePojo.getUrl());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (new ExtraOperations().haveNetworkConnection(context)) {
                        if (!gamePojo.getUrl().startsWith("http://") && !gamePojo.getUrl().startsWith("https://")){
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+gamePojo.getUrl())));
                        }
                        else {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(gamePojo.getUrl())));
                        }
                    }
                    else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
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

}

