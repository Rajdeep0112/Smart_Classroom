package com.example.smartclassroom.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartclassroom.Models.NewFileModel;
import com.example.smartclassroom.R;

import java.util.ArrayList;

public class SelectedAttachmentAdapter extends RecyclerView.Adapter<SelectedAttachmentAdapter.SelectedAttachmentViewHolder> {

    private ArrayList<NewFileModel> fileList;
    private Context context;

    public SelectedAttachmentAdapter(ArrayList<NewFileModel> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectedAttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_attachment_item_view,parent,false);
        return new SelectedAttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedAttachmentViewHolder holder, int position) {
        NewFileModel model = fileList.get(position);
        int idx = model.getFileType().indexOf('/');
        String type = model.getFileType().substring(idx+1);
        if(model.getFileType().substring(0,idx).equals("image")) type = "image";
        int res = context.getResources().getIdentifier(type,"drawable",context.getPackageName());
        holder.docImg.setImageResource(res);
        holder.docName.setText(model.getFileName());
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class SelectedAttachmentViewHolder extends RecyclerView.ViewHolder{

        private TextView docName;
        private ImageView docImg;

        public SelectedAttachmentViewHolder(@NonNull View itemView) {
            super(itemView);

            docImg = itemView.findViewById(R.id.doc_type);
            docName = itemView.findViewById(R.id.doc_name);
        }
    }
}
