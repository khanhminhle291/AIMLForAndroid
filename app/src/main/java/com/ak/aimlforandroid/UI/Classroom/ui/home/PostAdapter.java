package com.ak.aimlforandroid.UI.Classroom.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.UI.Models.Post;
import com.ak.aimlforandroid.databinding.PostItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter {

    private ArrayList<Post> posts;
    private PostItemBinding binding;
    private Context context;

    public PostAdapter(Context context, ArrayList<Post> posts) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PostItemBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ItemHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        binding.toppic.setText(post.getTopPic());
        binding.date.setText(post.getNgay());
        binding.content.setText(post.getNoiDung());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
