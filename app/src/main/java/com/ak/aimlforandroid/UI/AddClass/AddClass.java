package com.ak.aimlforandroid.UI.AddClass;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ak.aimlforandroid.UI.Models.Classroom;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.Untils.Untils;
import com.ak.aimlforandroid.databinding.ActivityAddClassBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

public class AddClass extends AppCompatActivity {

    private EditText ten, phan, phong, chuDe;
    private Button createBT;
    private ActivityAddClassBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        unitUI();
        prepareEvent();
        clickEvent();
    }

    private void unitUI() {
        ten = binding.className;
        phan = binding.phan;
        phong = binding.phong;
        chuDe = binding.chude;
        createBT = binding.create;
    }

    private void prepareEvent() {
        ten.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==0){
                    createBT.setEnabled(false);
                }else createBT.setEnabled(true);
            }
        });
    }

    private void clickEvent() {
        binding.closeActi.setOnClickListener(v -> {
            onBackPressed();
        });
        createBT.setOnClickListener(v ->{
            String classroomId = Untils.getSaltString(8);
            Classroom classroom = new Classroom(classroomId,textFromEDT(ten),textFromEDT(phan),textFromEDT(phong),textFromEDT(chuDe),Constants.AUTH.getCurrentUser().getUid());
            //
            Constants.CLASSROOM_DB.child(classroomId).setValue(classroom)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).child(Constants.CLASS_LIST)
                                            .child(classroom.getId()).setValue(classroom.getTen());
                                    Snackbar.make(v,classroomId,Snackbar.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(v,e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                }
                            });
        });
    }

    private String textFromEDT(EditText edt){
        return edt.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}