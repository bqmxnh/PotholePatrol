package com.example.potholepatrol.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;

public class FragmentSetting extends Fragment {
    private int originalStatusBarColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Lưu màu thanh trạng thái gốc
        Window window = requireActivity().getWindow();
        originalStatusBarColor = window.getStatusBarColor();

        // Làm cho thanh trạng thái trong suốt
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);

        return inflater.inflate(R.layout.activity_setting, container, false);
    }
    // Ví dụ Setting -> Dashboard thi no se doi lai mau xanh
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Khôi phục màu thanh trạng thái ban đầu
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(originalStatusBarColor);
    }
}
