package com.ak.aimlforandroid.UI.HOME.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.aimlforandroid.UI.Models.Classroom;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ItemsClassBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class CLassroomAdapter extends RecyclerView.Adapter<CLassroomAdapter.ItemHolder> {

    private ItemsClassBinding binding;
    private ArrayList<Classroom> classrooms;
    private ItemOclick itemOclick;

    public CLassroomAdapter(ArrayList<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ItemsClassBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ItemHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Classroom classroom = classrooms.get(position);
        binding.ten.setText(classroom.getTen());
        binding.phan.setText(classroom.getPhan());
        binding.phong.setText(classroom.getPhong().isEmpty() ? "" : "Phòng: "+classroom.getPhong());
        Constants.USER_DB.child(classroom.getUid_nguoiTao()).child("ten").get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        binding.gv.setText(dataSnapshot.getValue(String.class));
                    }
                });
    }
    public void setItemOnClick(ItemOclick itemOclick){
        this.itemOclick = itemOclick;
    }

    @Override
    public int getItemCount() {
        return classrooms.size();
    }

    protected class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            itemOclick.onCick(classrooms.get(getPosition()));
        }
    }

    public interface ItemOclick{
        public void onCick(Classroom classroom);
    }


}
