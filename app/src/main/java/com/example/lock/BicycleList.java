package com.example.lock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BicycleList extends RecyclerView.Adapter<BicycleList.MyViewHolder> {

    ArrayList<String> bicycleNames;
    CycleclickCallback cycleclickCallback;
    BicycleList(ArrayList<String> bicycleNames,CycleclickCallback cycleclickCallback){
        this.bicycleNames=bicycleNames;
        this.cycleclickCallback=cycleclickCallback;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view,parent,false);
        MyViewHolder viewHolder=new MyViewHolder(view,cycleclickCallback);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cycleName.setText(bicycleNames.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return bicycleNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cycleName;
        CycleclickCallback cycleclickCallback;
        public MyViewHolder(@NonNull View itemView,CycleclickCallback cycleclickCallback) {
            super(itemView);
            cycleName=itemView.findViewById(R.id.cycleName);
            this.cycleclickCallback=cycleclickCallback;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            cycleclickCallback.onBicycleClicked(getAdapterPosition());
        }
    }
    public interface CycleclickCallback{
        void onBicycleClicked(int position);
    }
}
