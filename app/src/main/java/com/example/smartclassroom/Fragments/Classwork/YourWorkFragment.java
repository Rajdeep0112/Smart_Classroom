package com.example.smartclassroom.Fragments.Classwork;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.Activities.DocumentViewActivity;
import com.example.smartclassroom.Adapters.AttachmentViewAdapter;
import com.example.smartclassroom.Models.CommentDetailsModel;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewFileModel;
import com.example.smartclassroom.Models.NewStreamModel;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class YourWorkFragment extends Fragment {

    private View view;
    private static final int FILE_UPLOAD_CODE = 200;
    private TextView marksTxt, dueDateTxt, submitStatusTxt, submitStatusDescTxt, attachmentsTxt, addWorkTxt, submitTxt;
    private RecyclerView attachmentsRv;
    private CardView addWorkCard, submitCard;
    private LinearLayout addWorkLayout, submitLayout;
    private ArrayList<NewFileModel> fileList = new ArrayList<>();
    private ArrayList<UploadFileModel> uploadList = new ArrayList<>();
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference reference, documents, workStatus, classDetails;
    private NewStreamModel model;
    private NewClassroomModel classroomModel;
    private CommentDetailsModel detailsModel;
    private AttachmentViewAdapter adapter;
    private String due, total, submitStatus, returnMarks, timestamp = getTimestamp(), returnTimestamp, returnStatus;
    private boolean handedIn;
    private ObjectMapper objectMapper = new ObjectMapper();
    private int redColor = Color.parseColor("#CF2727");
    private int greyColor = Color.parseColor("#757575");

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_UPLOAD_CODE && resultCode == RESULT_OK && data != null) {
            fileList = new ArrayList<>();
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                int position = 0;
                while (position < count) {
                    Uri uri = data.getClipData().getItemAt(position).getUri();
                    String fileName = getFileName(uri);
                    String fileExtension = getFileExtension(uri);
                    NewFileModel model1 = new NewFileModel(uri.toString(), fileName, fileExtension);
                    uploadFile(detailsModel.getClassId(), detailsModel.getId(), detailsModel.getUserId(), model1);
                    position++;
//                fileList.add(model1);
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                String fileName = getFileName(uri);
                String fileExtension = getFileExtension(uri);
                NewFileModel model1 = new NewFileModel(uri.toString(), fileName, fileExtension);
                uploadFile(detailsModel.getClassId(), detailsModel.getId(), detailsModel.getUserId(), model1);
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_your_work, container, false);
        getData();
        initializations();
        firebaseInitializations();
        btnFunc();
        getDue();
        setAttachmentsRv();
        getAttachments();
        return view;
    }

    private void initializations() {
        marksTxt = view.findViewById(R.id.marks);
        dueDateTxt = view.findViewById(R.id.due_date);
        submitStatusTxt = view.findViewById(R.id.submit_status);
        submitStatusDescTxt = view.findViewById(R.id.submit_status_desc);
        attachmentsTxt = view.findViewById(R.id.attachments);
        addWorkTxt = view.findViewById(R.id.add_work_txt);
        submitTxt = view.findViewById(R.id.submit_txt);
        attachmentsRv = view.findViewById(R.id.attachments_rv);
        addWorkCard = view.findViewById(R.id.add_work);
        submitCard = view.findViewById(R.id.submit);
        addWorkLayout = view.findViewById(R.id.add_work_layout);
        submitLayout = view.findViewById(R.id.submit_layout);
    }

    private void firebaseInitializations() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        classDetails = reference.child("Attachments").child(detailsModel.getClassId()).child(detailsModel.getId());
        documents = classDetails.child("Works").child(detailsModel.getUserId()).child("Attachments");
        workStatus = classDetails.child("Works").child(detailsModel.getUserId()).child("Status");
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void setAttachmentsRv() {
        attachmentsRv.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new AttachmentViewAdapter();
        attachmentsRv.setAdapter(adapter);
        adapterWork();
    }

    private void adapterWork() {
        adapter.setAttachmentViewAdapter(uploadList, getContext());
        adapter.setOnItemClickListener(new AttachmentViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), DocumentViewActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("UploadModel", uploadList.get(position));
                intent.putExtra("data", data);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            model = (NewStreamModel) bundle.getSerializable("Model");
            classroomModel = (NewClassroomModel) bundle.getSerializable("ClassroomModel");
            detailsModel = (CommentDetailsModel) bundle.getSerializable("DetailsModel");
        }
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select file"), FILE_UPLOAD_CODE);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void btnFunc() {
        setCardEnable();
        addWorkCard.setOnClickListener(view1 -> {
            chooseFile();
        });

        submitCard.setOnClickListener(view1 -> {
            String txt = submitTxt.getText().toString();
            setCardEnable();
            getReturnStatus(txt.toLowerCase());
        });
    }

    private void setCardEnable() {
        addWorkCard.setEnabled(false);
        submitCard.setEnabled(false);
    }

    private void uploadFile(String ClassId, String AssignmentId, String UserId, NewFileModel model) {
//        for (NewFileModel model : fileList) {
        if (model.getFileType() != null) {
            StorageReference reference = storageReference.child("Classrooms/" + ClassId + "/" + AssignmentId + "/" + "Works/" + UserId + "/" + System.currentTimeMillis() + "." + model.getFileType());
            reference.putFile(Uri.parse(model.getFilepath()))
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return reference.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "File uploaded", Toast.LENGTH_SHORT).show();
                            Uri url = task.getResult();
                            UploadFileModel fileModel = new UploadFileModel(model.getFileName(), url.toString());
                            String id = documents.push().getKey();
                            documents.child(id).setValue(fileModel);
                        }
                    });
        }
//        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
                if (due != null) {
                    dueDateTxt.setText("Due " + due);
                } else dueDateTxt.setText("No due");
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
                total = snapshot.getValue().toString();
                getWorkStatus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWorkStatus() {
        workStatus.child("Student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    WorkStatusModel model1 = snapshot.getValue(WorkStatusModel.class);
                    submitStatus = model1.getSubmitStatus();
                    returnMarks = model1.getReturnMarks();
                    returnStatus = "" + model1.getReturnStatus();
                    timestamp = model1.getTimestamp();
                    returnTimestamp = model1.getReturnTimestamp();
                    handedIn = model1.isHandedIn();
                    if (!handedIn) {
                        if (due != null) {
                            String currentTimestamp = getTimestamp();
                            int val = currentTimestamp.compareTo(due);
                            if (val <= 0) {
                                Toast.makeText(getContext(), "1u1", Toast.LENGTH_SHORT).show();
                                setLayout(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, returnMarks + "/" + total, "","Not handed in", "Resubmit", greyColor, true, true);
                            } else {
                                Toast.makeText(getContext(), "2u2", Toast.LENGTH_SHORT).show();
                                setLayout(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, returnMarks + "/" + total, "","Not handed in", "Resubmit", greyColor, true, true);
                            }
                        } else {
                            Toast.makeText(getContext(), "3u3", Toast.LENGTH_SHORT).show();
                            setLayout(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, returnMarks + "/" + total, "","Not handed in", "Resubmit", greyColor, true, true);
                        }

                    } else {
                        if (submitStatus.equals("hand in") && returnMarks == null) {
                            if (due != null) {
                                int val = timestamp.compareTo(due);
                                if (val <= 0) {
                                    Toast.makeText(getContext(), "h1", Toast.LENGTH_SHORT).show();
                                    setLayout(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE, "0/" + total, "Handed in", "","Unsubmit", greyColor, false, true);
                                } else {
                                    Toast.makeText(getContext(), "h2", Toast.LENGTH_SHORT).show();
                                    setLayout(View.GONE, View.GONE, View.VISIBLE, View.VISIBLE, View.GONE, "0/" + total, "Handed in", "Done late","Unsubmit", greyColor, false, true);
                                }
                            } else {
                                Toast.makeText(getContext(), "h3", Toast.LENGTH_SHORT).show();
                                setLayout(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE, "0/" + total, "Handed in", "","Unsubmit", greyColor, false, true);
                            }
                        } else if (submitStatus.equals("unsubmit")) {
                            if (returnStatus.equals("true")) {
                                if(timestamp == null) timestamp = returnTimestamp;
                                if (due != null) {
                                    int val = timestamp.compareTo(due);
                                    if (val <= 0) {
                                        Toast.makeText(getContext(), "1u", Toast.LENGTH_SHORT).show();
                                        setLayout(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, returnMarks + "/" + total, "", "","Resubmit", greyColor, true, true);
                                    } else {
                                        Toast.makeText(getContext(), "2u", Toast.LENGTH_SHORT).show();
                                        setLayout(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.VISIBLE, returnMarks + "/" + total, "", "Done late","Resubmit", greyColor, true, true);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "3u", Toast.LENGTH_SHORT).show();
                                    setLayout(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE, returnMarks + "/" + total, "", "","Resubmit", greyColor, true, true);
                                }
                            } else {
                                if (due != null) {
                                    int val = timestamp.compareTo(due);
                                    if (val <= 0) {
                                        Toast.makeText(getContext(), "u1", Toast.LENGTH_SHORT).show();
                                        setLayout(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.VISIBLE, "0/" + total, "", "","Hand in", greyColor, true, true);
                                    } else {
                                        Toast.makeText(getContext(), "u2", Toast.LENGTH_SHORT).show();
                                        setLayout(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, "0/" + total, "Missing","Done late", "Hand in", redColor, true, true);
                                    }
                                } else {
                                    Toast.makeText(getContext(), "u3", Toast.LENGTH_SHORT).show();
                                    setLayout(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.VISIBLE, "0/" + total, "", "","Hand in", greyColor, true, true);
                                }
                            }

                        } else if (submitStatus.equals("resubmit")) {
                            if (due != null) {
                                int reVal = returnTimestamp.compareTo(due);
                                if (reVal <= 0) {
                                    Toast.makeText(getContext(), "r1", Toast.LENGTH_SHORT).show();
                                    setLayout(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, returnMarks + "/" + total, "Handed in", "","Unsubmit", greyColor, false, true);
                                } else {
                                    Toast.makeText(getContext(), "r2", Toast.LENGTH_SHORT).show();
                                    setLayout(View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, returnMarks + "/" + total, "Handed in", "Done late","Unsubmit", greyColor, false, true);
                                }
                            } else {
                                Toast.makeText(getContext(), "r3", Toast.LENGTH_SHORT).show();
                                setLayout(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE, returnMarks + "/" + total, "Handed in", "","Unsubmit", greyColor, false, true);
                            }
                        }
                    }

                } else {
                    if (due != null) {
                        String timestamp = getTimestamp();
                        int val = timestamp.compareTo(due);
                        if (val <= 0) {
                            Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                            setLayout(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.VISIBLE, "0/" + total, "", "","Hand in", greyColor, true, true);
                        } else {
                            Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                            setLayout(View.VISIBLE, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE, "0/" + total, "Missing", "Done late","Hand in", redColor, true, true);
                        }
                    } else {
                        Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                        setLayout(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.VISIBLE, "0/" + total, "", "","Hand in", greyColor, true, true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLayout(int ddtV, int mtV, int sstV, int ssdtV, int awcV, String mtT, String sstT, String ssdtT, String stT, int sstC, boolean awcE, boolean scE) {
        dueDateTxt.setVisibility(ddtV);
        marksTxt.setVisibility(mtV);
        marksTxt.setText(mtT);
        submitStatusTxt.setVisibility(sstV);
        submitStatusTxt.setText(sstT);
        submitStatusTxt.setTextColor(sstC);
        submitStatusDescTxt.setText(ssdtT);
        submitStatusDescTxt.setVisibility(ssdtV);
        addWorkCard.setEnabled(awcE);
        addWorkCard.setVisibility(awcV);
        submitCard.setEnabled(scE);
        submitCard.setVisibility(View.VISIBLE);
        submitTxt.setText(stT);
    }

    private void getReturnStatus(String txt) {
        workStatus.child("Student").child("returnStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    returnStatus = snapshot.getValue().toString().toLowerCase();
                } else {
                    returnStatus = "false";
                }
                setSubmit(txt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSubmit(String txt) {
        WorkStatusModel model1;
        String Timestamp = getTimestamp();
        if (returnStatus.equals("true")) {
            model1 = new WorkStatusModel(txt, returnMarks, timestamp, Timestamp, true, true);
            workStatus.child("Teacher").child("submitStatus").setValue(txt);
            workStatus.child("Teacher").child("timestamp").setValue(timestamp);
            workStatus.child("Teacher").child("returnTimestamp").setValue(Timestamp);
            workStatus.child("Teacher").child("handedIn").setValue(true);
        } else {
            model1 = new WorkStatusModel(txt, returnMarks, Timestamp, null, false, true);
            workStatus.child("Teacher").child("submitStatus").setValue(txt);
            workStatus.child("Teacher").child("timestamp").setValue(Timestamp);
            workStatus.child("Teacher").child("returnTimestamp").setValue(null);
            workStatus.child("Teacher").child("handedIn").setValue(true);
        }
        Map<String, Object> map1 = objectMapper.convertValue(model1, Map.class);
        workStatus.child("Student").updateChildren(map1);
    }

    private String getTimestamp() {
        String Date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String Time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String Timestamp = Date + " " + Time;
        return Timestamp;
    }
}