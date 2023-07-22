package com.example.smartclassroom.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartclassroom.Activities.StudentWorkActivity;
import com.example.smartclassroom.Classes.CommonFuncClass;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.Models.WorkStatusModel;
import com.example.smartclassroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class StudentWorkViewAdapter extends RecyclerView.Adapter<StudentWorkViewAdapter.StudentWorkViewHolder>{
    private ArrayList<NewPeopleModel> studentList = new ArrayList<>();
    private CommentDetailsModel detailsModel;
    private Context context;
    private onItemClickListener listener;
    private int redColor = Color.parseColor("#CF2727");
    private int greyColor = Color.parseColor("#757575");
    private int greenColor = Color.parseColor("#24A629");
    private String due, total,returnStatus="false";
    private FirebaseDatabase database;
    private DatabaseReference reference, teacherStatus, classDetails;
    private CommonFuncClass cfc;

    public void setStudentWorkViewAdapter(ArrayList<NewPeopleModel> studentList, CommentDetailsModel detailsModel, Context context) {
        this.studentList = studentList;
        this.detailsModel = detailsModel;
        this.context = context;
        cfc = new CommonFuncClass(context);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        classDetails = reference.child("Attachments").child(detailsModel.getClassId()).child(detailsModel.getId());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentWorkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_work_item_view,parent,false);
        return new StudentWorkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentWorkViewHolder holder, int position) {
        NewPeopleModel model = studentList.get(position);
        setUserProfile(model.getUserId(),holder,context);
        holder.studentUserNameTxt.setText(model.getUserName());
        getDue(model.getUserId(),holder,context);
        setCardAction(holder,context,model,detailsModel);

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public class StudentWorkViewHolder extends RecyclerView.ViewHolder{

        private ImageView studentProfileImg;
        private TextView studentUserNameTxt,submitStatusTxt,returnMarksTxt,draftMarksTxt,submitStatusDescTxt;
        private CardView studentWorkCard;
        private ProgressBar progressBar;

        public StudentWorkViewHolder(@NonNull View itemView) {
            super(itemView);

            studentProfileImg = itemView.findViewById(R.id.student_profile);
            studentUserNameTxt = itemView.findViewById(R.id.student_user_name);
            submitStatusTxt = itemView.findViewById(R.id.submit_status);
            returnMarksTxt = itemView.findViewById(R.id.return_marks);
            draftMarksTxt = itemView.findViewById(R.id.draft_marks);
            submitStatusDescTxt = itemView.findViewById(R.id.submit_status_desc);
            studentWorkCard = itemView.findViewById(R.id.student_work_card);
            progressBar = itemView.findViewById(R.id.progress_bar);

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

    public void setOnItemClickListener(StudentWorkViewAdapter.onItemClickListener listener){this.listener=listener;}

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

    public void setUserProfile(String userId, StudentWorkViewHolder holder, Context context) {
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

    public void setProfileImg(String url, StudentWorkViewHolder holder, Context context) {
        if (url.equals("")) {
            holder.progressBar.setVisibility(View.GONE);
            holder.studentProfileImg.setBackground(context.getDrawable(R.drawable.circle_boundary));
            holder.studentProfileImg.setImageResource(R.drawable.default_profile);
        } else {
            if (isValidContextForGlide(context)) {
                Glide.with(context).load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                holder.studentProfileImg.setBackground(context.getDrawable(R.drawable.circle_no_boundary));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.studentProfileImg.setBackground(context.getDrawable(R.drawable.circle_no_boundary));
                                return false;
                            }
                        }).into(holder.studentProfileImg);
            }
        }
    }

    private void getDue(String userId,StudentWorkViewHolder holder,Context context) {
        classDetails.child("dueDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                due = snapshot.getValue().toString();
                getTotal(userId,holder,context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotal(String userId,StudentWorkViewHolder holder,Context context) {
        classDetails.child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = snapshot.getValue().toString();
                getWorkStatus(userId,holder,context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWorkStatus(String userId,StudentWorkViewHolder holder,Context context) {
        teacherStatus = classDetails.child("Works").child(userId).child("Status").child("Teacher");
        teacherStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    WorkStatusModel model1 = snapshot.getValue(WorkStatusModel.class);
                    String submitStatus = ""+model1.getSubmitStatus();
                    String returnMarks = ""+model1.getReturnMarks();
                    String draftMarks = ""+model1.getDraftMarks();
                    returnStatus = ""+model1.getReturnStatus();
                    String timestamp = ""+model1.getTimestamp();
                    String returnTimestamp = ""+model1.getReturnTimestamp();
                    Boolean handedIn = model1.isHandedIn();
                    if(!handedIn){
                        if (due != null) {
                            String currentTimestamp = getTimestamp();
                            int val = currentTimestamp.compareTo(due);
                            if (val <= 0) {
                                if (returnStatus.equals("true")) {
                                    cfc.toastShort("1u1");
                                    setLayout(holder, "Assigned", returnMarks + "/" + total, draftMarks + "/" + total, "Not handed in", greyColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
                                } else {
                                    cfc.toastShort("11u1");
                                    setLayout(holder, "Assigned", returnMarks + "/" + total, draftMarks + "/" + total, "Not handed in", greyColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                }
                            } else {
                                if (returnStatus.equals("true")) {
                                    cfc.toastShort("2u2");
                                    setLayout(holder, "Missing", returnMarks + "/" + total, draftMarks + "/" + total, "Not handed in", redColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
                                } else {
                                    cfc.toastShort("22u2");
                                    setLayout(holder, "Missing", returnMarks + "/" + total, draftMarks + "/" + total, "Not handed in", redColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                }
                            }
                        }
                    }else {
                        if (submitStatus.equals("hand in") && returnMarks.equals("null")) {
                            if (due != null) {
                                int val = timestamp.compareTo(due);
                                if (val <= 0) {
                                    cfc.toastShort("h1");
                                    setLayout(holder, "Handed in", returnMarks, draftMarks, "", greenColor, greenColor, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                                } else {
                                    cfc.toastShort("h2");
                                    setLayout(holder, "Handed in", returnMarks, draftMarks, "Done late", greenColor, greenColor, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);
                                }
                            } else {
                                cfc.toastShort("h3");
                                setLayout(holder, "Handed in", returnMarks, draftMarks, "", greenColor, greenColor, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                            }
                        } else if (submitStatus.equals("unsubmit")) {
                            if (!returnMarks.equals("null")) {
                                if (due != null) {
                                    int val = timestamp.compareTo(due);
                                    if (val <= 0) {
                                        if (returnStatus.equals("true")) {
                                            cfc.toastShort("1u");
                                            setLayout(holder, "Assigned", returnMarks + "/" + total, draftMarks + "/" + total, "", greyColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.GONE);
                                        } else {
                                            cfc.toastShort("11u");
                                            setLayout(holder, "Assigned", returnMarks + "/" + total, draftMarks + "/" + total, "", greyColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                        }
                                    } else {
                                        if (returnStatus.equals("true")) {
                                            cfc.toastShort("2u");
                                            setLayout(holder, "Missing", returnMarks + "/" + total, draftMarks + "/" + total, "Done late", redColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
                                        } else {
                                            cfc.toastShort("22u");
                                            setLayout(holder, "Missing", returnMarks + "/" + total, draftMarks + "/" + total, "Done late", redColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                        }
                                    }
                                } else {
                                    if (returnStatus.equals("true")) {
                                        cfc.toastShort("3u");
                                        setLayout(holder, "", returnMarks + "/" + total, draftMarks + "/" + total, "", greyColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
                                    } else {
                                        cfc.toastShort("33u");
                                        setLayout(holder, "", returnMarks + "/" + total, draftMarks + "/" + total, "", greyColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
                                    }
                                }
                            } else {
                                if (due != null) {
                                    int val = timestamp.compareTo(due);
                                    if (val <= 0) {
                                        cfc.toastShort("u1");
                                        setLayout(holder, "Assigned", returnMarks, draftMarks, "", greyColor, greenColor, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                                    } else {
                                        cfc.toastShort("u2");
                                        setLayout(holder, "Missing", returnMarks, draftMarks, "", redColor, greenColor, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                                    }
                                } else {
                                    cfc.toastShort("u3");
                                    setLayout(holder, "Assigned", returnMarks, draftMarks, "", greyColor, greenColor, View.VISIBLE, View.GONE, View.GONE, View.GONE);
                                }
                            }

                        } else if (submitStatus.equals("resubmit")) {
                            if (due != null) {
                                int reVal = returnTimestamp.compareTo(due);
                                if (reVal <= 0) {
                                    if (returnStatus.equals("true")) {
                                        cfc.toastShort("r1");
                                        setLayout(holder, "Handed in", returnMarks + "/" + total, draftMarks + "/" + total, "", greenColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.GONE);
                                    } else {
                                        cfc.toastShort("r11");
                                        setLayout(holder, "Handed in", returnMarks + "/" + total, draftMarks + "/" + total, "", greenColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                    }
                                } else {
                                    if (returnStatus.equals("true")) {
                                        cfc.toastShort("r2");
                                        setLayout(holder, "Handed in", returnMarks + "/" + total, draftMarks + "/" + total, "Done late", greenColor, greenColor, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);
                                    } else {
                                        cfc.toastShort("r22");
                                        setLayout(holder, "Handed in", returnMarks + "/" + total, draftMarks + "/" + total, "Done late", greenColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                    }
                                }
                            } else {
                                if (returnStatus.equals("true")) {
                                    cfc.toastShort("r3");
                                    setLayout(holder, "Handed in", returnMarks + "/" + total, draftMarks + "/" + total, "", greenColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                } else {
                                    cfc.toastShort("r33");
                                    setLayout(holder, "Handed in", returnMarks + "/" + total, draftMarks + "/" + total, "", greenColor, greenColor, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE);
                                }
                            }
                        }
                    }

                } else {
                    if (due != null) {
                        String timestamp = getTimestamp();
                        int val = timestamp.compareTo(due);
                        if (val <= 0) {
                            cfc.toastShort("1");
                            setLayout(holder, "Assigned", "null", "null", "", greyColor, greenColor,View.VISIBLE, View.GONE, View.GONE, View.GONE);
                        } else {
                            cfc.toastShort("2");
                            setLayout(holder, "Missing", "null", "null", "", redColor, greenColor,View.VISIBLE, View.GONE, View.GONE, View.GONE);
                        }
                    } else {
                        cfc.toastShort("3");
                        setLayout(holder, "Assigned", "null", "null", "", greyColor, greenColor,View.VISIBLE, View.GONE, View.GONE, View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLayout(StudentWorkViewHolder holder,String sstT,String rmtT,String dmtT,String ssdtT,int sstC,int rmtC,int sstV,int rmtV,int dmtV,int ssdtV) {
        TextView submitStatusTxt,returnMarksTxt,draftMarksTxt,submitStatusDescTxt;

        submitStatusTxt = holder.submitStatusTxt;
        returnMarksTxt = holder.returnMarksTxt;
        draftMarksTxt = holder.draftMarksTxt;
        submitStatusDescTxt = holder.submitStatusDescTxt;

        submitStatusTxt.setText(sstT);
        returnMarksTxt.setText(rmtT);
        if(dmtT.substring(0,4).equals("null")) draftMarksTxt.setText("Draft");
        else draftMarksTxt.setText("Previously: "+dmtT);
        submitStatusDescTxt.setText(ssdtT);

        submitStatusTxt.setTextColor(sstC);
        if(returnStatus.equals("true")) returnMarksTxt.setTextColor(greyColor);
        else returnMarksTxt.setTextColor(rmtC);

        submitStatusTxt.setVisibility(sstV);
        returnMarksTxt.setVisibility(rmtV);
        draftMarksTxt.setVisibility(dmtV);
        submitStatusDescTxt.setVisibility(ssdtV);
    }

    private String getTimestamp() {
        String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String Timestamp = Date + " " + Time;
        return Timestamp;
    }

    private void setCardAction(StudentWorkViewHolder holder,Context context,NewPeopleModel studentModel,CommentDetailsModel detailsModel){
        holder.studentWorkCard.setOnClickListener(view -> {
            Intent intent = new Intent(context, StudentWorkActivity.class);
            Bundle data = new Bundle();
            data.putSerializable("StudentModel",studentModel);
            data.putSerializable("DetailsModel",detailsModel);
            intent.putExtra("data", data);
            context.startActivity(intent);
        });
    }
}
