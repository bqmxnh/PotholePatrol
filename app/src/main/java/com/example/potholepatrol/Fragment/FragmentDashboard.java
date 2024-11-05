package com.example.potholepatrol.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.potholepatrol.R;

public class FragmentDashboard extends Fragment {

    private TextView textUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho FragmentDashboard
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        // Thay đổi màu thanh trạng thái
        Window window = requireActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard));
        window.setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.status_bar_dashboard));

        // Khởi tạo textUsername
        textUsername = view.findViewById(R.id.text_username);

        // Lấy email từ SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String email = preferences.getString("user_email", "User");

        // Hiển thị email trong text_username
        textUsername.setText(email);

        return view;
    }
}
