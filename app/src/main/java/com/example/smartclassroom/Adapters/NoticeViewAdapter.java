package com.example.smartclassroom.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartclassroom.Activities.CommentActivity;
import com.example.smartclassroom.Activities.MainActivity;
import com.example.smartclassroom.Classes.CommonFuncClass;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class NoticeViewAdapter extends RecyclerView.Adapter<NoticeViewAdapter.NoticeViewHolder> {

    private Context context;
    private ArrayList<NewStreamModel> streamModel = new ArrayList<>();
    private CommentDetailsModel detailsModel;
    private onItemClickListener listener;
    private int Status, Notice, Assignment;
    private CommonFuncClass cfc;

    public void setNotices(ArrayList<NewStreamModel> streamModel, CommentDetailsModel detailsModel, Context context) {
        this.streamModel = streamModel;
        this.detailsModel = detailsModel;
        this.context = context;
        cfc = new CommonFuncClass(context);
        Status = 1;
        Notice = 1;
        Assignment = 2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Notice) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item_view, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stream_classwork_item_view, parent, false);
        }
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NewStreamModel model = streamModel.get(position);
        holder.addComment.setOnClickListener(view -> {
            if (model.getNoticeId() != null) detailsModel.setId(model.getNoticeId());
            else detailsModel.setId(model.getAssignmentId());
            Intent intent = new Intent(context, CommentActivity.class);
            Bundle data = new Bundle();
            data.putSerializable("DetailsModel", (Serializable) detailsModel);
            intent.putExtra("data", data);
            context.startActivity(intent);
        });
        if (model.getNoticeId() != null) {
            holder.userName.setText(model.getUserName());
            holder.date.setText(model.getTime());
            holder.desc.setText(model.getNoticeShare());
            setUserProfile(model.getUserId(), holder, context);
            if (model.getNoOfAttachments() > 0) {
                holder.attach.setVisibility(View.VISIBLE);
                holder.attachData.setText(model.getNoOfAttachments() + " attachment");
            } else holder.attach.setVisibility(View.GONE);
            if (model.getNoOfComments() > 0) {
                holder.addComment.setText(model.getNoOfComments() + " class comments");
            } else holder.addComment.setText(R.string.AddClassComment);
        } else {
            holder.streamClassworkTitle.setText("New assignment: " + model.getTitle());
            holder.streamClassworkTime.setText(model.getTime());
            if (model.getNoOfComments() > 0) {
                holder.addComment.setText(model.getNoOfComments() + " class comments");
            } else holder.addComment.setText(R.string.AddClassComment);
        }

    }

    @Override
    public int getItemCount() {
        return streamModel.size();
    }

    public NewStreamModel getNotice(int position) {
        return streamModel.get(position);
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        ImageButton moreVer;
        TextView userName, date, desc, attachData, addComment, streamClassworkTitle, streamClassworkTime;
        ImageView userProfile, attachImg, streamClassworkImg;
        CardView streamCard;
        LinearLayout attach;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);

            if (Status == 1) {
                moreVer = itemView.findViewById(R.id.noticeMore);
                userName = itemView.findViewById(R.id.noticeUserName);
                date = itemView.findViewById(R.id.noticeDate);
                desc = itemView.findViewById(R.id.noticeDesc);
                attachData = itemView.findViewById(R.id.noticeAttachText);
                addComment = itemView.findViewById(R.id.noticeComment);
                userProfile = itemView.findViewById(R.id.noticeProfile);
                attachImg = itemView.findViewById(R.id.noticeAttachImage);
                streamCard = itemView.findViewById(R.id.noticeCardView);
                attach = itemView.findViewById(R.id.noticeAttach);
            } else {
                streamClassworkTitle = itemView.findViewById(R.id.stream_classwork_title);
                streamClassworkTime = itemView.findViewById(R.id.stream_classwork_time);
                streamClassworkImg = itemView.findViewById(R.id.stream_classwork_img);
                addComment = itemView.findViewById(R.id.stream_classwork_comment);
                streamCard = itemView.findViewById(R.id.stream_classwork_card_view);
                moreVer = itemView.findViewById(R.id.stream_classwork_more_option);
            }

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (streamModel.get(position).getNoticeId() != null) {
            Status = 1;
            return Notice;
        } else {
            Status = 2;
            return Assignment;
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

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

    public void setUserProfile(String userId, NoticeViewHolder holder, Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        DatabaseReference profile = reference.child("Profiles").child(userId);
        profile.child("profileUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String url = snapshot.getValue().toString();
                    setProfileImg(url, holder, context);

                } else {
                    String url = "";
                    setProfileImg(url, holder, context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setProfileImg(String url, NoticeViewHolder holder, Context context) {
        if (url.equals("")) {
            holder.userProfile.setImageResource(R.drawable.default_profile);
        } else {
            if (isValidContextForGlide(context)) {
                Glide.with(context).load(url).into(holder.userProfile);
            }
        }
    }
}
