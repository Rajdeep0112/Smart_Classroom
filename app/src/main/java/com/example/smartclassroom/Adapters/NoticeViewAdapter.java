package com.example.smartclassroom.Adapters;

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

import com.example.smartclassroom.Activities.CommentActivity;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewNoticeModel;
import com.example.smartclassroom.R;

import java.io.Serializable;
import java.util.ArrayList;

public class NoticeViewAdapter extends RecyclerView.Adapter<NoticeViewAdapter.NoticeViewHolder> {

    private Context context;
    private ArrayList<NewNoticeModel> noticeModel = new ArrayList<>();
    private CommentDetailsModel detailsModel;
    private onItemClickListener listener;
    private int Status,Notice,Assignment;

    public void setNotices(ArrayList<NewNoticeModel> noticeModel, CommentDetailsModel detailsModel, Context context) {
        this.noticeModel = noticeModel;
        this.detailsModel = detailsModel;
        this.context = context;
        Status=1;
        Notice=1;
        Assignment=2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==Notice){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item_view, parent, false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stream_classwork_item_view,parent,false);
        }
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NewNoticeModel model = noticeModel.get(position);
        holder.addComment.setOnClickListener(view -> {
            if(model.getNoticeId()!=null) detailsModel.setId(model.getNoticeId());
            else detailsModel.setId(model.getAssignmentId());
            Intent intent = new Intent(context, CommentActivity.class);
            Bundle data = new Bundle();
            data.putSerializable("DetailsModel", (Serializable) detailsModel);
            intent.putExtra("data",data);
            context.startActivity(intent);
        });
        if(model.getNoticeId()!=null) {
            holder.userName.setText(model.getUserName());
            holder.date.setText(model.getTime());
            holder.desc.setText(model.getNoticeShare());
            if (model.getNoOfAttachments() > 0) {
                holder.attach.setVisibility(View.VISIBLE);
                holder.attachData.setText(model.getNoOfAttachments() + " attachment");
            }
            if(model.getNoOfComments()>0) {
                holder.addComment.setText(model.getNoOfComments() + " class comments");
            }
        }else {
            holder.streamClassworkTitle.setText("New assignment: "+model.getTitle());
            holder.streamClassworkTime.setText(model.getTime());
            if(model.getNoOfComments()>0) {
                holder.addComment.setText(model.getNoOfComments() + " class comments");
            }
        }

    }

    @Override
    public int getItemCount() {
        return noticeModel.size();
    }

    public NewNoticeModel getNotice(int position) {
        return noticeModel.get(position);
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        ImageButton moreVer;
        TextView userName, date, desc, attachData, addComment, streamClassworkTitle, streamClassworkTime;
        ImageView userProfile, attachImg, streamClassworkImg;
        CardView noticeCard;
        LinearLayout attach;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);

            if(Status==1) {
                moreVer = itemView.findViewById(R.id.noticeMore);
                userName = itemView.findViewById(R.id.noticeUserName);
                date = itemView.findViewById(R.id.noticeDate);
                desc = itemView.findViewById(R.id.noticeDesc);
                attachData = itemView.findViewById(R.id.noticeAttachText);
                addComment = itemView.findViewById(R.id.noticeComment);
                userProfile = itemView.findViewById(R.id.noticeProfile);
                attachImg = itemView.findViewById(R.id.noticeAttachImage);
                noticeCard = itemView.findViewById(R.id.noticeCardView);
                attach = itemView.findViewById(R.id.noticeAttach);
            }else {
                streamClassworkTitle = itemView.findViewById(R.id.stream_classwork_title);
                streamClassworkTime = itemView.findViewById(R.id.stream_classwork_time);
                streamClassworkImg = itemView.findViewById(R.id.stream_classwork_img);
                addComment = itemView.findViewById(R.id.stream_classwork_comment);
                moreVer = itemView.findViewById(R.id.stream_classwork_more_option);
            }

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(noticeModel.get(position));
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(noticeModel.get(position).getNoticeId()!=null){
            Status = 1;
            return Notice;
        }else {
            Status = 2;
            return Assignment;
        }
    }

    public interface onItemClickListener {
        void onItemClick(NewNoticeModel newNoticeModel);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }
}
