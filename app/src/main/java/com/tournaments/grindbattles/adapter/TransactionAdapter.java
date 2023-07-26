package com.tournaments.grindbattles.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.model.TransactionPojo;
import com.tournaments.grindbattles.utils.ActionAlertMessage;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<TransactionPojo> transactionPojoList;
    private String PrevDate;

    public TransactionAdapter(List<TransactionPojo> transactionPojoList, Context context){
        super();
        this.transactionPojoList = transactionPojoList;
        this.context = context;
    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_transactions, parent, false);
        TransactionAdapter.ViewHolder viewHolder = new TransactionAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TransactionAdapter.ViewHolder holder, int position) {
        final TransactionPojo transactionPojo =  transactionPojoList.get(position);

        holder.tnName.setText(transactionPojo.getRemark());

        if(position == 0){

            PrevDate = "empty";

        }else{

            PrevDate = transactionPojoList.get(position - 1).getDate();
        }

        try {
            if (transactionPojo.getDate().intern() != PrevDate.intern()){

                holder.date.setText(transactionPojo.getDate());
                holder.date.setVisibility(View.VISIBLE);
            }

            if (transactionPojo.getType().equals("1")){
                holder.tncat.setText("#id : PCCR"+transactionPojo.getId());
                holder.amount.setText("+ " + transactionPojo.getCoins_used());
                holder.amount.setTextColor(context.getResources().getColor(R.color.successGreen));

            }else if(transactionPojo.getType().equals("0")){
                holder.tncat.setText("#id : PCDB"+transactionPojo.getId());
                holder.amount.setText("- " + transactionPojo.getCoins_used());
                holder.amount.setTextColor(context.getResources().getColor(R.color.colorError));
            }

            if(transactionPojo.getStatus().equals("0")){
                ((GradientDrawable)holder.statusName.getBackground()).setColor(context.getResources().getColor(R.color.colorWarning));
                holder.statusName.setText(context.getResources().getString(R.string.pending));

            }else if(transactionPojo.getStatus().equals("1")){

                ((GradientDrawable)holder.statusName.getBackground()).setColor(context.getResources().getColor(R.color.colorSuccess));
                holder.statusName.setText(context.getResources().getString(R.string.success));

            }else if(transactionPojo.getStatus().equals("2")){

                ((GradientDrawable)holder.statusName.getBackground()).setColor(context.getResources().getColor(R.color.colorError));
                holder.statusName.setText(context.getResources().getString(R.string.rejected));

            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        holder.SingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActionAlertMessage.transactionDetailDialog(context,transactionPojo.getId(), transactionPojo.getUser_id(), transactionPojo.getRequest_name(), transactionPojo.getReq_from(), transactionPojo.getGetway_name(), transactionPojo.getDate(), transactionPojo.getPayment_id(),transactionPojo.getCoins_used(),transactionPojo.getReq_amount(), transactionPojo.getType(),transactionPojo.getStatus(),transactionPojo.getRemark());
            }
        });
    }


    @Override
    public int getItemCount() {
        return transactionPojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView date,tnName,tncat,amount,statusName;
        ImageView image;
        LinearLayout SingleItem;

        public ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            tnName = itemView.findViewById(R.id.tnName);
            tncat = itemView.findViewById(R.id.tnType);
            amount = itemView.findViewById(R.id.amount);
            statusName = itemView.findViewById(R.id.statusName);
            image = itemView.findViewById(R.id.image);
            SingleItem = itemView.findViewById(R.id.SingleItem);
        }

    }
}
