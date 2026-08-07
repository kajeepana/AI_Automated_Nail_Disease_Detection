package com.example.aiautomatednaildiseasedetection.api;

import com.example.aiautomatednaildiseasedetection.model.Feedback;
import com.example.aiautomatednaildiseasedetection.model.Upload;
import com.example.aiautomatednaildiseasedetection.model.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {


    @POST("api/users/register")
    Call<User> registerUser(@Body User user);


    @POST("api/users/login")
    Call<User> loginUser(@Body User user);


    @POST("api/users/profile")
    Call<User> updateProfile(@Body User user);


    @POST("api/uploads")
    Call<Upload> saveUpload(@Body Upload upload);


    @POST("api/feedback")
    Call<Feedback> saveFeedback(@Body Feedback feedback);


    @Multipart
    @POST("api/uploads/upload")
    Call<String> uploadImage(
            @Part MultipartBody.Part file,
            @Part("email") RequestBody email
    );

}