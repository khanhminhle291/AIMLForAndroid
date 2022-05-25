package com.ak.aimlforandroid;

import androidx.annotation.NonNull;

import com.ak.aimlforandroid.UI.Models.Classroom;

import java.util.ArrayList;

public interface DataRecive {
    interface Classroom{
        interface List{
            void Classroom(ArrayList<com.ak.aimlforandroid.UI.Models.Classroom> classrooms);
        }
        interface One{
            void Classroom(com.ak.aimlforandroid.UI.Models.Classroom classroom);
        }
    }

}
