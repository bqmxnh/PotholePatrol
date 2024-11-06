package com.example.potholepatrol.api;

import com.example.potholepatrol.model.ForgetPasswordRequest;
import com.example.potholepatrol.model.ForgetPasswordResponse;
import com.example.potholepatrol.model.LoginRequest;
import com.example.potholepatrol.model.LoginResponse;
import com.example.potholepatrol.model.LogoutRequest;
import com.example.potholepatrol.model.LogoutResponse;
import com.example.potholepatrol.model.RegisterRequest;
import com.example.potholepatrol.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {

    //đăng nhập
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // đăng xuất
    @Headers("Content-Type: application/json")
    @DELETE("auth/logout")
    Call<LogoutResponse> logout(@Body LogoutRequest logoutRequest);

    // đăng kí
    @Headers("Content-Type: application/json")
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    // quên mk
    @Headers("Content-Type: application/json")
    @POST("auth/forgot-password")
    Call<ForgetPasswordResponse> forgetPassword(@Body ForgetPasswordRequest forgetPasswordRequest);

}
