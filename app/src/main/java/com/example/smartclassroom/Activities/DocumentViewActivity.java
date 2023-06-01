package com.example.smartclassroom.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.smartclassroom.Models.UploadFileModel;
import com.example.smartclassroom.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URLEncoder;

public class DocumentViewActivity extends AppCompatActivity {

    private WebView webView;
    private Toolbar documentToolbar;
    private UploadFileModel model;

    @Override
    protected void onPause() {
        super.onPause();
        webView.destroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_view);
        initialisations();
        configureToolbar();
        getData();
        setData();
    }

    private void initialisations(){
        webView = findViewById(R.id.web_view);
        documentToolbar = findViewById(R.id.document_toolbar);
    }

    private void configureToolbar(){
        documentToolbar.setTitle("");
        setSupportActionBar(documentToolbar);
        documentToolbar.setNavigationIcon(R.drawable.back);
        documentToolbar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void getData(){
        Intent intent = getIntent();
        Bundle data=intent.getBundleExtra("data");
        model= (UploadFileModel) data.getSerializable("UploadModel");
    }

    private void setData(){
        setWebViewSettings();
        try {
            String encode_url= URLEncoder.encode(model.getUrl(),"UTF-8"); //Url Convert to UTF-8 It important.
            String gPdfViewer = "https://drive.google.com/viewerng/viewer?embedded=true&url=";
            String docViewer = "https://docs.google.com/gview?embedded=true&url=";
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(model.getUrl());
            ref.getMetadata().addOnSuccessListener(storageMetadata -> {
                String type = storageMetadata.getContentType();
                if(type.contains("application")) webView.loadUrl(docViewer+encode_url);
                else webView.loadUrl(model.getUrl());
            });
        } catch (Exception e) {
            Log.e("Exception",e.getMessage());
        }
    }

    private void setWebViewSettings(){
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
//        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("TAG", "error code " + errorCode);
            Log.e("TAG", "description " + description);
        }
    }
}