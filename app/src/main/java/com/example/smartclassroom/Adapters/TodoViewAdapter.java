package com.example.smartclassroom.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartclassroom.Activities.ClassworkActivity;
import com.example.smartclassroom.Activities.MainActivity;
import com.example.smartclassroom.Classes.CommonFuncClass;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewStreamModel;
import com.example.smartclassroom.Models.WorkStatusModel;
import com.example.smartclassroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TodoViewAdapter extends RecyclerView.Adapter<TodoViewAdapter.TodoViewHolder> {

    private Context context;
    private String tag;
    private ArrayList<NewStreamModel> assignedList = new ArrayList<>();
    private ArrayList<CommentDetailsModel> assignedDetailsList = new ArrayList<>();
    private ArrayList<NewStreamModel> missingList = new ArrayList<>();
    private ArrayList<CommentDetailsModel> missingDetailsList = new ArrayList<>();
    private ArrayList<NewStreamModel> doneList = new ArrayList<>();
    private ArrayList<CommentDetailsModel> doneDetailsList = new ArrayList<>();
    private ArrayList<WorkStatusModel> doneStatusList = new ArrayList<>();
    private HashMap<String, NewClassroomModel> classroomList = new HashMap<>();
    private onItemClickListener listener;
    private int redColor = Color.parseColor("#CF2727");
    private int greyColor = Color.parseColor("#757575");
    private int greenColor = Color.parseColor("#24A629");
    private FirebaseDatabase database;
    private DatabaseReference reference, classDetails, workStatus;
    private FirebaseAuth auth;
    private CollectionReference classrooms, allClassrooms, allClasswork;
    private DocumentReference user;
    private String UserName, Email, UserID;
    private CommonFuncClass cfc;


    public void setTodo(String tag, Context context) {
        this.tag = tag;
        this.context = context;
        firebaseInitializations();
        cfc = new CommonFuncClass(context);
        getUser();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_layout, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        if(tag.equals("Assigned")){
            setAssignedData(holder,position);
        } else if(tag.equals("Missing")){
            setMissingData(holder,position);
        }else {
            setDoneData(holder,position);
        }
    }

    @Override
    public int getItemCount() {
        if (tag.equals("Assigned")) return assignedList.size();
        else if (tag.equals("Missing")) return missingList.size();
        else return doneList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {

        CardView todoCard;
        TextView todoClassworkTitleTxt, todoClassroomNameTxt, statusTxt, dateTxt, timeTxt;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);

            todoClassworkTitleTxt = itemView.findViewById(R.id.todo_classwork_title);
            todoClassroomNameTxt = itemView.findViewById(R.id.todo_classroom_name);
            statusTxt = itemView.findViewById(R.id.status);
            dateTxt = itemView.findViewById(R.id.date);
            timeTxt = itemView.findViewById(R.id.time);
            todoCard = itemView.findViewById(R.id.todo_card_view);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    private void setAssignedData(TodoViewHolder holder, int position) {
//        cfc.toastShort(assignedList.size()+"");
        NewStreamModel newStreamModel = assignedList.get(position);
        NewClassroomModel newClassroomModel = classroomList.get(newStreamModel.getAssignmentId());
        CommentDetailsModel commentDetailsModel = assignedDetailsList.get(position);
        String due = newStreamModel.getDueDate();
        if (due != null) {
            String date = due.substring(0, 10);
            String time = due.substring(11);
            setLayout(holder, greenColor, greenColor, newStreamModel.getTitle(), newClassroomModel.getClassroomName(), "Assigned", date, time, View.GONE, View.VISIBLE, View.VISIBLE);
        } else {
            setLayout(holder, greenColor, greenColor, newStreamModel.getTitle(), newClassroomModel.getClassroomName(), "No due", "", "", View.VISIBLE, View.GONE, View.GONE);
        }
        holder.todoCard.setOnClickListener(view -> {
            setAction(newClassroomModel, newStreamModel, commentDetailsModel);
        });
    }

    private void setMissingData(TodoViewHolder holder, int position) {
//        cfc.toastShort(missingList.size()+"");
        NewStreamModel newStreamModel = missingList.get(position);
        NewClassroomModel newClassroomModel = classroomList.get(newStreamModel.getAssignmentId());
        CommentDetailsModel commentDetailsModel = missingDetailsList.get(position);
        String due = newStreamModel.getDueDate();
        String date = due.substring(0, 10);
        String time = due.substring(11);
        setLayout(holder, redColor, redColor, newStreamModel.getTitle(), newClassroomModel.getClassroomName(), "Missing", date, time, View.GONE, View.VISIBLE, View.VISIBLE);
        holder.todoCard.setOnClickListener(view -> {
            setAction(newClassroomModel, newStreamModel, commentDetailsModel);
        });
    }

    private void setDoneData(TodoViewHolder holder, int position) {
//        cfc.toastShort(doneList.size()+"");
        NewStreamModel newStreamModel = doneList.get(position);
        NewClassroomModel newClassroomModel = classroomList.get(newStreamModel.getAssignmentId());
        CommentDetailsModel commentDetailsModel = doneDetailsList.get(position);
        WorkStatusModel workStatusModel = doneStatusList.get(position);
        String total = newStreamModel.getPoints();
        String returnMarks = workStatusModel.getReturnMarks();
        boolean status = workStatusModel.getReturnStatus();
        if(status){
            if(total!=null){
                setLayout(holder, greenColor, greenColor, newStreamModel.getTitle(), newClassroomModel.getClassroomName(), returnMarks + "/" + total, "", "", View.VISIBLE, View.GONE, View.GONE);
            }else {
                setLayout(holder, greenColor, greenColor, newStreamModel.getTitle(), newClassroomModel.getClassroomName(), "Handed in", "", "", View.VISIBLE, View.GONE, View.GONE);
            }
        }else {
            setLayout(holder, greenColor, greenColor, newStreamModel.getTitle(), newClassroomModel.getClassroomName(), "Handed in", "", "", View.VISIBLE, View.GONE, View.GONE);
        }
        holder.todoCard.setOnClickListener(view -> {
            setAction(newClassroomModel, newStreamModel, commentDetailsModel);
        });
    }

    private void setAction(NewClassroomModel newClassroomModel, NewStreamModel model, CommentDetailsModel detailsModel) {
        Intent intent = new Intent(context, ClassworkActivity.class);
        Bundle data = new Bundle();
        data.putString("Source", "stream");
        data.putSerializable("DetailsModel", detailsModel);
        data.putSerializable("ClassroomModel", newClassroomModel);
        data.putSerializable("Model", model);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }

    private void setLayout(TodoViewHolder holder, int dtC, int ttC, String tcttT, String tcntT, String stT, String dtT, String ttT, int stV, int dtV, int ttV) {
        holder.dateTxt.setTextColor(dtC);
        holder.timeTxt.setTextColor(ttC);

        holder.todoClassworkTitleTxt.setText(tcttT);
        holder.todoClassroomNameTxt.setText(tcntT);
        holder.statusTxt.setText(stT);
        holder.dateTxt.setText(dtT);
        holder.timeTxt.setText(ttT);

        holder.statusTxt.setVisibility(stV);
        holder.dateTxt.setVisibility(dtV);
        holder.timeTxt.setVisibility(ttV);
    }

    private String getTimestamp() {
        String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String Timestamp = Date + " " + Time;
        return Timestamp;
    }

    private void firebaseInitializations() {
        auth = FirebaseAuth.getInstance();
        UserID = auth.getCurrentUser().getUid();
        user = FirebaseFirestore.getInstance().collection("Users").document(UserID);
        classrooms = user.collection("Classrooms");
        allClassrooms = FirebaseFirestore.getInstance().collection("Classrooms");
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    private void getUser() {
        user.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                UserName = snapshot.getString("userName");
                Email = snapshot.getString("email");
                getClassrooms();
            }
        });
    }

    private void getClassrooms() {
        classrooms.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    NewClassroomModel classroomModel = snapshot.toObject(NewClassroomModel.class);
                    String occupation = classroomModel.getOccupation().toLowerCase();
                    if (occupation.equals("student")) {
                        getClasswork(classroomModel);
                    }
                }
            }
        });
    }

    private void getClasswork(NewClassroomModel classroomModel) {
        allClasswork = allClassrooms.document(classroomModel.getClassId()).collection("Classwork");
        allClasswork.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int i =0 ;
                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                    NewStreamModel streamModel = snapshot.toObject(NewStreamModel.class);
                    classroomList.put(streamModel.getAssignmentId(), classroomModel);
                    Log.e("stream "+i,streamModel.getTitle());
                    getWorkStatus(classroomModel,streamModel);
                    i++;
                }
            }
        });
    }

    private void getWorkStatus(NewClassroomModel classroomModel,NewStreamModel streamModel) {
        classDetails = reference.child("Attachments").child(classroomModel.getClassId()).child(streamModel.getAssignmentId());
        workStatus = classDetails.child("Works").child(UserID).child("Status").child("Student");
        workStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String due = streamModel.getDueDate();
//                String total = streamModel.getPoints();
                CommentDetailsModel detailsModel = new CommentDetailsModel(classroomModel.getClassId(), streamModel.getAssignmentId(), UserID, UserName, Email);
                if (snapshot.getValue() != null) {
                    WorkStatusModel statusModel = snapshot.getValue(WorkStatusModel.class);
                    boolean status = statusModel.getReturnStatus();
                    String submitStatus = statusModel.getSubmitStatus().toLowerCase();
//                    String returnMarks = statusModel.getReturnMarks();
                    if (due != null) {
//                        String date = due.substring(0, 10);
//                        String time = due.substring(11);
                        String currentTimestamp = getTimestamp();
                        int val = currentTimestamp.compareTo(due);
                        if (val <= 0) {
                            if (status) {
//                                marks
                                doneList.add(streamModel);
                                doneStatusList.add(statusModel);
                                doneDetailsList.add(detailsModel);
                                Log.e(streamModel.getTitle(),"done");
//                                if (total != null) {
//                                    setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), returnMarks + "/" + total, date, time, View.VISIBLE, View.GONE, View.GONE);
//                                } else {
//                                    setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Handed in", date, time, View.VISIBLE, View.GONE, View.GONE);
//                                }
                            } else {
                                if (submitStatus.equals("hand in")) {
//                                    handed in
                                    doneList.add(streamModel);
                                    doneStatusList.add(statusModel);
                                    doneDetailsList.add(detailsModel);
                                    Log.e(streamModel.getTitle(),"done1");
//                                    setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Handed in", date, time, View.VISIBLE, View.GONE, View.GONE);
                                } else {
//                                    assigned
                                    assignedList.add(streamModel);
                                    assignedDetailsList.add(detailsModel);
                                    Log.e(streamModel.getTitle(),"assigned");
//                                    setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Assigned", date, time, View.GONE, View.VISIBLE, View.VISIBLE);
                                }
                            }
                        } else {
                            if (status) {
//                                marks
                                doneList.add(streamModel);
                                doneStatusList.add(statusModel);
                                doneDetailsList.add(detailsModel);
                                Log.e(streamModel.getTitle(),"done2");
//                                if (total != null) {
//                                    setLayout(holder, redColor, redColor, streamModel.getTitle(), classroomModel.getClassroomName(), returnMarks + "/" + total, date, time, View.VISIBLE, View.GONE, View.GONE);
//                                } else {
//                                    setLayout(holder, redColor, redColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Handed in", date, time, View.VISIBLE, View.GONE, View.GONE);
//                                }
                            } else {
                                if (submitStatus.equals("hand in")) {
//                                    handed in
                                    doneList.add(streamModel);
                                    doneStatusList.add(statusModel);
                                    doneDetailsList.add(detailsModel);
                                    Log.e(streamModel.getTitle(),"done3");
//                                    setLayout(holder, redColor, redColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Handed in", date, time, View.VISIBLE, View.GONE, View.GONE);
                                } else {
//                                    missing
                                    missingList.add(streamModel);
                                    missingDetailsList.add(detailsModel);
                                    Log.e(streamModel.getTitle(),"missing");
//                                    setLayout(holder, redColor, redColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Missing", date, time, View.GONE, View.VISIBLE, View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        if (status) {
//                            marks
                            doneList.add(streamModel);
                            doneStatusList.add(statusModel);
                            doneDetailsList.add(detailsModel);
                            Log.e(streamModel.getTitle(),"done4");
//                            if (total != null) {
//                                setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), returnMarks + "/" + total, "", "", View.VISIBLE, View.GONE, View.GONE);
//                            } else {
//                                setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Handed in", "", "", View.VISIBLE, View.GONE, View.GONE);
//                            }
                        } else {
//                            assigned no due
                            assignedList.add(streamModel);
                            assignedDetailsList.add(detailsModel);
                            Log.e(streamModel.getTitle(),"assigned ND");
//                            setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "No due", "", "", View.VISIBLE, View.GONE, View.GONE);
                        }
                    }
//                    setAction(classroomModel, streamModel, detailsModel);
                    notifyDataSetChanged();
                } else {
                    if (due != null) {
//                        String date = due.substring(0, 10);
//                        String time = due.substring(11);
                        String currentTimestamp = getTimestamp();
                        int val = currentTimestamp.compareTo(due);
                        if (val <= 0) {
//                            assigned
                            assignedList.add(streamModel);
                            assignedDetailsList.add(detailsModel);
                            Log.e(streamModel.getTitle(),"assigned2");
//                            setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Assigned", date, time, View.GONE, View.VISIBLE, View.VISIBLE);
                        } else {
//                            missing
                            missingList.add(streamModel);
                            missingDetailsList.add(detailsModel);
                            Log.e(streamModel.getTitle(),"missing2");
//                            setLayout(holder, redColor, redColor, streamModel.getTitle(), classroomModel.getClassroomName(), "Missing", date, time, View.GONE, View.VISIBLE, View.VISIBLE);
                        }
                    } else {
//                        assigned no due
                        assignedList.add(streamModel);
                        assignedDetailsList.add(detailsModel);
                        Log.e(streamModel.getTitle(),"assigned ND2");
//                        setLayout(holder, greenColor, greenColor, streamModel.getTitle(), classroomModel.getClassroomName(), "No due", "", "", View.VISIBLE, View.GONE, View.GONE);
                    }
//                    setAction(classroomModel, streamModel, detailsModel);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
