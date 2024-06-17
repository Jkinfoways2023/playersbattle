package com.tournaments.grindbattles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tournaments.grindbattles.Interface.OnItemClickListener;
import com.tournaments.grindbattles.databinding.DesignSlotsBinding;
import com.tournaments.grindbattles.model.SlotListOrganizedModel;

import java.util.List;

public class SlotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    List<SlotListOrganizedModel.Data> slotmodel;
    Context context;
    private OnItemClickListener onItemClickListener;
    int total_selected=0;
    int max_team=0;
    public SlotListAdapter(List<SlotListOrganizedModel.Data> slotmodel, Context context,int total_selected) {
        this.slotmodel = slotmodel;
        this.total_selected=total_selected;
        this.context = context;
        max_team=this.slotmodel.get(0).position.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(DesignSlotsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.binding.slotSequance.setText(String.valueOf(slotmodel.get(position).slot));
        if(slotmodel.get(position).position.size()==2)
        {
            //duo
            disable_duo_checkbox(position,viewHolder);
        }
        else if(slotmodel.get(position).position.size()==4)
        {
            //squad
            disable_squad_checkbox(position,viewHolder);
        }
        else{
         //
            disable_solo_checkbox(position,viewHolder);
        }
        onchecklitsnter(position,viewHolder);
    }

    private void onchecklitsnter(int position, ViewHolder viewHolder)
    {

            viewHolder.binding.posA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {

                        if(max_team==total_selected) {
                            Toast.makeText(context, "Maximum "+max_team+" Allowed"+total_selected, Toast.LENGTH_SHORT).show();
                            viewHolder.binding.posA.setChecked(false);
                        }
                        else {
                            total_selected=total_selected+1;
                        }

                    }
                    else{
                        total_selected=total_selected-1;
                    }

                }
            });
            viewHolder.binding.posB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked)
                    {

                        if(max_team==total_selected) {
                            Toast.makeText(context, "Maximum "+max_team+" Allowed"+total_selected, Toast.LENGTH_SHORT).show();
                            viewHolder.binding.posB.setChecked(false);
                        }
                        else {
                            total_selected=total_selected+1;
                        }

                    }
                    else{
                        total_selected=total_selected-1;
                    }
                }
            });
            viewHolder.binding.posC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(isChecked)
                        {

                            if(max_team==total_selected) {
                                Toast.makeText(context, "Maximum "+max_team+" Allowed"+total_selected, Toast.LENGTH_SHORT).show();
                                viewHolder.binding.posC.setChecked(false);
                            }
                            else {
                                total_selected=total_selected+1;
                            }

                        }
                        else{
                            total_selected=total_selected-1;
                        }

                }
            });
            viewHolder.binding.posD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {

                        if(max_team==total_selected) {
                            Toast.makeText(context, "Maximum "+max_team+" Allowed"+total_selected, Toast.LENGTH_SHORT).show();
                            viewHolder.binding.posD.setChecked(false);
                        }
                        else {
                            total_selected=total_selected+1;
                        }

                    }
                    else{
                        total_selected=total_selected-1;
                    }
                }
            });


    }

    private void disable_squad_checkbox(int position, ViewHolder viewHolder)
    {
        if(slotmodel.get(position).position.get(0).selected)
        {
            viewHolder.binding.posA.setChecked(true);
            viewHolder.binding.posA.setClickable(false);
            viewHolder.binding.posA.setAlpha(0.3f);
        }
        if(slotmodel.get(position).position.get(1).selected)
        {
            viewHolder.binding.posB.setChecked(true);
            viewHolder.binding.posB.setClickable(false);
            viewHolder.binding.posB.setAlpha(0.3f);
        }
        if(slotmodel.get(position).position.get(2).selected)
        {
            viewHolder.binding.posC.setChecked(true);
            viewHolder.binding.posC.setClickable(false);
            viewHolder.binding.posC.setAlpha(0.3f);
        }
        if(slotmodel.get(position).position.get(3).selected)
        {
            viewHolder.binding.posD.setChecked(true);
            viewHolder.binding.posD.setClickable(false);
            viewHolder.binding.posD.setAlpha(0.3f);
        }


    }
    private void disable_duo_checkbox(int position, ViewHolder viewHolder)
    {
        if(slotmodel.get(position).position.get(0).selected)
        {
            viewHolder.binding.posA.setChecked(true);
            viewHolder.binding.posA.setClickable(false);
            viewHolder.binding.posA.setAlpha(0.3f);
        }
        if(slotmodel.get(position).position.get(1).selected)
        {
            viewHolder.binding.posB.setChecked(true);
            viewHolder.binding.posB.setClickable(false);
            viewHolder.binding.posB.setAlpha(0.3f);
        }
        viewHolder.binding.llC.setVisibility(View.GONE);
        viewHolder.binding.llD.setVisibility(View.GONE);
    }
    private void disable_solo_checkbox(int position, ViewHolder viewHolder)
    {
        if(slotmodel.get(position).position.get(0).selected)
        {
            viewHolder.binding.posA.setChecked(true);
            viewHolder.binding.posA.setClickable(false);
            viewHolder.binding.posA.setAlpha(0.3f);
        }
        viewHolder.binding.llA.setVisibility(View.GONE);
        viewHolder.binding.llC.setVisibility(View.GONE);
        viewHolder.binding.llD.setVisibility(View.GONE);
    }



    @Override
    public int getItemCount() {
        return slotmodel.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final DesignSlotsBinding binding;
        public ViewHolder(DesignSlotsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
