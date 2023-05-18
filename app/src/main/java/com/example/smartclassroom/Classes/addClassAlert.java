package com.example.smartclassroom.Classes;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.smartclassroom.R;

public class addClassAlert extends AppCompatDialogFragment {

    private TextView create,join;
    private dialogListener addListener;
    private AlertDialog.Builder builder;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.add_class_layout,null);
        initialisations(view);
        createAction();
        joinAction();
        builder.setView(view);
        return builder.create();
    }

    private void initialisations(View view){
        create=view.findViewById(R.id.tvCreate);
        join=view.findViewById(R.id.tvJoin);
    }

    private void createAction(){
        create.setOnClickListener(view -> {
            addListener.setText("CreateClass");
            addClassAlert.this.getDialog().cancel();
        });
    }

    private void joinAction(){
        join.setOnClickListener(view -> {
            addListener.setText("JoinClass");
            addClassAlert.this.getDialog().cancel();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addListener=(dialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement dialog Listener");
        }
    }

    public interface dialogListener{
        void setText(String string);
    }
}
