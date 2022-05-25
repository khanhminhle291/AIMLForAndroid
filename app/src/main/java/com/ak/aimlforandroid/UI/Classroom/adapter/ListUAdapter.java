package com.ak.aimlforandroid.UI.Classroom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.databinding.ItemUBinding;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ListUAdapter extends RecyclerView.Adapter {
    private ItemUBinding binding;
    private Context context;
    private ArrayList<User> users;
    public ListUAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding =ItemUBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ItemHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User x = users.get(position);
        binding.ten.setText(x.getTen());
        binding.msv.setText(String.valueOf(x.getId()));
        binding.ngaysinh.setText(x.getNgaySinh());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
