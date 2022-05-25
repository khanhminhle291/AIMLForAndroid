package com.ak.aimlforandroid.UI.Classroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityAttendancedListBinding;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class AttendancedList extends AppCompatActivity {

    private ActivityAttendancedListBinding binding;
    private String classID;
    private ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendancedListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        classID = getIntent().getStringExtra("classID");
        if (classID!=null){
            Constants.CLASSROOM_DB.child(classID).child("ten").get()
                            .addOnSuccessListener(dataSnapshot -> {
                                binding.title.setText(dataSnapshot.getValue(String.class));
                            });
            Constants.CLASSROOM_DB.child(classID).child(Constants.ATTENDANCED).get()
                    .addOnCompleteListener(task -> {
                       if (task.isComplete()){
                           ArrayList<String> item = new ArrayList<>();
                           for (DataSnapshot x : task.getResult().getChildren()){
                               item.add(x.getKey());
                           }
                           adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,item);
                           binding.listAtt.setAdapter(adapter);
                       }
                    });
        }

        binding.closeActi.setOnClickListener(v -> {
            finish();
        });

        binding.listAtt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) binding.listAtt.getItemAtPosition(position);
                Intent intent = new Intent(AttendancedList.this, StudentList.class);
                intent.putExtra("classID",classID);
                intent.putExtra("name",name);
                startActivity(intent);
            }
        });

    }
}