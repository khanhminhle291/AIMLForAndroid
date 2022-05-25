package com.ak.aimlforandroid.Untils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.Pair;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.common.ops.NormalizeOp;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaceModel {
    private Context context;
    private Interpreter interpreter;
    private int imgSize = 112;
    private ImageProcessor imageTensorProcessor = new ImageProcessor.Builder()
            .add(new ResizeOp( imgSize , imgSize , ResizeOp.ResizeMethod.BILINEAR ))
            .add(new NormalizeOp( 127.5f , 127.5f ))
            .build();
    public FaceModel(Context context){
        this.context = context;
        Interpreter.Options interpreterOptions = new Interpreter.Options().setNumThreads(4);
        try {
            interpreter = new Interpreter(FileUtil.loadMappedFile(context,"mobile_face_net.tflite"),interpreterOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FaceModel() {

    }

    public float[] getFaceEmbedding (Bitmap image, Rect crop, boolean preRotate){
        return runFaceNet(
                convertBitmapToBuffer(
                        cropRectFromBitmap(image,crop,preRotate)
                )
        );
    }

    public static float match(float[] emb1, float[] emb2) {
        float distance = 0;
        for (int i = 0; i < emb1.length; i++) {
            float diff = emb1[i] - emb2[i];
            distance += diff*diff;
        }
        distance = (float) Math.sqrt(distance);
        return distance;

    }



    public float[] getFaceEmbeddingWithoutBBox (Bitmap image){
        return runFaceNet(
                convertBitmapToBuffer(image)
        );
    }


    public static Bitmap cropRectFromBitmap(Bitmap source, Rect rect, boolean preRotate) throws IllegalArgumentException {
        int width = rect.width();
        int height = rect.height();
        if ( (rect.left + width) > source.getWidth() ){
            width = source.getWidth() - rect.left;
        }
        if ( (rect.top + height ) > source.getHeight() ){
            height = source.getHeight() - rect.top;
        }
        return Bitmap.createBitmap(preRotate ? rotateBitmap(source,90f) : source,rect.left,rect.top,width,height);

    }

    public static Bitmap rotateBitmap(Bitmap source, Float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate( angle );
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix , false );
    }

    public float[] createFaceData(Bitmap src){

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

        interpreter.run(imgData, embeedings);

        return embeedings[0];
    }

    private float[] runFaceNet(Object input){
//        //long t1 = System.currentTimeMillis();

        float[][] embeedings = new float[1][192];
        interpreter.run(input,embeedings);
        return embeedings[0];
    }

    private ByteBuffer convertBitmapToBuffer(Bitmap image) {
        TensorImage imageTensor = imageTensorProcessor.process(TensorImage.fromBitmap(image));
        return imageTensor.getBuffer();
    }

}
