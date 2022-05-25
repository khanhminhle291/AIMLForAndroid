package com.ak.aimlforandroid.Untils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ak.aimlforandroid.DataRecive;
import com.ak.aimlforandroid.UI.HOME.Adapter.CLassroomAdapter;
import com.ak.aimlforandroid.UI.Models.Classroom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Random;

public class Untils {
    public static String getSaltString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static ArrayList<Float> toArray(float[] em){
        ArrayList<Float> floats = new ArrayList<>();
        for (float x : em)
            floats.add(x);
        return floats;
    }

    public static float[] fromArray(ArrayList<Float> em){
        float[] fl = new float[em.size()];
        for (int i =0; i<em.size(); i++)
            fl[i] = em.get(i);
        return fl;
    }


    public static void saveData(FaceData faceData) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), ".AIMLForAndroid");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "data.txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("hello\n");
            for (int i=0; i<1; i++){
                for (int j=0; j<192; j++){
                    writer.append(String.valueOf(faceData.getFace()[i][j])+" ");
                }
                writer.append("\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        if (source != null && !source.isRecycled()) {
            Canvas cavas = new Canvas(resultBitmap);
            // draw background
            Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
            paint.setColor(Color.WHITE);
            cavas.drawRect(
                    new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                    paint);

            Matrix matrix = new Matrix();
            matrix.postTranslate(-cropRectF.left, -cropRectF.top);
            cavas.drawBitmap(source, matrix, paint);
            source.recycle();
        }

        return resultBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        // Recycle the old bitmap if it has changed.
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

    public static MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public static void setImage(String url, ImageView target){
        Picasso.get()
                .load(url == null ? "android.resource://com.ak.aimlforandroid/drawable/profile" : url)
                .into(target);
    }



}


