package com.example.whosherejava;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    Context context;
    Activity activity;
    List<Class> classList;

    public RecyclerAdapter(Context context, Activity activity,  List<Class> classList) {
        this.context = context;
        this.classList = classList;
        this.activity=activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.class_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.cItemTV.setText(classList.get(position).getName());
//        TO BE CONT LATER
//        holder.cItemIV.setImageResource(classList.get(position).get);
        holder.cItemIV.setBackgroundColor(classList.get(position).getColor());
        holder.cItemCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cid", "poop: "+classList.get(position).getCid());

                MainActivity mainActivity= (MainActivity) activity;
                mainActivity.currClassId = classList.get(position).getCid();
                ((MainActivity) activity).goToClassPage(classList.get(position).getCid());

            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView cItemCV;
        TextView cItemTV;
        ImageView cItemIV;

        public MyViewHolder(View itemView){
            super(itemView);

            cItemTV = itemView.findViewById(R.id.cItemTV);
            cItemIV = itemView.findViewById(R.id.cItemIV);
            cItemCV = itemView.findViewById(R.id.cItemCV);
        }
    }
}
