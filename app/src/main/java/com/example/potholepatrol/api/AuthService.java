package com.example.potholepatrol.api;

import com.example.potholepatrol.model.DailyChartRequest;
import com.example.potholepatrol.model.DailyChartResponse;
import com.example.potholepatrol.model.DeleteNotificationRequest;
import com.example.potholepatrol.model.NotificationRequest;
import com.example.potholepatrol.model.NotificationResponse;
import com.example.potholepatrol.model.DashboardStatsResponse;
import com.example.potholepatrol.model.DistanceTraveledUpdateRequest;
import com.example.potholepatrol.model.DistanceTraveledUpdateResponse;
import com.example.potholepatrol.model.ForgetPasswordRequest;
import com.example.potholepatrol.model.ForgetPasswordResponse;
import com.example.potholepatrol.model.LoginRequest;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.model.NewPasswordRequest;
import com.example.potholepatrol.model.NewPasswordResponse;
import com.example.potholepatrol.model.PotholeRequest;
import com.example.potholepatrol.model.RegisterRequest;
import com.example.potholepatrol.model.RegisterResponse;
import com.example.potholepatrol.model.ReportRequest;
import com.example.potholepatrol.model.UserProfileResponse;
import com.example.potholepatrol.model.VerifyOTPRequest;
import com.example.potholepatrol.model.VerifyOTPResponse;
import com.example.potholepatrol.model.DistanceTraveledUpdateRequest;
import com.example.potholepatrol.model.DistanceTraveledUpdateResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.HTTP;
import retrofit2.http.Query;


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

    @PATCH("user/updateDistance")
    Call<DistanceTraveledUpdateResponse> updateDistanceTraveled(
            @Header("Authorization") String token,
            @Body DistanceTraveledUpdateRequest request);

    @GET("notification/getUserNotifications")
    Call<NotificationResponse> getUserNotifications(@Header("Authorization") String token);

    @POST("notification/create")
    Call<NotificationResponse> createNotification(
            @Header("Authorization") String token,
            @Body NotificationRequest request);

    @Headers("Content-Type: application/json")
    @PATCH("notification/markAsRead")
    Call<Void> markAsRead(
            @Header("Authorization") String token,
            @Body Map<String, String> notificationIds
    );

    @HTTP(method = "DELETE", path = "notification/delete", hasBody = true)
    Call<Void> deleteNotification(
            @Header("Authorization") String token,
            @Body DeleteNotificationRequest request
    );

    @POST("dashboard/dailyChart")
    Call<DailyChartResponse> getDailyChart(
            @Header("Authorization") String token,
            @Body DailyChartRequest body
    );

    @Headers("Content-Type: application/json")
    @POST("user/sendReport")
    Call<Void> sendReport(
            @Header("Authorization") String token,
            @Body ReportRequest reportRequest
    );













}
