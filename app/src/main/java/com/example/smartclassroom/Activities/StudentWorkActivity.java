package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewPeopleModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.Models.WorkStatusModel;
import com.example.smartclassroom.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class StudentWorkActivity extends AppCompatActivity {

    private Toolbar studentsWorkToolbar;
    private ImageView studentProfileImg;
    private TextView studentNameTxt,statusTxt;
    private EditText marksEdTxt;
    private RecyclerView attachmentsRv;
    private Button returnWorkBtn;
    private CommentDetailsModel detailsModel;
    private NewPeopleModel studentModel;
    private FirebaseDatabase database;
    private DatabaseReference reference,profile, documents, workStatus, classDetails;
    private AttachmentViewAdapter adapter;
    private ArrayList<UploadFileModel> uploadList;
    private String due,total,submitStatus = "null",returnMarks = "null",draftMarks = "null" ,returnStatus = "null",timestamp = "null",returnTimestamp = "null",teacherTimestamp = "null";
    private int greenColor = Color.parseColor("#24A629");
    private int blackColor = Color.parseColor("#FF000000");
    private int redColor = Color.parseColor("#CF2727");
    private boolean extract = false,handedIn = false;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(extract) {
            String marks = marksEdTxt.getText().toString();
            if(!returnMarks.equals("null")){
                if(!returnMarks.equals(marks) && !marks.equals("")){
                    updateMarks();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_work);
        getData();
        initializations();
        configureToolbar();
        firebaseInitializations();
        setStudentProfile();
        setData();
        btnFunc();
        setMarksEdTxt();
        setAttachmentsRv();
        getDue();
        getAttachments();
    }

    private void initializations(){
        studentsWorkToolbar = findViewById(R.id.student_work_toolbar);
        studentProfileImg = findViewById(R.id.student_profile);
        studentNameTxt = findViewById(R.id.student_name);
        statusTxt = findViewById(R.id.status);
        marksEdTxt = findViewById(R.id.marks);
        attachmentsRv = findViewById(R.id.attachments_rv);
        returnWorkBtn = findViewById(R.id.return_work);
    }

    private void firebaseInitializations(){
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        profile = reference.child("Profiles").child(studentModel.getUserId()).child("profileUrl");
        classDetails = reference.child("Attachments").child(detailsModel.getClassId()).child(detailsModel.getId());
        documents = classDetails.child("Works").child(studentModel.getUserId()).child("Attachments");
        workStatus = classDetails.child("Works").child(studentModel.getUserId()).child("Status");
    }

    private void configureToolbar(){
        studentsWorkToolbar.setTitle("");
        setSupportActionBar(studentsWorkToolbar);
        studentsWorkToolbar.setNavigationIcon(R.drawable.back);
        studentsWorkToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void getData(){
        Intent intent = getIntent();
        Bundle data = intent.getBundleExtra("data");
        studentModel = (NewPeopleModel) data.getSerializable("StudentModel");
        detailsModel = (CommentDetailsModel) data.getSerializable("DetailsModel");
    }

    private void setData(){
        studentNameTxt.setText(studentModel.getUserName());
    }

    private void setMarksEdTxt(){
        marksEdTxt.setEnabled(false);
        marksEdTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String marks = charSequence.toString();
                if(returnMarks.equals(marks)){
                    if(returnStatus.equals("true")) {
                        returnWorkBtn.setEnabled(false);
                    }else {
                        returnWorkBtn.setEnabled(true);
                    }
                }else {
                    returnWorkBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        marksEdTxt.setOnEditorActionListener((textView, i, keyEvent) -> {
            String marks = marksEdTxt.getText().toString();
            if(i == EditorInfo.IME_ACTION_DONE && !returnMarks.equals(marks) && !marks.equals("")){
                updateMarks();
                return true;
            }
            return false;
        });
    }

    private void updateMarks(){
        String marks = marksEdTxt.getText().toString();
        if(returnMarks.equals("null")){
            workStatus.child("Teacher").child("draftMarks").setValue(null);
        }else {
            if(returnStatus.equals("true")) {
                workStatus.child("Teacher").child("draftMarks").setValue(returnMarks);
            }
        }
        workStatus.child("Teacher").child("returnMarks").setValue(marks);
        workStatus.child("Teacher").child("returnStatus").setValue(false);
        workStatus.child("Teacher").child("teacherTimestamp").setValue(getTimestamp());
    }

    private void btnFunc() {
        returnWorkBtn.setEnabled(false);
        returnWorkBtn.setOnClickListener(view1 -> {
            setReturn();
        });
    }

    private void setAttachmentsRv() {
        attachmentsRv.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new AttachmentViewAdapter();
        attachmentsRv.setAdapter(adapter);
        adapterWork();
    }

    private void adapterWork() {
        adapter.setAttachmentViewAdapter(uploadList, this);
        adapter.setOnItemClickListener(new AttachmentViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
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

    public void setStudentProfile() {
        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String url = snapshot.getValue().toString();
                    setProfileImg(url);

                } else {
                    String url = "";
                    setProfileImg(url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setProfileImg(String url) {
        if (url.equals("")) {
            studentProfileImg.setImageResource(R.drawable.default_profile);
        } else {
            if (isValidContextForGlide(this)) {
                Glide.with(this).load(url).into(studentProfileImg);
            }
        }
    }

    private void getAttachments() {
        uploadList = new ArrayList<>();
        documents.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UploadFileModel modelClass = snapshot.getValue(UploadFileModel.class);
                uploadList.add(modelClass);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapterWork();
    }

    private void getDue() {
        classDetails.child("dueDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                due = snapshot.getValue().toString();
                getTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotal() {
        classDetails.child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = ""+snapshot.getValue().toString();
                getWorkStatus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWorkStatus() {
        workStatus.child("Teacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    WorkStatusModel model = snapshot.getValue(WorkStatusModel.class);
                    submitStatus = ""+model.getSubmitStatus();
                    returnMarks = ""+model.getReturnMarks();
                    draftMarks = ""+model.getDraftMarks();
                    returnStatus = ""+model.getReturnStatus();
                    timestamp = ""+model.getTimestamp();
                    returnTimestamp = ""+model.getReturnTimestamp();
                    teacherTimestamp = ""+model.getTeacherTimestamp();
                    handedIn = model.isHandedIn();
                    extract = true;
                    if(!handedIn){
                        String currentTimestamp = getTimestamp();
                        if (returnMarks.equals("null")) {
                            if (due != null) {
                                int val = currentTimestamp.compareTo(due);
                                if (val <= 0) {
                                    if (total.equals("null")) {
                                        setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                    } else {
                                        setLayout("Assigned", returnMarks, total, blackColor, true, View.VISIBLE);
                                    }
                                } else {
                                    if (total.equals("null")) {
                                        setLayout("Missing", "", "", redColor, false, View.INVISIBLE);
                                    } else {
                                        setLayout("Missing", returnMarks, total, redColor, true, View.VISIBLE);
                                    }
                                }
                            } else {
                                if (total.equals("null")) {
                                    setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                } else {
                                    setLayout("Assigned", returnMarks, total, blackColor, true, View.VISIBLE);
                                }
                            }
                        } else {
                            if (due != null) {
                                int val = currentTimestamp.compareTo(due);
                                if (val <= 0) {
                                    if (total.equals("null")) {
                                        setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                    } else {
                                        if (draftMarks.equals("null")) {
                                            setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                        } else {
                                            setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                        }
                                    }
                                } else {
                                    if (total.equals("null")) {
                                        setLayout("Missing", "", "", redColor, false, View.INVISIBLE);
                                    } else {
                                        if (draftMarks.equals("null")) {
                                            setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                        } else {
                                            setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
                                if (total.equals("null")) {
                                    setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                } else {
                                    if (draftMarks.equals("null")) {
                                        setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                    } else {
                                        setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                    }
                                }
                            }
                        }
                    }else {
                        if (submitStatus.equals("hand in")) {
                            if (total.equals("null")) {
                                setLayout("Handed in", "", "", greenColor, false, View.INVISIBLE);
                            } else {
                                setLayout("Handed in", returnMarks, total, greenColor, true, View.VISIBLE);
                            }
                        } else if (submitStatus.equals("unsubmit")) {
                            if (returnMarks.equals("null")) {
                                if (due != null) {
                                    int val = timestamp.compareTo(due);
                                    if (val <= 0) {
                                        if (total.equals("null")) {
                                            setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                        } else {
                                            setLayout("Assigned", returnMarks, total, blackColor, true, View.VISIBLE);
                                        }
                                    } else {
                                        if (total.equals("null")) {
                                            setLayout("Missing", "", "", redColor, false, View.INVISIBLE);
                                        } else {
                                            setLayout("Missing", returnMarks, total, redColor, true, View.VISIBLE);
                                        }
                                    }
                                } else {
                                    if (total.equals("null")) {
                                        setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                    } else {
                                        setLayout("Assigned", returnMarks, total, blackColor, true, View.VISIBLE);
                                    }
                                }
                            } else {
                                if (due != null) {
                                    int val = timestamp.compareTo(due);
                                    if (val <= 0) {
                                        if (total.equals("null")) {
                                            setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                        } else {
                                            if (draftMarks.equals("null")) {
                                                setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                            } else {
                                                setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                            }
                                        }
                                    } else {
                                        if (total.equals("null")) {
                                            setLayout("Missing", "", "", redColor, false, View.INVISIBLE);
                                        } else {
                                            if (draftMarks.equals("null")) {
                                                setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                            } else {
                                                setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                            }
                                        }
                                    }
                                } else {
                                    if (total.equals("null")) {
                                        setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                                    } else {
                                        if (draftMarks.equals("null")) {
                                            setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                        } else {
                                            setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                        }
                                    }
                                }
                            }
                        } else if (submitStatus.equals("resubmit")) {
                            if (total.equals("null")) {
                                setLayout("Assigned", "", "", blackColor, false, View.INVISIBLE);
                            } else {
                                if (draftMarks.equals("null")) {
                                    setLayout("Marked", returnMarks, total, blackColor, true, View.VISIBLE);
                                } else {
                                    setLayout("Previously: " + draftMarks + "/" + total, returnMarks, total, blackColor, true, View.VISIBLE);
                                }
                            }
                        }
                    }

                } else {
                    extract = true;
                    if(due!=null){
                        int val = timestamp.compareTo(due);
                        if (val <= 0) {
                            if(total.equals("null")) {
                                setLayout("Assigned", "","",blackColor,false,View.INVISIBLE);
                            }else {
                                setLayout("Assigned",returnMarks,total,blackColor,true,View.VISIBLE);
                            }
                        }else {
                            if(total.equals("null")) {
                                setLayout("Missing", "","",redColor,false,View.INVISIBLE);
                            }else {
                                setLayout("Missing",returnMarks,total,redColor,true,View.VISIBLE);
                            }
                        }
                    }else {
                        if(total.equals("null")) {
                            setLayout("Assigned", "","",blackColor,false,View.INVISIBLE);
                        }else {
                            setLayout("Assigned",returnMarks,total,blackColor,true,View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLayout(String stT,String metT,String metH,int stC,boolean metE,int metV) {
        statusTxt.setText(stT);
        if(metT.equals("null")) marksEdTxt.setText("");
        else marksEdTxt.setText(metT);

        if(!metH.equals("null")) marksEdTxt.setHint("Marks/"+metH);

        statusTxt.setTextColor(stC);

        marksEdTxt.setEnabled(metE);

        marksEdTxt.setVisibility(metV);
    }

    private void setReturn() {
        String marks = marksEdTxt.getText().toString();
        if(!marks.equals("")) {
            WorkStatusModel model1, model2;
            String Timestamp = getTimestamp();
            if (returnTimestamp.equals("null") && timestamp.equals("null")) {
                model1 = new WorkStatusModel("unsubmit", marks, null, null, null, Timestamp, true, handedIn);
                model2 = new WorkStatusModel("unsubmit", marks, null, null, true, handedIn);
            } else if (returnTimestamp.equals("null")) {
                model1 = new WorkStatusModel("unsubmit", marks, null, timestamp, null, Timestamp, true, handedIn);
                model2 = new WorkStatusModel("unsubmit", marks, timestamp, null, true, handedIn);
            } else {
                model1 = new WorkStatusModel("unsubmit", marks, null, returnTimestamp, returnTimestamp, Timestamp, true, handedIn);
                model2 = new WorkStatusModel("unsubmit", marks, returnTimestamp, returnTimestamp, true, handedIn);
            }

            Map<String, Object> map1 = objectMapper.convertValue(model1, Map.class);
            Map<String, Object> map2 = objectMapper.convertValue(model2, Map.class);

            workStatus.child("Teacher").updateChildren(map1);
            workStatus.child("Student").updateChildren(map2);

            returnWorkBtn.setEnabled(false);
            marksEdTxt.setEnabled(false);
        }
    }

    private String getTimestamp() {
        String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String Timestamp = Date + " " + Time;
        return Timestamp;
    }

}