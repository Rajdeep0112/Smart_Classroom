package com.example.smartclassroom.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.R;

import java.util.ArrayList;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class PeopleViewAdapter extends Section {

    private Context context;
    private ArrayList<NewPeopleModel> peopleModels=new ArrayList<>();
    private String Section;
    private onItemClickListener listener;

    public PeopleViewAdapter(Context context, ArrayList<NewPeopleModel> peopleModels, String Section){
        super(SectionParameters.builder()
                .itemResourceId(R.layout.people_item_view)
                .headerResourceId(R.layout.people_header_view)
                .build());
        this.context = context;
        this.peopleModels=peopleModels;
        this.Section=Section;
    }
    @Override
    public int getContentItemsTotal() {
        return peopleModels.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new PeopleViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new PeopleHeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        NewPeopleModel model = peopleModels.get(position);
        PeopleViewHolder viewHolder = (PeopleViewHolder) holder;
        viewHolder.userName.setText(model.getUserName());
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);
        PeopleHeaderViewHolder viewHolder = (PeopleHeaderViewHolder) holder;
        viewHolder.textView.setText(Section);
    }

    public class PeopleViewHolder extends RecyclerView.ViewHolder{

        private CardView cardView;
        private TextView userName;

        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.people_card_view);
            userName = itemView.findViewById(R.id.people_user_name);

            itemView.setOnClickListener(view -> {
                int position=getAdapterPosition();
                if(listener!=null && position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(peopleModels.get(position));
                }
            });
        }
    }

    public class PeopleHeaderViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public PeopleHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.header);
        }
    }

    public interface onItemClickListener{
        void onItemClick(NewPeopleModel newPeopleModel);
    }

    public void setOnItemClickListener(onItemClickListener listener){this.listener=listener;}

}
