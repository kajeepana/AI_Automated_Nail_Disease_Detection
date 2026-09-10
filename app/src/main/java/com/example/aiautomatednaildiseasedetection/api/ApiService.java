package com.example.aiautomatednaildiseasedetection.api;

import com.example.aiautomatednaildiseasedetection.dto.ForgotPasswordRequest;
import com.example.aiautomatednaildiseasedetection.dto.ResetPasswordRequest;
import com.example.aiautomatednaildiseasedetection.dto.VerifyOtpRequest;
import com.example.aiautomatednaildiseasedetection.model.Feedback;
import com.example.aiautomatednaildiseasedetection.model.LoginRequest;
import com.example.aiautomatednaildiseasedetection.model.NailAnalysis;
import com.example.aiautomatednaildiseasedetection.model.Upload;
import com.example.aiautomatednaildiseasedetection.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // =====================================================
    // USER
    // =====================================================

    // REGISTER
    @POST("api/users/register")
    Call<User> registerUser(
            @Body User user
    );


    // LOGIN
    @POST("api/users/login")
    Call<User> loginUser(
            @Body LoginRequest loginRequest
    );


    // UPDATE PROFILE
    @POST("api/users/profile")
    Call<User> updateProfile(
            @Body User user
    );


    // =====================================================
    // REGISTRATION EMAIL OTP
    // =====================================================

    // VERIFY REGISTRATION OTP
    @POST("api/users/verify-otp")
    Call<String> verifyOtp(
            @Body VerifyOtpRequest request
    );


    // =====================================================
    // FORGOT PASSWORD
    // =====================================================

    // SEND FORGOT PASSWORD OTP
    @POST("api/users/forgot-password")
    Call<String> forgotPassword(
            @Body ForgotPasswordRequest request
    );


    // VERIFY FORGOT PASSWORD OTP
    // VERIFY FORGOT PASSWORD OTP
    @POST("api/users/verify-otp")
    Call<String> verifyForgotPasswordOtp(
            @Body VerifyOtpRequest request
    );

    // RESET PASSWORD
    @POST("api/users/reset-password")
    Call<String> resetPassword(
            @Body ResetPasswordRequest request
    );


    // =====================================================
    // UPLOAD
    // =====================================================

    // SAVE UPLOAD
    @POST("api/uploads")
    Call<Upload> saveUpload(
            @Body Upload upload
    );


    // GET UPLOADS BY EMAIL
    @GET("api/uploads/{email}")
    Call<List<Upload>> getUploads(
            @Path("email") String email
    );


    // UPLOAD NAIL IMAGE
    @Multipart
    @POST("api/uploads/upload")
    Call<NailAnalysis> uploadImage(
            @Part MultipartBody.Part file,
            @Part("email") RequestBody email
    );


    // =====================================================
    // FEEDBACK
    // =====================================================

    @POST("api/feedback")
    Call<Feedback> saveFeedback(
            @Body Feedback feedback
    );


    // =====================================================
    // ANALYSIS
    // =====================================================

    // SAVE ANALYSIS
    @POST("api/analysis")
    Call<NailAnalysis> saveAnalysis(
            @Body NailAnalysis analysis
    );


    // GET ANALYSIS BY ID
    @GET("api/analysis/{id}")
    Call<NailAnalysis> getAnalysisById(
            @Path("id") Long id
    );


    // GET ANALYSES BY EMAIL
    @GET("api/analysis/user/{email}")
    Call<List<NailAnalysis>> getAnalysesByEmail(
            @Path("email") String email
    );

}