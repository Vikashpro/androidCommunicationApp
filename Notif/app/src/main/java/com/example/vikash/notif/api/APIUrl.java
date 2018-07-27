package com.example.vikash.notif.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vikash on 3/28/18.
 */

public class APIUrl {
    public static final String BASE_URL = "http://192.168.0.111/fyp/public/";
    private static Retrofit retrofit = null;
    private static APIService service = null;
    public static APIService getClient(){

       if(retrofit == null) {
           Gson gson = new GsonBuilder()
                   .setLenient()
                   .create();

           Retrofit retrofit = new Retrofit.Builder()
                   .baseUrl(APIUrl.BASE_URL)
                   .addConverterFactory(GsonConverterFactory.create(gson))
                   .build();
           service = retrofit.create(APIService.class);
       }
       return service;
    }

}
