package com.ak.aimlforandroid.UI;

import static com.ak.aimlforandroid.Untils.Untils.getCropBitmapByCPU;
import static com.ak.aimlforandroid.Untils.Untils.rotateBitmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.ak.aimlforandroid.UI.AddFaceData.AddFaceData;
import com.ak.aimlforandroid.UI.Attendance.Attendance;
import com.ak.aimlforandroid.Untils.Constants;
import com.ak.aimlforandroid.Untils.FaceData;
import com.ak.aimlforandroid.Untils.FaceModel;
import com.ak.aimlforandroid.Untils.FaceUntils;
import com.ak.aimlforandroid.Untils.Untils;
import com.ak.aimlforandroid.databinding.ActivityDiemdanhtestBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;


import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;


public class diemdanhtest extends AppCompatActivity {

    private final String PATH = "mobile_face_net.tflite";
    private ImageView  facePRV;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    private FaceDetector faceDetector;
    private Interpreter tflite;
    private InputImage inputImage;
    private Bitmap bitmap;
    private ActivityDiemdanhtestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiemdanhtestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uinitUI();
        // load model
        try {
            tflite = new Interpreter(Untils.loadModelFile(this,PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();
        faceDetector = FaceDetection.getClient(highAccuracyOpts);

        cameraBing();

    }

    private void uinitUI() {
        previewView = binding.preview;
        facePRV = binding.facePRV;
    }

    private void cameraBing() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProviderListenableFuture =ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(()->{
            try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    cameraProvider.unbindAll();
                    CameraSelector cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build();

                @SuppressLint("RestrictedApi") ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setCameraSelector(cameraSelector)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(new Executor() {
                    @Override
                    public void execute(Runnable command) {
                        command.run();
                    }
                }, new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = image.getImage();

                        if (mediaImage!=null){
                            bitmap = FaceModel.rotateBitmap(toBitmap(mediaImage),90f);
                            //inputImage = InputImage.fromMediaImage(mediaImage,image.getImageInfo().getRotationDegrees());
                            faceDetector.process(InputImage.fromBitmap(bitmap,0))
                                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                                        @Override
                                        public void onSuccess(List<Face> faces) {
                                            if (faces.size()!=0){
                                                Bitmap xas = Bitmap.createBitmap(bitmap);
                                                Constants.USER_DB.child(Constants.AUTH.getCurrentUser().getUid()).child("face_data")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                if (task.isComplete()){
                                                                    ArrayList<Float> da = new ArrayList<>();
                                                                    for (DataSnapshot dt : task.getResult().getChildren()){
                                                                        da.add(dt.getValue(Float.class));
                                                                    }



                                                                    Bitmap crop = FaceModel.cropRectFromBitmap(bitmap,faces.get(0).getBoundingBox(),false);
                                                                    binding.facePRV.setImageBitmap(crop);
                                                                    FaceModel faceNetModel = new FaceModel(diemdanhtest.this);
                                                                    float[] em = faceNetModel.getFaceEmbedding(xas,faces.get(0).getBoundingBox(),false);
                                                                    float x;
                                                                    if ((x = FaceModel.match(em,Untils.fromArray(da)))<1.0f){
                                                                        binding.result.setText("dung"+String.format("%.10f",x)+" /"+em[10]+"/"+Untils.fromArray(da)[10]);
                                                                    }else {
                                                                        binding.result.setText("sai"+String.format("%.10f",x));
                                                                    }
                                                                }
                                                            }
                                                        });
                                                //recognizeImage(cropFace);
                                            }
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                        @Override
                                        public void onComplete(@NonNull Task<List<Face>> task) {
                                            image.close();
                                        }
                                    });
                        }


                    }
                });
                cameraProvider.bindToLifecycle((LifecycleOwner) this,cameraSelector,preview,imageAnalysis);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }, ContextCompat.getMainExecutor(this));
    }
    private Bitmap toBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
//    public void recognizeImage(final Bitmap bitmap) {
//
//        // set Face to Preview
//        face_preview.setImageBitmap(bitmap);
//
//        //Create ByteBuffer to store normalized image
//
//        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
//
//        imgData.order(ByteOrder.nativeOrder());
//
//        intValues = new int[inputSize * inputSize];
//
//        //get pixel values from Bitmap to normalize
//        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        imgData.rewind();
//
//        for (int i = 0; i < inputSize; ++i) {
//            for (int j = 0; j < inputSize; ++j) {
//                int pixelValue = intValues[i * inputSize + j];
//                if (isModelQuantized) {
//                    // Quantized model
//                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
//                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
//                    imgData.put((byte) (pixelValue & 0xFF));
//                } else { // Float model
//                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//
//                }
//            }
//        }
//        //imgData is input to our model
//        Object[] inputArray = {imgData};
//
//        Map<Integer, Object> outputMap = new HashMap<>();
//
//
//        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable
//
//        outputMap.put(0, embeedings);
//
//        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model
//
//
//
//        float distance_local = Float.MAX_VALUE;
//        String id = "0";
//        String label = "?";
//
//        //Compare new face with saved Faces.
//        if (registered.size() > 0) {
//
//            final List<Pair<String, Float>> nearest = findNearest(embeedings[0]);//Find 2 closest matching face
//
//            if (nearest.get(0) != null) {
//
//                final String name = nearest.get(0).first; //get name and distance of closest matching face
//                // label = name;
//                distance_local = nearest.get(0).second;
//                if (developerMode)
//                {
//                    if(distance_local<distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                        reco_name.setText("Nearest: "+name +"\nDist: "+ String.format("%.3f",distance_local)+"\n2nd Nearest: "+nearest.get(1).first +"\nDist: "+ String.format("%.3f",nearest.get(1).second));
//                    else
//                        reco_name.setText("Unknown "+"\nDist: "+String.format("%.3f",distance_local)+"\nNearest: "+name +"\nDist: "+ String.format("%.3f",distance_local)+"\n2nd Nearest: "+nearest.get(1).first +"\nDist: "+ String.format("%.3f",nearest.get(1).second));
//
////                    System.out.println("nearest: " + name + " - distance: " + distance_local);
//                }
//                else
//                {
//                    if(distance_local<distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
//                        reco_name.setText(name);
//                    else
//                        reco_name.setText("Unknown");
////                    System.out.println("nearest: " + name + " - distance: " + distance_local);
//                }
//
//
//
//            }
//        }
//
//
////            final int numDetectionsOutput = 1;
////            final ArrayList<SimilarityClassifier.Recognition> recognitions = new ArrayList<>(numDetectionsOutput);
////            SimilarityClassifier.Recognition rec = new SimilarityClassifier.Recognition(
////                    id,
////                    label,
////                    distance);
////
////            recognitions.add( rec );
//
//    }
}