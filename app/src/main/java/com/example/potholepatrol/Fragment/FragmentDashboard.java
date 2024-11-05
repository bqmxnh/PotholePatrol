package com.example.potholepatrol.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;

public class FragmentDashboard extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Thay đổi màu thanh trạng thái
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard)); // Thay your_color bằng màu bạn muốn
        window.setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard)); // Thay your_color bằng màu bạn muốn

        return inflater.inflate(R.layout.activity_dashboard, container, false);
    }
}
