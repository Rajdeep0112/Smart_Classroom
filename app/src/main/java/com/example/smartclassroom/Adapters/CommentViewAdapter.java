package com.example.smartclassroom.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartclassroom.Models.NewCommentModel;
import com.example.smartclassroom.R;

import java.util.List;

public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.CommentViewHolder> {

    private List<NewCommentModel> comments;
    private String UserId;
    private Boolean Status;
    private int Send,Receive;

    public CommentViewAdapter(List<NewCommentModel> comments, String userId) {
        this.comments = comments;
        UserId = userId;
        Status=false;
        Send=1;
        Receive=2;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==Send){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_send_item_view,parent,false);
        }else{
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_recivied_item_view,parent,false);
        }
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        if(!Status){
            holder.userName.setText(comments.get(position).getFromUserName());
        }
        holder.msg.setText(comments.get(position).getMessage());
        holder.time.setText(comments.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private TextView msg,time,userName;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            if(Status){
                msg=itemView.findViewById(R.id.sendTxt);
                time=itemView.findViewById(R.id.time);
            }else {
                msg=itemView.findViewById(R.id.receivedTxt);
                time=itemView.findViewById(R.id.time);
                userName=itemView.findViewById(R.id.user_name);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(comments.get(position).getFromId().equals(UserId)){
            Status=true;
            return Send;
        }else {
            Status=false;
            return Receive;
        }
    }
}
