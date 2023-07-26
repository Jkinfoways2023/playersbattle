package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.common.Config;
import com.tournaments.grindbattles.model.SliderPojo;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private Context context;
    private List<SliderPojo> sliderPojoList;

    private SliderAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(final SliderAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public SliderAdapter(List<SliderPojo> sliderPojoList, Context context){
        super();
        this.sliderPojoList = sliderPojoList;
        this.context = context;
    }

    @Override
    public SliderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_slider, parent, false);
        SliderAdapter.ViewHolder viewHolder = new SliderAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SliderAdapter.ViewHolder holder, final int position) {
        final SliderPojo sliderPojo =  sliderPojoList.get(position);
        //Tools.displayImageOriginal(context, holder.imageIv, Config.FILE_PATH_URL+sliderPojo.getProd_img());
        Picasso.get().load(Config.FILE_PATH_URL+sliderPojo.getProd_img().replace(" ", "%20")).into(holder.imageIv);
        holder.imageIv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, sliderPojoList.get(position), position);
                }
            }
        });

    }

    public interface OnItemClickListener {
        void onItemClick(View view, SliderPojo obj, int pos);
    }

    @Override
    public int getItemCount() {
        return sliderPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageIv;

        public ViewHolder(View itemView) {
            super(itemView);

            imageIv = (ImageView) itemView.findViewById(R.id.imageIv);
        }

    }

}

