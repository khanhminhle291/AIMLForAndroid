package com.ak.aimlforandroid.UI.ResetPassWord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityResetPassWordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class ResetPassWord extends AppCompatActivity {

    private ActivityResetPassWordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPassWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.conti.setOnClickListener(v -> {
            if (binding.edtTaikhoan.getText().toString().trim() == null){
                binding.edtTaikhoan.setError("Không được để trống");
            }else {
                Constants.AUTH.sendPasswordResetEmail(binding.edtTaikhoan.getText().toString().trim())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ResetPassWord.this, "Kiểm tra email để thiết lập lại mật khẩu", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ResetPassWord.this, "Không tìm thấy Email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}