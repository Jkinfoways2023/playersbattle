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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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


                if(!transactionPojo.getDate().contains("-"))
                {
                    holder.date.setText(convertTimestampToDate(transactionPojo.getDate()));
                }
                else{
                    holder.date.setText(convertDateString(transactionPojo.getDate()));
                }
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
                ActionAlertMessage.transactionDetailDialog(context,transactionPojo.getId(), transactionPojo.getUser_id(), transactionPojo.getRequest_name(), transactionPojo.getReq_from(), transactionPojo.getGetway_name(),  holder.date.getText().toString(), transactionPojo.getPayment_id(),transactionPojo.getCoins_used(),transactionPojo.getReq_amount(), transactionPojo.getType(),transactionPojo.getStatus(),transactionPojo.getRemark());
            }
        });
    }

    public static String convertTimestampToDate(String timestampString) {
        // Convert the String to a long
        long timestamp = Long.parseLong(timestampString);

        // Determine if the timestamp is in milliseconds or microseconds
        if (timestampString.length() > 13) {
            // Assume timestamp is in microseconds
            timestamp /= 1000;
        } else if (timestampString.length() > 10) {
            // Assume timestamp is in milliseconds
            // No conversion needed
        } else {
            // Assume timestamp is in seconds
            timestamp *= 1000;
        }

        // Create a Date object with the timestamp in milliseconds
        Date date = new Date(timestamp);

        // Format the date to a human-readable format
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
        return formatter.format(date);
    }

    public static String convertDateString(String dateString) {
        // Define the input and output date formats
        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

        try {
            // Parse the input date string to a Date object
            Date date = inputFormatter.parse(dateString);

            // Format the Date object to the desired output format
            return outputFormatter.format(date);
        } catch (ParseException e) {
            // Handle the exception if the date string is not parsable
            e.printStackTrace();
            return null;
        }
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
