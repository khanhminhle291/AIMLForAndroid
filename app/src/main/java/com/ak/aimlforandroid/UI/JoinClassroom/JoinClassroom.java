package com.ak.aimlforandroid.UI.JoinClassroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.UI.Models.Classroom;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityJoinClassroomBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class JoinClassroom extends AppCompatActivity {

    private ActivityJoinClassroomBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJoinClassroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prepareEvent();
        clickEvent();
    }

    String classID;
    private void clickEvent() {
        binding.create.setOnClickListener(v->{
            closeKeyboard();
            Constants.CLASSROOM_DB.child((classID = binding.idclassroom.getText().toString().trim()))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Classroom classroom = dataSnapshot.getValue(Classroom.class);
                            if (classroom!=null){
                                Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).child(Constants.CLASS_LIST)
                                        .child(classroom.getId()).setValue(classroom.getTen());
                                Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).get()
                                                .addOnSuccessListener(dataSnapshot1 -> {
                                                    User user = dataSnapshot1.getValue(User.class);
                                                    if (user!=null){
                                                        Constants.CLASSROOM_DB.child(classID).child("STUDENT_LIST").child(String.valueOf(user.getId())).setValue(user);
                                                    }
                                                });
                                finish();
                            }
                            else {
                                Snackbar.make(v,getString(R.string.class_is_not_exist),Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(v,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                        }
                    });
        });
        binding.closeActi.setOnClickListener(v->{
            onBackPressed();
        });

    }

    private void prepareEvent() {
        binding.idclassroom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==0){
                    binding.create.setEnabled(false);
                }else binding.create.setEnabled(true);
            }
        });
    }

    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}