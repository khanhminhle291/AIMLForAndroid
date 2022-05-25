package com.ak.aimlforandroid.Untils;

import java.util.ArrayList;

public class FaceData {
    protected float[][] face;

    public FaceData() {
        this.face = new float[1][192];
    }

    public FaceData(float[][] face) {
        this.face = face;
    }

    public float[][] getFace() {
        return face;
    }

    public ArrayList<Float> toArrayList(){
        ArrayList<Float> arrayList = new ArrayList<>();
        for (int i=0; i<192; i++){
            arrayList.add(face[0][i]);
        }
        return arrayList;
    }

    public void fromArrayList(ArrayList<Float> faces){
        for (int i=0; i< 192; i++){
            face[0][i]=faces.get(i);
        }
    }

    public void setFace(float[][] face) {
        this.face = face;
    }
}
