package com.ak.aimlforandroid.UI.PROFILE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.ak.aimlforandroid.SpLash;
import com.ak.aimlforandroid.UI.AddFaceData.AddFaceData;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityProfileBinding;


public class PROFILE extends AppCompatActivity {

    private ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        unitUI();
        clickEvent();
    }

    private void unitUI() {
        Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).get()
                .addOnSuccessListener(dataSnapshot -> {
                    User user = dataSnapshot.getValue(User.class);
                    if (user!=null){
                        binding.name.setText(user.getTen());
                    }
                });
    }

    private void clickEvent() {
        binding.logout.setOnClickListener(v -> {
            Constants.AUTH.signOut();
            startActivity(new Intent(this, SpLash.class));
            finishAffinity();
        });
        binding.addface.setOnClickListener(v -> {
            startActivity(new Intent(this, AddFaceData.class));
        });
    }
}