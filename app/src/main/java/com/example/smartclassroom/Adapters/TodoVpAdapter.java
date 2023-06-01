package com.example.smartclassroom.Adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartclassroom.Fragments.Todo.AssignedFragment;
import com.example.smartclassroom.Fragments.Todo.DoneFragment;
import com.example.smartclassroom.Fragments.Todo.MissingFragment;

import java.util.ArrayList;

public class TodoVpAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments=new ArrayList<>();
    private ArrayList<String> headers=new ArrayList<>();

    public TodoVpAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        initData();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        sendClassDetails(fragments.get(position),position);
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    private void initData(){
        addData(new AssignedFragment(),"Assigned");
        addData(new MissingFragment(),"Missing");
        addData(new DoneFragment(),"Done");
    }

    private void addData(Fragment fragment,String header){
        fragments.add(fragment);
        headers.add(header);
    }

    public String getHeader(int position){
        return headers.get(position);
    }

    private void sendClassDetails(Fragment fragment,int position){
        Bundle bundle=new Bundle();
        bundle.putString("Tag",headers.get(position));
        fragment.setArguments(bundle);
    }
}
