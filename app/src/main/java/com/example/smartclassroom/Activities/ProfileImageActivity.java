package com.example.smartclassroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smartclassroom.Models.NewFileModel;
import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class ProfileImageActivity extends AppCompatActivity {

    private static final int IMAGE_UPLOAD_CODE = 200;
    private ImageView profileImg;
    private Toolbar profileToolbar;
    private TextView imageTxt, done, cancel;
    private String profileUrl = "";
    private FirebaseDatabase database;
    private DatabaseReference reference, profile;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private Uri uri;
    private Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_UPLOAD_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageTxt.setVisibility(View.GONE);
                profileImg.setImageBitmap(bitmap);
                done.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) chooseImage();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_toolbar_menu, menu);
        menu.removeItem(R.id.profile);
        menu.removeItem(R.id.refresh);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_image);
        initialisations();
        configureToolbar();
        extractData();

        done.setOnClickListener(view -> {
            uploadPhoto();
        });

        cancel.setOnClickListener(view -> {
            extractData();
        });
    }

    private void initialisations() {
        profileImg = findViewById(R.id.profile_img);
        profileToolbar = findViewById(R.id.profile_toolbar);
        imageTxt = findViewById(R.id.image_txt);
        done = findViewById(R.id.done);
        cancel = findViewById(R.id.cancel);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        profile = reference.child("Profiles").child(auth.getCurrentUser().getUid());
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void configureToolbar() {
        profileToolbar.setTitle("Profile photo");
        setSupportActionBar(profileToolbar);
        profileToolbar.setNavigationIcon(R.drawable.back);
        profileToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void extractData() {
        done.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        profile.child("profileUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    profileUrl = snapshot.getValue().toString();
                    imageTxt.setVisibility(View.GONE);
                    Glide.with(getApplicationContext()).load(profileUrl).into(profileImg);

                } else {
                    profileUrl = "";
                    profileImg.setImageDrawable(null);
                    imageTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select file"), IMAGE_UPLOAD_CODE);
    }

    private void uploadPhoto() {
        StorageReference reference = storageReference.child("Profiles/" + auth.getCurrentUser().getUid() + "/" + "Photo" + "." + getFileExtension(uri));
        reference.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        Uri url = task.getResult();
                        profile.child("profileUrl").setValue(url.toString());
                        extractData();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

//    private Uri getImageUri(Context context, Bitmap bitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
//        return Uri.parse(path);
//    }

//    public static Bitmap drawableToBitmap (Drawable drawable) {
//        Bitmap bitmap = null;
//
//        if (drawable instanceof BitmapDrawable) {
//            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//            if(bitmapDrawable.getBitmap() != null) {
//                return bitmapDrawable.getBitmap();
//            }
//        }
//
//        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
//            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
//        } else {
//            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        }
//
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//        drawable.draw(canvas);
//        return bitmap;
//    }
}