package com.example.potholepatrol.Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import android.widget.LinearLayout;
import com.example.potholepatrol.R;

public class LocationFragment extends Fragment {

    // Khai báo các biến của các nút và TextView
    private ImageView btnBack, ivCurrentLocation, ivSearch;
    private TextView tvTitle, tvCurrentLocation;
    private CardView cvCurrentLocation, cvSearch;
    private LinearLayout layoutCurrentLocation, layoutSearch;
    private EditText etSearch;

    public LocationFragment() {
        // Required empty public constructor
    }

    // Khởi tạo LocationFragment với tham số
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        // Thêm tham số vào Bundle nếu cần
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nếu cần, lấy dữ liệu từ Bundle
        if (getArguments() != null) {
            String param1 = getArguments().getString("param1");
            String param2 = getArguments().getString("param2");
            // Xử lý tham số param1 và param2 nếu cần
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        // Ánh xạ các biến từ layout
        btnBack = rootView.findViewById(R.id.btnBack);
        tvTitle = rootView.findViewById(R.id.tvTitle);
        ivCurrentLocation = rootView.findViewById(R.id.ivCurrentLocation);
        tvCurrentLocation = rootView.findViewById(R.id.tvCurrentLocation);
        ivSearch = rootView.findViewById(R.id.ivSearch);
        etSearch = rootView.findViewById(R.id.tvSearch);
        cvCurrentLocation = rootView.findViewById(R.id.cvCurrentLocation);
        cvSearch = rootView.findViewById(R.id.cvSearch);
        layoutCurrentLocation = rootView.findViewById(R.id.layoutCurrentLocation);
        layoutSearch = rootView.findViewById(R.id.layoutSearch);

        // Bạn có thể thêm sự kiện click tại đây nếu cần
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    requireActivity().onBackPressed();
                }
            }
        });

        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return rootView;
    }
}
