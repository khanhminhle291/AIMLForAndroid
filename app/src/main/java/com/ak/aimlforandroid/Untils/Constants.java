package com.ak.aimlforandroid.Untils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {

    public static final int STUDENT = 0;
    public static final int TEACHER = 1;

    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance("https://aiml-96dbb-default-rtdb.asia-southeast1.firebasedatabase.app/");
    public static final String CLASS_LIST = "CLASS_LIST";
    public static final String ATTEN_ROOM = "ATTENDANCE";
    public static final String ATTENDANCED = "ATTENDANCED";
    public static final DatabaseReference USER_DB = DATABASE.getReference("USER");
    public static final DatabaseReference CLASSROOM_DB = DATABASE.getReference("CLASSROOM");
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
