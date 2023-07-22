package com.example.smartclassroom.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.R;

import java.util.ArrayList;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder>{

    private ArrayList<NewClassroomModel> modelList= new ArrayList<>();
    private Context context;
    private onItemClickListener listener;
    private ProgressBar progressBar;

    public void setClasses(ArrayList<NewClassroomModel> modelList, ProgressBar progressBar, Context context){
        this.modelList=modelList;
        this.context=context;
        this.progressBar = progressBar;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.classroom_item_view,parent,false);
        return new ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        if(modelList.size()-1==position){
            progressBar.setVisibility(View.GONE);
        }
        NewClassroomModel model=modelList.get(position);
        holder.classroomName.setText(model.getClassroomName());
        holder.section.setText(model.getSection());
        String occupation= model.getOccupation().toLowerCase();
        if(occupation.equals("teacher")){
            String noOfStudents=model.getNoOfStudents()+" Students";
            holder.name.setText(noOfStudents);
        }else holder.name.setText(model.getUserName());
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public NewClassroomModel getClassroom(int position){
        return modelList.get(position);
    }

    public class ClassroomViewHolder extends RecyclerView.ViewHolder {
        TextView classroomName,section,name;
        CardView cardView;
        public ClassroomViewHolder(@NonNull View itemView) {
            super(itemView);
            classroomName=itemView.findViewById(R.id.classroomName);
            section=itemView.findViewById(R.id.section);
            name=itemView.findViewById(R.id.name);
            cardView=itemView.findViewById(R.id.classroomCardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();
                    if(listener!=null && position!=RecyclerView.NO_POSITION){
                        listener.onItemClick(modelList.get(position));
                    }
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(NewClassroomModel classroomModel);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
}
