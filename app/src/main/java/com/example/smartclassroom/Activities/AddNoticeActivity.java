package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.Adapters.SelectedAttachmentAdapter;
import com.example.smartclassroom.Models.NewClassroomModel;
import com.example.smartclassroom.Models.NewFileModel;
import com.example.smartclassroom.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.ArrayList;

public class AddNoticeActivity extends AppCompatActivity{


    private static final int FILE_UPLOAD_CODE = 200;
    private Toolbar addNoticeToolbar;
    private EditText noticeShare;
    private TextView attachments;
    private RecyclerView recyclerView;
    private SelectedAttachmentAdapter adapter;
    private ArrayList<NewFileModel> fileList = new ArrayList<>();
    private Uri uri;
    private StorageReference storageReference;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILE_UPLOAD_CODE && resultCode==RESULT_OK && data!=null){
            uri = data.getData();
            NewFileModel model = new NewFileModel(uri.toString(),getFileName(uri),getContentResolver().getType(uri));
            fileList.add(model);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice);

        initializations();
        setSupportActionBar(addNoticeToolbar);
        configureToolbar();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SelectedAttachmentAdapter(fileList,this);
        recyclerView.setAdapter(adapter);

        attachments.setOnClickListener(view -> {
            chooseFile();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add_notice_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.post:
//                uploadFile();
                createNotice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initializations(){
        addNoticeToolbar=findViewById(R.id.add_notice_toolbar);
        noticeShare=findViewById(R.id.notice_share);
        recyclerView = findViewById(R.id.attachments_recycler_view);
        attachments = findViewById(R.id.attachments);
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void configureToolbar(){
        addNoticeToolbar.setTitle("");
        addNoticeToolbar.setNavigationIcon(R.drawable.close);
        addNoticeToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    public void createNotice(){
        String NoticeShare=noticeShare.getText().toString().trim();

        Intent i=new Intent();
        i.putExtra("NoticeShare",NoticeShare);
        Bundle data = new Bundle();
        data.putSerializable("FileModel",fileList);
        i.putExtra("data",data);
        setResult(RESULT_OK,i);
        finish();
    }

    private void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent,"Select file"),FILE_UPLOAD_CODE);
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
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
}