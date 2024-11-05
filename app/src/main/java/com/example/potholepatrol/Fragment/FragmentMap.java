package com.example.potholepatrol.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.potholepatrol.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class FragmentMap extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map, container, false);

        // Sự kiện nhấn cho icon_search
        view.findViewById(R.id.icon_search).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.map_search);

            // Xử lý nút Confirm trong map_search
            LinearLayout btnConfirm = bottomSheetDialog.findViewById(R.id.btn_confirm);
            if (btnConfirm != null) {
                btnConfirm.setOnClickListener(confirmView -> {
                    bottomSheetDialog.dismiss(); // Đóng map_search

                    // Hiển thị BottomSheetDialog mới cho map_list
                    BottomSheetDialog mapListDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
                    mapListDialog.setContentView(R.layout.map_list);
                    mapListDialog.show();
                });
            }

            bottomSheetDialog.show();
        });

        // Sự kiện nhấn cho icon_report
        view.findViewById(R.id.icon_report).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
            bottomSheetDialog.setContentView(R.layout.map_report);

            // Xử lý nút Upload trong map_report
            LinearLayout btnUpload = bottomSheetDialog.findViewById(R.id.btn_upload_image);
            if (btnUpload != null) {
                btnUpload.setOnClickListener(uploadView -> {
                    bottomSheetDialog.dismiss(); // Đóng map_report

                    // Hiển thị BottomSheetDialog mới cho map_upload_image
                    BottomSheetDialog uploadDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
                    uploadDialog.setContentView(R.layout.map_upload_image);
                    uploadDialog.show();
                });
            }

            bottomSheetDialog.show();
        });

        return view;
    }
}
