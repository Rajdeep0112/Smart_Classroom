package com.example.smartclassroom.RoomFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartclassroom.ClassroomAdapter;
import com.example.smartclassroom.R;

public class StreamFragment extends Fragment {


    private String ClassName,Section;
    private TextView className,section;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_stream, container, false);
        initialisation(view);
        Bundle bundle=this.getArguments();
        if(bundle!=null) {
            ClassName = bundle.getString("ClassName");
            Section = bundle.getString("Section");
        }

        className.setText(ClassName);
        section.setText(Section);
        return view;
    }

    private void initialisation(View view){
        className=view.findViewById(R.id.room_name);
        section=view.findViewById(R.id.room_section);
    }
}