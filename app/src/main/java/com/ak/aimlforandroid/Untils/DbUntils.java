package com.ak.aimlforandroid.Untils;

import androidx.annotation.NonNull;

import com.ak.aimlforandroid.DataRecive;
import com.ak.aimlforandroid.UI.Models.Classroom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class DbUntils {
    public static void getClassroom(String classID,DataRecive.Classroom.One data){
        Constants.CLASSROOM_DB.child(classID).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Classroom classroom = dataSnapshot.getValue(Classroom.class);
                        if (classroom!=null)
                            data.Classroom(classroom);
                    }
                });
    }
    public static void getListClass(DataRecive.Classroom.List dataRecive){
        ArrayList<Classroom> classrooms = new ArrayList<>();
        ArrayList<String> classId = new ArrayList<>();
        Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid())
                .child(Constants.CLASS_LIST)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isComplete()){
                            for (DataSnapshot data : task.getResult().getChildren()){
                                classId.add(data.getKey());
                            }
                            Constants.CLASSROOM_DB.get()
                                    .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (task.isComplete()){
                                                for (DataSnapshot dataSnapshot : task.getResult().getChildren()){
                                                    Classroom classroom = dataSnapshot.getValue(Classroom.class);
                                                    if (classId.contains(classroom.getId())){
                                                        classrooms.add(classroom);
                                                    }
                                                }
                                                dataRecive.Classroom(classrooms);
                                            }


                                        }
                                    });


                        }
                    }
                });
    }

}
