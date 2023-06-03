package com.GlaDius.war.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.GlaDius.war.R;
import com.GlaDius.war.common.Config;
import com.GlaDius.war.model.ProductPojo;
import com.GlaDius.war.views.Tools;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<ProductPojo> productPojoList;

    private OnItemClickListener mOnItemClickListener;
    private OnMoreButtonClickListener onMoreButtonClickListener;

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnMoreButtonClickListener(final OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }


    public ProductAdapter(List<ProductPojo> productPojoList, Context context){
        super();
        this.productPojoList = productPojoList;
        this.context = context;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_product, parent, false);
        ProductAdapter.ViewHolder viewHolder = new ProductAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ProductAdapter.ViewHolder holder, final int position) {
        final ProductPojo productPojo =  productPojoList.get(position);

        holder.title.setText(productPojo.getName());
        holder.actualPrice.setText(productPojo.getPrice());
        holder.actualPrice.setPaintFlags(holder.actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.ourPrice.setText(productPojo.getPrice_discount());
        Tools.displayImageOriginal(context, holder.image, Config.FILE_PATH_URL+productPojo.getImage());

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, productPojoList.get(position), position);
                }
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onMoreButtonClickListener == null) return;
                onMoreButtonClick(view, productPojo);
            }
        });

    }

    private void onMoreButtonClick(final View view, final ProductPojo productPojo) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMoreButtonClickListener.onItemClick(view, productPojo, item);
                return true;
            }
        });
        popupMenu.inflate(R.menu.menu_product_more);
        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return productPojoList.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, ProductPojo obj, int pos);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, ProductPojo obj, MenuItem item);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView title;
        public TextView actualPrice;
        public TextView ourPrice;
        public ImageButton more;
        public View lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            actualPrice = (TextView) itemView.findViewById(R.id.actualPrice);
            ourPrice = (TextView) itemView.findViewById(R.id.ourPrice);
            more = (ImageButton) itemView.findViewById(R.id.more);
            lyt_parent = (View) itemView.findViewById(R.id.lyt_parent);
        }

    }

}

