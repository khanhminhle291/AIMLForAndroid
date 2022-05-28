package com.ak.aimlforandroid.UI.Classroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.widget.Toast;

import com.ak.aimlforandroid.UI.Classroom.adapter.ListUAdapter;
import com.ak.aimlforandroid.UI.Models.User;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.databinding.ActivityStudentListBinding;
import com.google.firebase.database.DataSnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class StudentList extends AppCompatActivity {

    private ActivityStudentListBinding binding;
    private RecyclerView recyclerView;
    private ListUAdapter adapter;
    private String classID, name;
    private ArrayList<User> users;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private final int CREATE_FILE_CODE = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        classID = getIntent().getStringExtra("classID");
        name = getIntent().getStringExtra("name");
        users = new ArrayList<>();
        recyclerView = binding.rcv;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListUAdapter(StudentList.this, users);
        recyclerView.setAdapter(adapter);

        binding.title.setText(name);
        Constants.CLASSROOM_DB.child(classID).child(Constants.ATTENDANCED).child(name).child("STUDENT_LIST").get()
                .addOnCompleteListener(task -> {
                    if (task.isComplete()){
                        users.clear();
                        for (DataSnapshot data : task.getResult().getChildren()){
                            users.add(data.getValue(User.class));
                        }
                        binding.export.setEnabled(true);
                        adapter.notifyDataSetChanged();
                    }
                });

        binding.closeActi.setOnClickListener(v -> {
            finish();
        });

        binding.export.setOnClickListener(v->{
            if (checkPermission()){
                try {
                    exportDataToCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                requestPermission();
            }
        });
    }

    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else {
            return ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission(){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }


    public static String toCSVline(User user) {
        String result = "";

        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(user.getId())).append(",");
        sb.append(user.getTen()).append(",");
        sb.append(user.getEmail()).append(",");
        result = sb.deleteCharAt(sb.length() - 1).toString();

        return result+"\n";
    }

    String csvData;
    private void exportDataToCSV() throws IOException {
        csvData = "";
        for (User u : users) {
            csvData += toCSVline(u);

        }


//        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String uniqueFileName = name;
//        File file = new File(directory, uniqueFileName);
//        FileWriter fileWriter = new FileWriter(file);
//        fileWriter.write(csvData);
//        fileWriter.flush();
//        fileWriter.close();
//        Toast.makeText(this, "File Exported Successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xlsx");
        intent.putExtra(Intent.EXTRA_TITLE,name+".xlsx");
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,"");
        startActivityForResult(intent,CREATE_FILE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R && (requestCode==WRITE_EXTERNAL_STORAGE_REQUEST_CODE)){
            if (Environment.isExternalStorageManager()){
                try {
                    exportDataToCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "Chưa cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CREATE_FILE_CODE){
            if (resultCode == RESULT_OK){
                Uri uri = data.getData();
                WritableWorkbook workbook;
                WorkbookSettings settings = new WorkbookSettings();
                settings.setLocale(new Locale("vn","VN"));
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    workbook = Workbook.createWorkbook(outputStream,settings);
                    WritableSheet sheet = workbook.createSheet(name,0);
                    sheet.addCell(new Label(0,0,"Mã sinh viên"));
                    sheet.addCell(new Label(1,0,"Họ và Tên"));
                    sheet.addCell(new Label(2,0,"Email"));
                    for (int i = 0 ; i<users.size(); i++){
                        sheet.addCell(new Label(0,i+2,String.valueOf(users.get(i).getId())));
                        sheet.addCell(new Label(1,i+2,users.get(i).getTen()));
                        sheet.addCell(new Label(2,i+2,users.get(i).getEmail()));
                    }
                    workbook.write();
                    workbook.close();
                    Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lưu thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lưu thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (WriteException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Lưu thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
//                try {
//                    OutputStream outputStream = this.getContentResolver().openOutputStream(uri);
//                    outputStream.write(csvData.getBytes(StandardCharsets.UTF_8));
//                    outputStream.close();
//                    Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Lưu thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "Lưu thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
            }else {
                Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE){
            if ((grantResults[0]==PackageManager.PERMISSION_GRANTED)&&(grantResults[1]==PackageManager.PERMISSION_GRANTED)){
                try {
                    exportDataToCSV();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                Toast.makeText(this, "Chưa cấp quyền", Toast.LENGTH_SHORT).show();
            }
        }
    }
}