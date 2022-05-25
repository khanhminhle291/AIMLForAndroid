package com.ak.aimlforandroid.UI.Classroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ak.aimlforandroid.R;
import com.ak.aimlforandroid.UI.Attendance.Attendance;
import com.ak.aimlforandroid.UI.Classroom.CreateAttendanceRoom.CreateAttendance;

import com.ak.aimlforandroid.UI.Classroom.ui.home.HomeFragment;
import com.ak.aimlforandroid.UI.JoinClassroom.JoinClassroom;
import com.ak.aimlforandroid.UI.Models.AttendanceRoom;
import com.ak.aimlforandroid.UI.Models.Classroom;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityClassroomDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
public class ClassroomDetail extends AppCompatActivity {
    private ActivityClassroomDetailBinding binding;
    private BottomSheetDialog bottomSheet;
    private String classID;
    private AttendanceRoom attendanceRoom;
    private DatabaseReference data;
    private final int FACE_R = 1;
    private boolean atten = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classID = getIntent().getStringExtra("ClassID");
        binding = ActivityClassroomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle bundle = new Bundle();
        bundle.putString("ClassID",classID);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_classroom_detail);
        navController.navigate(R.id.navigation_home,bundle);
        NavigationUI.setupWithNavController(binding.navView, navController);

        unitUI();
        cickEvent();
        run();
    }

    private void run() {
        if (classID!=null){
            data = Constants.CLASSROOM_DB.child(classID).child(Constants.ATTEN_ROOM);
            data.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    attendanceRoom = snapshot.getValue(AttendanceRoom.class);
                    if (binding != null)
                        if (attendanceRoom != null) {
                            Constants.CLASSROOM_DB.child(classID).child(Constants.ATTENDANCED).child(attendanceRoom.getNgay()).child("STUDENT_LIST").child(Constants.AUTH.getCurrentUser().getUid())
                                    .get().addOnSuccessListener(dataSnapshot -> {
                                        if (dataSnapshot.getValue(User.class)!=null)
                                            atten = true;
                                        else atten = false;

                                    });
                            binding.cardV.setVisibility(View.VISIBLE);
                            binding.className.setText(attendanceRoom.getLop());
                            binding.time.setText(attendanceRoom.getTime());
                        } else
                            binding.cardV.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        Constants.CLASSROOM_DB.child(classID).get()
                .addOnSuccessListener(dataSnapshot -> {
                   Classroom cl = dataSnapshot.getValue(Classroom.class);
                   if (cl!=null){
                       binding.title.setText(cl.getTen()+" - "+cl.getId());
                       if (cl.getUid_nguoiTao().equals(Constants.AUTH.getCurrentUser().getUid())){
                           binding.addBT.setVisibility(View.VISIBLE);
                       }else {
                           binding.addBT.setVisibility(View.GONE);
                       }
                   }
                });

    }

    private void unitUI() {
        bottomSheet = new BottomSheetDialog(this);
    }

    private void cickEvent() {

        binding.closeActi.setOnClickListener(v -> {
            finish();
        });

        binding.addBT.setOnClickListener(v -> {
            showBottomSheet();
        });

        binding.cardV.setOnClickListener(v -> {
            if (atten){
                Toast.makeText(this, getString(R.string.attendanced), Toast.LENGTH_SHORT).show();
            }else {
                startActivityIfNeeded(new Intent(ClassroomDetail.this, Attendance.class),FACE_R);
            }
        });
    }

    private void showBottomSheet(){
        bottomSheet.setContentView(R.layout.classroom_bottom_sheet);
        bottomSheet.show();
        //
        LinearLayout attendance = bottomSheet.findViewById(R.id.attendance);
        LinearLayout addPost = bottomSheet.findViewById(R.id.add_post);
        LinearLayout his = bottomSheet.findViewById(R.id.att_his);
        //
//        if (user.getLoaiTaiKhoan().equals("Student")){
//            addClass.setVisibility(View.GONE);
//        }
        //click event
        attendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateAttendance.class);
            intent.putExtra("ClassID",classID);
            startActivity(intent);
            bottomSheet.hide();
        });

        addPost.setOnClickListener(v->{
            Intent intent = new Intent(this, AddPost.class);
            intent.putExtra("classID",classID);
            startActivity(intent);
            bottomSheet.hide();
        });

        his.setOnClickListener(v -> {
            Intent intent = new Intent(this,AttendancedList.class);
            intent.putExtra("classID",classID);
            startActivity(intent);
            bottomSheet.hide();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this,""+requestCode+"/"+resultCode,Toast.LENGTH_SHORT).show();
        if (requestCode== FACE_R && resultCode == RESULT_OK){
            Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).get()
                            .addOnSuccessListener(dataSnapshot -> {
                                User user = dataSnapshot.getValue(User.class);
                                if (user!=null){
                                    Constants.CLASSROOM_DB.child(classID).child(Constants.ATTENDANCED).child(attendanceRoom.getNgay()).child("STUDENT_LIST").child(Constants.AUTH.getCurrentUser().getUid())
                                            .setValue(user);
                                    atten = true;
                                    Toast.makeText(this, getString(R.string.attendance_successful), Toast.LENGTH_SHORT).show();
                                }
                            });
        }else {
            atten = false;
        }
    }

    public String getClassID(){return this.classID;}
}