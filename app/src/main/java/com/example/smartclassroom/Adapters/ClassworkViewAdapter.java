package com.example.smartclassroom.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.R;

import java.util.ArrayList;

public class ClassworkViewAdapter extends RecyclerView.Adapter<ClassworkViewAdapter.ClassworkViewHolder> {

    private Context context;
    private ArrayList<NewStreamModel> classworkModel=new ArrayList<>();
    private onItemClickListener listener;

    public void setAssignments(ArrayList<NewStreamModel> classworkModel,Context context){
        this.classworkModel=classworkModel;
        this.context=context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.classwork_item_view,parent,false);
        return new ClassworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassworkViewHolder holder, int position) {
        NewStreamModel model=classworkModel.get(position);
        holder.title.setText(model.getTitle());
        holder.time.setText("Posted "+model.getTime());
    }

    @Override
    public int getItemCount() {
        return classworkModel.size();
    }

    public NewStreamModel getAssignment(int position){return classworkModel.get(position);}

    public class ClassworkViewHolder extends RecyclerView.ViewHolder{

        ImageView assignmentImg;
        ImageButton moreOptions;
        TextView title,time;
        CardView cardView;

        public ClassworkViewHolder(@NonNull View itemView) {
            super(itemView);
            assignmentImg=itemView.findViewById(R.id.classwork_assignment_img);
//            moreOptions=itemView.findViewById(R.id.classwork_more_option);
            title=itemView.findViewById(R.id.classwork_assignment_title);
            time=itemView.findViewById(R.id.classwork_assignment_time);
            cardView=itemView.findViewById(R.id.classwork_card_view);

            itemView.setOnClickListener(view -> {
                int position=getAdapterPosition();
                if(listener!=null && position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){this.listener=listener;}
}
