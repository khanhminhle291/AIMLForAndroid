package com.ak.aimlforandroid.UI.Classroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.ak.aimlforandroid.UI.Classroom.adapter.ListUAdapter;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityStudentListBinding;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class StudentList extends AppCompatActivity {

    private ActivityStudentListBinding binding;
    private RecyclerView recyclerView;
    private ListUAdapter adapter;
    private String classID, name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        classID = getIntent().getStringExtra("classID");
        name = getIntent().getStringExtra("name");
        recyclerView = binding.rcv;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Constants.CLASSROOM_DB.child(classID).child(Constants.ATTENDANCED).child(name).child("STUDENT_LIST").get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete()){
                        ArrayList<User> users = new ArrayList<>();
                        for (DataSnapshot data : task.getResult().getChildren()){
                            users.add(data.getValue(User.class));
                        }
                        adapter = new ListUAdapter(StudentList.this, users);
                        recyclerView.setAdapter(adapter);
                    }
                });

        binding.closeActi.setOnClickListener(v -> {
            finish();
        });
    }
}