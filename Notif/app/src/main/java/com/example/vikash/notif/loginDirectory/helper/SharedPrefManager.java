package com.example.vikash.notif.loginDirectory.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.vikash.notif.loginDirectory.model.User;

import java.util.List;

/**
 * Created by vikash on 3/30/18.
 */

public class SharedPrefManager {
    private static SharedPrefManager Instance;
    private static Context context;
    private static final String SHARED_PREF_NAME = "usersharedpref";

    private static final String KEY_USER_CMS_ID = "keyuserid";
    private static final String KEY_USER_EMAIL = "keyuseremail";
    private static final String KEY_USER_NAME = "keyusername";
    private static final String PROFILE_PIC_URL = "profilepicurl";

    private SharedPrefManager(Context context){
        this.context = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context){
        if(Instance == null){
            Instance = new SharedPrefManager(context);
        }
        return  Instance;
    }
    public boolean userLogin(List<User> user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        User us = user.get(0);
        editor.putString(KEY_USER_CMS_ID,us.getCmsId());
        editor.putString(KEY_USER_EMAIL,us.getEmail());
        editor.putString(KEY_USER_NAME,us.getName());
        editor.putString(PROFILE_PIC_URL,us.getImage());
        editor.apply();
        return true;

    }
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USER_EMAIL, null) != null)
            return true;
        return false;
    }
    public void setImageUrl(String image_url){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_PIC_URL,image_url);
        editor.apply();
    }
    public String getImageUrl(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(PROFILE_PIC_URL,null);
    }
    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_CMS_ID,null),
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(PROFILE_PIC_URL, null)
        );
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

}
