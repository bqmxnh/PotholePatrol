package com.example.potholepatrol.api;

import com.example.potholepatrol.model.DashboardStatsResponse;
import com.example.potholepatrol.model.ForgetPasswordRequest;
import com.example.potholepatrol.model.ForgetPasswordResponse;
import com.example.potholepatrol.model.LoginRequest;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.model.NewPasswordRequest;
import com.example.potholepatrol.model.NewPasswordResponse;
import com.example.potholepatrol.model.PotholeRequest;
import com.example.potholepatrol.model.RegisterRequest;
import com.example.potholepatrol.model.RegisterResponse;
import com.example.potholepatrol.model.UserProfileResponse;
import com.example.potholepatrol.model.VerifyOTPRequest;
import com.example.potholepatrol.model.VerifyOTPResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.HTTP;


public interface AuthService {

    //Log In
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    //Log Out
    @HTTP(method = "DELETE", path = "auth/logout", hasBody = true)
    Call<Void> logout(@Body Map<String, String> body);

    // Register
    @Headers("Content-Type: application/json")
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    // Forget Password
    @Headers("Content-Type: application/json")
    @POST("auth/forgot-password")
    Call<ForgetPasswordResponse> forgetPassword(@Body ForgetPasswordRequest forgetPasswordRequest);

    // Verify OTP
    @Headers("Content-Type: application/json")
    @POST("auth/verify-otp")
    Call<VerifyOTPResponse> verifyOTP(@Body VerifyOTPRequest verifyOTPRequest);

    // Reset Password
    @Headers("Content-Type: application/json")
    @POST("auth/reset-password")
    Call<NewPasswordResponse> newPassword(@Body NewPasswordRequest newPasswordRequest);

    // Log In with Google
    @Headers("Content-Type: application/json")
    @POST("auth/google")
    Call<LoginResponse> googleSignIn(@Body Map<String, String> body);

    @Headers("Content-Type: application/json")
    @POST("map/addPothole")
    Call<Void> addPothole(
            @Header("Authorization") String token,
            @Body PotholeRequest potholeRequest
    );

    @GET("user/me")
    Call<UserProfileResponse> getUserProfile(@Header("Authorization") String token);

    @PATCH("user/settings")
    Call<Map<String, Object>> updateUserProfile(
            @Header("Authorization") String token,
            @Body Map<String, Object> fields
    );

    @GET("dashboard/stats")
    Call<DashboardStatsResponse> getDashboardStats(@Header("Authorization") String token);




}
