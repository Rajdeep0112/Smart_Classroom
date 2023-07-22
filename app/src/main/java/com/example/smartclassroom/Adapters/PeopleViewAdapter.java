package com.example.smartclassroom.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        setUserProfile(model.getUserId(), viewHolder.userProfile,viewHolder.progressBar,context);
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
        private ImageView userProfile;
        private ProgressBar progressBar;

        public PeopleViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.people_card_view);
            userName = itemView.findViewById(R.id.people_user_name);
            userProfile = itemView.findViewById(R.id.people_profile);
            progressBar = itemView.findViewById(R.id.progress_bar);

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

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    public void setUserProfile(String userId, ImageView imageView, ProgressBar progressBar, Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        DatabaseReference profile = reference.child("Profiles").child(userId);
        profile.child("profileUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String url = snapshot.getValue().toString();
                    setProfileImg(url, imageView, progressBar, context);

                } else {
                    String url = "";
                    setProfileImg(url, imageView, progressBar, context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setProfileImg(String url, ImageView imageView, ProgressBar progressBar, Context context) {
        if (url.equals("")) {
            progressBar.setVisibility(View.GONE);
            imageView.setBackground(context.getDrawable(R.drawable.circle_boundary));
            imageView.setImageResource(R.drawable.default_profile);
        } else {
            if(context!=null) {
                if (isValidContextForGlide(context)) {
                    Glide.with(context).load(url)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    imageView.setBackground(context.getDrawable(R.drawable.circle_no_boundary));
                                    return false;
                                }
                            }).into(imageView);
                }
            }
        }
    }
}
