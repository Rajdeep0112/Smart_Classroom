package com.example.smartclassroom.Classes;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;

public class CommonFuncClass implements Serializable {

    private Context context;

    public CommonFuncClass(Context context) {
        this.context = context;
    }

    public void toastShort(String string){
        if(context!=null){
            Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
        }
    }
}
