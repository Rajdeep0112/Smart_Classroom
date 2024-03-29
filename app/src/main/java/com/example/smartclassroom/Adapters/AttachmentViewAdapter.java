package com.example.smartclassroom.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
//import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class AttachmentViewAdapter extends RecyclerView.Adapter<AttachmentViewAdapter.AttachmentViewHolder>{
    private static final long MAX_BYTES_DOC = 50000000;
    private Context context;
    private ArrayList<UploadFileModel> uploadList = new ArrayList<>();
    private onItemClickListener listener;

    public void setAttachmentViewAdapter(ArrayList<UploadFileModel> uploadList,Context context) {
        this.uploadList = uploadList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.attachments_item_view,parent,false);
        return new AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        UploadFileModel model = uploadList.get(position);
        image(model.getUrl(),model,holder);
//        Glide.with(context).load(model.getUrl()).into(holder.doc_image);

//        holder.doc_image.loadUrl(model.getUrl());
//        holder.doc_image.setImageBitmap(image);
        holder.doc_name.setText(model.getName());

//        holder.doc_image.setOnClickListener(view -> {
//            Intent intent = new Intent(context, DocumentViewActivity.class);
//            Bundle data = new Bundle();
//            data.putSerializable("UploadModel",model);
//            intent.putExtra("data",data);
//            context.startActivity(intent);
//        });
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

    private void image(String url,UploadFileModel model, AttachmentViewHolder holder){
//        holder.progressBar.setVisibility(View.VISIBLE);
//        AtomicReference<Bitmap> bitmap = new AtomicReference<>();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
//        ref.getBytes(MAX_BYTES_DOC).addOnSuccessListener(bytes -> {
//            holder.doc_image;
//        });
        ref.getBytes(MAX_BYTES_DOC).addOnSuccessListener(bytes -> {
//            bitmap.set(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
//            holder.doc_image.setImageBitmap(bitmap.get());
//            holder.pdfView.fromBytes(bytes).pages(0).spacing(0).swipeHorizontal(false).enableSwipe(false).load();
//            Log.e("context", bytes + "");
            ref.getMetadata().addOnSuccessListener(storageMetadata -> {
                String type = storageMetadata.getContentType();
                if(type.contains("pdf")){
                    try {
                        File file = File.createTempFile(model.getName(),null,null);
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bytes);
                        Bitmap bitmap;
                        PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY));
                        PdfRenderer.Page page = renderer.openPage(0);
                        int width = page.getWidth();
                        int height = page.getHeight();
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        page.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.doc_image.setImageBitmap(bitmap);
                        Log.e(model.getName(), bytes.toString());
//                if(holder.doc_image.getDrawable()!=null) holder.progressBar.setVisibility(View.GONE);
//                Document document = new Document()
//                context.getContentResolver().openInputStream()
                        fos.close();
                    } catch (IOException e) {

                    }
                }
                else{
                    if (isValidContextForGlide(context)){
                        Log.e(model.getName(), String.valueOf((holder.progressBar==null)));
                        Glide.with(context).load(model.getUrl())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        holder.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        holder.progressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(holder.doc_image);
//                    if(holder.doc_image.getDrawable()!=null) holder.progressBar.setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return (int) uploadList.size();
    }

    public class AttachmentViewHolder extends RecyclerView.ViewHolder{

        private ImageView doc_type;
        private ImageView doc_image;
//        private WebView doc_image;
        private TextView doc_name;
//        private PDFView pdfView;
        private ProgressBar progressBar;

        public AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_image = itemView.findViewById(R.id.doc_image);
            doc_name = itemView.findViewById(R.id.doc_name);
            doc_type = itemView.findViewById(R.id.doc_type);
//            pdfView = itemView.findViewById(R.id.pdfView);
            progressBar = itemView.findViewById(R.id.progress_bar);

//            doc_image.getSettings().setJavaScriptEnabled(true);
//            doc_image.getSettings().setLoadWithOverviewMode(true);
//            doc_image.getSettings().setUseWideViewPort(true);


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

    public void setOnItemClickListener(onItemClickListener listener){this.listener= (onItemClickListener) listener;}

}
