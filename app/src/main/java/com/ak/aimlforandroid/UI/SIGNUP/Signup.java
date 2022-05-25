package com.ak.aimlforandroid.UI.SIGNUP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.UI.HOME.Home;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;

public class Signup extends AppCompatActivity {

    private EditText email, password, name, className, id;
    private Spinner spinner;
    private ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //
        unitUI();
        clickEvent();

    }

    private void clickEvent() {
        binding.btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!invalidate()){
                    setCLickAble(false);
                    binding.progressLayout.setVisibility(View.VISIBLE);
                    User user = new User(Integer.parseInt(id.getText().toString().trim()),name.getText().toString().trim(),className.getText().toString().trim(),email.getText().toString().trim(),spinner.getSelectedItem().toString());
                    Constants.AUTH.createUserWithEmailAndPassword(user.getEmail(), password.getText().toString().trim())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Snackbar.make(v,getString(R.string.signup_successful),Snackbar.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Signup.this, Home.class));
                                                    finishAffinity();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    setCLickAble(true);
                                                    binding.progressLayout.setVisibility(View.GONE);
                                                    Snackbar.make(v,getString(R.string.signup_failure)+"\n"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    setCLickAble(true);
                                    binding.progressLayout.setVisibility(View.GONE);
                                    Snackbar.make(v,getString(R.string.signup_failure)+"\n"+e.getMessage(),Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void setCLickAble(boolean cLickAble){
        email.setEnabled(cLickAble);
        password.setEnabled(cLickAble);
        name.setEnabled(cLickAble);
        className.setEnabled(cLickAble);
        id.setEnabled(cLickAble);
        spinner.setEnabled(cLickAble);
        binding.btSignup.setEnabled(cLickAble);
    }

    private boolean invalidate() {
        boolean invalidate = false;
        if (email.getText().toString().isEmpty()){
            email.setError(getString(R.string.not_null));
            invalidate = true;
        }
        else {
            if (!Constants.validate(email.getText().toString().trim())){
                email.setError(getString(R.string.email_fomat_error));
                invalidate = true;
            }
        }
        if (password.getText().toString().isEmpty()){
            password.setError(getString(R.string.not_null));
            invalidate = true;
        }
        if (name.getText().toString().isEmpty()){
            name.setError(getString(R.string.not_null));
            invalidate = true;
        }
        if (className.getText().toString().isEmpty()){
            className.setError(getString(R.string.not_null));
            invalidate = true;
        }
        if (id.getText().toString().isEmpty()){
            id.setError(getString(R.string.not_null));
            invalidate = true;
        }
        return invalidate;
    }

    private void unitUI() {
        email = binding.edtTaikhoan;
        password = binding.edtPassword;
        name = binding.nameEdt;
        className = binding.classEdt;
        id = binding.idEdt;
        //
        spinner = binding.loaitaikhoan;
        String[] spinnerItem = {"Student","Teacher"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,spinnerItem);
        spinner.setAdapter(adapter);
        //
    }
}