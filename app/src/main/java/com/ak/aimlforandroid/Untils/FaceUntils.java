package com.ak.aimlforandroid.Untils;


import static com.ak.aimlforandroid.Untils.Untils.getCropBitmapByCPU;
import static com.ak.aimlforandroid.Untils.Untils.getResizedBitmap;
import static com.ak.aimlforandroid.Untils.Untils.rotateBitmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceUntils {

    private FaceDetector faceDetector;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    boolean isModelQuantized=false;
    int inputSize=112;
    private static HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>();

    public FaceUntils(FaceDetectorOptions faceDetectorOptions) {
        this.faceDetector = FaceDetection.getClient(faceDetectorOptions);
    }

    public FaceUntils() {
    }

    public void FaceDetector(Bitmap src, FaceDetectorEvent faceDetectorEvent){
        this.faceDetector
                .process(InputImage.fromBitmap(src, 0))
                .addOnSuccessListener(faces -> {
                    faceDetectorEvent.OnSuccess(faces);
                })
                .addOnFailureListener(e -> {
                    faceDetectorEvent.OnFailure(e);
                })
                .addOnCompleteListener(task -> {
                    faceDetectorEvent.onComplete(task);
                });
    }

    public static FaceData createFaceData(Bitmap src, Interpreter tfLite){

        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * 112 * 112 * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        int[] intValues = new int[112 * 112];

        //get pixel values from Bitmap to normalize
        try {
            src.getPixels(intValues, 0, src.getWidth(), 0, 0, src.getWidth(), src.getHeight());
        }catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException){
            Log.e("exp","loi");
        }
        imgData.rewind();

        for (int i = 0; i < 112; ++i) {
            for (int j = 0; j < 112; ++j) {
                int pixelValue = intValues[i * 112 + j];
                //if (false) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
//                } else { // Float model
//                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//
//                }
            }
        }
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();

        float[][] embeedings = new float[1][192]; //output of model will be stored in this variable



        outputMap.put(0, embeedings);

        tfLite.runForMultipleInputsOutputs(inputArray, outputMap);
        Log.e("embeedings",""+outputMap.size());
        return new FaceData(embeedings);
    }

    public static boolean recognizeImage(final FaceData faceData, final  Bitmap src2, Activity activity) throws IOException {

        SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                "0", "", -1f);
        result.setExtra(faceData.getFace());

        registered.put("",result);

        float[][] embeedings =FaceUntils.createFaceData(src2, new Interpreter(Untils.loadModelFile(activity, "mobile_face_net.tflite"))).getFace();

        float distance_local = Float.MAX_VALUE;
        String id = "0";
        String label = "?";

        //Compare new face with saved Faces.
        if (registered.size() > 0) {

            final List<Pair<String, Float>> nearest = findNearest(embeedings[0]);//Find 2 closest matching face

            if (nearest.get(0) != null) {

                final String name = nearest.get(0).first; //get name and distance of closest matching face
                // label = name;
                distance_local = nearest.get(0).second;

                    if(distance_local<1.0f)
                        return true;
            }
        }

        return false;

    }

    private static List<Pair<String, Float>> findNearest(float[] emb) {
        List<Pair<String, Float>> neighbour_list = new ArrayList<Pair<String, Float>>();
        Pair<String, Float> ret = null; //to get closest match
        Pair<String, Float> prev_ret = null; //to get second closest match
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
        {

            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];

            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                prev_ret=ret;
                ret = new Pair<>(name, distance);
            }
        }
        if(prev_ret==null) prev_ret=ret;
        neighbour_list.add(ret);
        neighbour_list.add(prev_ret);

        return neighbour_list;

    }

    public static Bitmap croppedFace(Bitmap src, Face face, int rotationDegrees){
        //Adjust orientation of Face
        Bitmap frame_bmp1 = rotateBitmap(src, rotationDegrees, false, false);

        //Get bounding box of face
        RectF boundingBox = new RectF(face.getBoundingBox());
        //Crop out bounding box from whole Bitmap(image)
        Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
        return getResizedBitmap(cropped_face, 112, 112);
    }

    public interface FaceDetectorEvent{
        public void OnSuccess(List<Face> faces);
        public void OnFailure(Exception e);
        public void onComplete(Task<List<Face>> task);
    }

}
