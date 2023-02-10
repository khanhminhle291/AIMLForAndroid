package com.ak.aimlforandroid.UI.PROFILE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityUserInforBinding;
import com.google.android.material.textfield.TextInputEditText;

public class UserInfor extends AppCompatActivity {

    private String uid ="";
    private TextInputEditText ten, mssv, lop;
    private ActivityUserInforBinding binding;
    User u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInforBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uid = getIntent().getStringExtra("id");
        ten = binding.tenUserif;
        mssv = binding.mssvUserif;
        lop = binding.lopUserif;

        if (uid!=null || !uid.isEmpty()){
            Constants.USER_DB.child(uid).get()
                    .addOnSuccessListener(dataSnapshot -> {
                        u = dataSnapshot.getValue(User.class);
                        if (u!=null){
                            ten.setText(u.getTen());
                            lop.setText(u.getLop());
                            mssv.setText(u.getId() + "");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }

        binding.updateBT.setOnClickListener(v -> {
            if (u==null)
                return;
            u.setTen(ten.getText().toString().trim());
            // Mã số sinh viên không thể sửa
            u.setLop(lop.getText().toString().trim());
            Constants.USER_DB.child(uid).setValue(u)
                    .addOnSuccessListener(unused ->{
                        Toast.makeText(this, "Thay đổi thành công !", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }
}