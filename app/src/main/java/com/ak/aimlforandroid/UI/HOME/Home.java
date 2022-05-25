package com.ak.aimlforandroid.UI.HOME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ak.aimlforandroid.DataRecive;
import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.UI.AddClass.AddClass;
import com.ak.aimlforandroid.UI.Classroom.ClassroomDetail;
import com.ak.aimlforandroid.UI.HOME.Adapter.CLassroomAdapter;
import com.ak.aimlforandroid.UI.JoinClassroom.JoinClassroom;
import com.ak.aimlforandroid.UI.Models.Classroom;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.UI.PROFILE.PROFILE;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.Untils.DbUntils;
import com.ak.aimlforandroid.Untils.Untils;
import com.ak.aimlforandroid.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private TextView username;
    private ImageView imageProfile;
    private FloatingActionButton fbtAdd;
    private BottomSheetDialog bottomSheet;
    private RecyclerView recyclerView;
    private ActivityHomeBinding binding;
    private User user;
    private CLassroomAdapter adapter;
    private ArrayList<Classroom> classroomArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        unitUI();
        clickEvent();

    }

    private void clickEvent() {
        fbtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

        adapter.setItemOnClick(new CLassroomAdapter.ItemOclick() {
            @Override
            public void onCick(Classroom classroom) {
                Intent intent = new Intent(Home.this, ClassroomDetail.class);
                intent.putExtra("ClassID",classroom.getId());
                startActivity(intent);
            }
        });

        imageProfile.setOnClickListener(v->{
            startActivity(new Intent(Home.this, PROFILE.class));
        });
    }

    private void showBottomSheet(){
        bottomSheet.setContentView(R.layout.bottom_sheet);
        bottomSheet.show();
       //
        LinearLayout addClass = bottomSheet.findViewById(R.id.add_class);
        LinearLayout joinClass = bottomSheet.findViewById(R.id.join_class);
       //
        if (user.getLoaiTaiKhoan().equals("Student")){
            addClass.setVisibility(View.GONE);
        }
        //click event
        addClass.setOnClickListener(v -> {
            startActivity(new Intent(this, AddClass.class));
            bottomSheet.hide();
        });
        joinClass.setOnClickListener(v->{
            startActivity(new Intent(this, JoinClassroom.class));
            bottomSheet.hide();
        });
    }

    private void unitUI() {
        classroomArrayList = new ArrayList<>();
        //classroomArrayList.add(new Classroom("ajda","adklad","adad","awdad","daw","adwd"));
        username = binding.userName;
        imageProfile = binding.imageProfile;
        fbtAdd = binding.fltAdd;
        //
        recyclerView = binding.rcvListClass;
        adapter = new CLassroomAdapter(classroomArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        bottomSheet = new BottomSheetDialog(this);

        //
        Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                        username.setText(user.getTen());
                        fbtAdd.setVisibility(View.VISIBLE);
                        Untils.setImage(user.getUrlAnhDaiDien(),imageProfile);
                    }
                });

        //debug test

    }

    @Override
    protected void onResume() {
        super.onResume();
        DbUntils.getListClass(new DataRecive.Classroom.List(){

            @Override
            public void Classroom(ArrayList<Classroom> classrooms) {
                classroomArrayList.clear();
                classroomArrayList.addAll(classrooms);
                adapter.notifyDataSetChanged();
            }
        });
    }
}