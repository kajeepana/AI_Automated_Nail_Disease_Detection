package com.example.aiautomatednaildiseasedetection.api;

import com.example.aiautomatednaildiseasedetection.model.Profile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/profile/save")
    Call<String> saveProfile(@Body Profile profile);

}