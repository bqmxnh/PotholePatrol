package com.example.potholepatrol.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.potholepatrol.Adapter.LocationAdapter;
import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.ArrayList;
import java.util.List;

public class LocationFragment extends Fragment {

    private ImageView btnBack, ivCurrentLocation, ivSearch;
    private TextView tvTitle, tvCurrentLocation;
    private CardView cvCurrentLocation, cvSearch;
    private LinearLayout layoutCurrentLocation, layoutSearch;
    private EditText etSearch;
    private Button btnSearch;
    private ListView lvLocationResults;
    private LocationAdapter locationAdapter;

    private List<NominatimResult> locationList;

    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_location, container, false);

        // Bind the UI elements to the views
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
        btnSearch = rootView.findViewById(R.id.btnSearch);
        lvLocationResults = rootView.findViewById(R.id.lvLocationResults);

        // Khởi tạo adapter mới
        locationList = new ArrayList<>();
        locationAdapter = new LocationAdapter(getContext(), locationList);
        lvLocationResults.setAdapter(locationAdapter);

        // Back button event
        btnBack.setOnClickListener(v -> {
            if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getActivity().getSupportFragmentManager().popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        // Search button event
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchLocation(query);  // Call the search function
            } else {
                Toast.makeText(getContext(), "Please enter a location", Toast.LENGTH_SHORT).show();  // Show message if no input
            }
        });



        // ListView item click event
        lvLocationResults.setOnItemClickListener((parent, view, position, id) -> {
            NominatimResult selectedResult = locationList.get(position); // Lấy địa điểm được chọn

            // Tạo dialog với layout tùy chỉnh
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_route_confirmation, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            // Loại bỏ viền vuông mặc định
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
            // Gán dữ liệu vào layout
            TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
            TextView tvLocationName = dialogView.findViewById(R.id.tvLocationName);
            Button btnYes = dialogView.findViewById(R.id.btnYes);
            Button btnNo = dialogView.findViewById(R.id.btnNo);

            tvTitle.setText("Route to this location?");
            tvLocationName.setText(selectedResult.getDisplay_name());

            // Xử lý nút Yes
            btnYes.setOnClickListener(v -> {
                // Lưu dữ liệu vào SharedPreferences
                SharedPreferences preferences = getContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("lat", selectedResult.getLat());
                editor.putString("lon", selectedResult.getLon());
                // Thêm flag để chỉ định rằng nên vẽ route
                editor.putBoolean("shouldDrawRoute", true);
                // Thêm flag mới để chỉ định rằng nên hiện navigation panel
                editor.putBoolean("showNavigationPanel", true);
                editor.apply();

                dialog.dismiss();
                getActivity().getSupportFragmentManager().popBackStack();
            });

            // Xử lý nút No
            btnNo.setOnClickListener(v -> dialog.dismiss());

            // Hiển thị dialog
            dialog.show();
        });


        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(App.wrap(context));
    }

    // Search location function
    private void searchLocation(String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NominatimAPI api = retrofit.create(NominatimAPI.class);

        // Send search request
        Call<List<NominatimResult>> call = api.searchLocation(query, "json");
        call.enqueue(new Callback<List<NominatimResult>>() {
            @Override
            public void onResponse(Call<List<NominatimResult>> call, Response<List<NominatimResult>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NominatimResult> results = response.body();

                    if (!results.isEmpty()) {
                        locationList.clear();
                        int maxResults = Math.min(10, results.size());
                        locationList.addAll(results.subList(0, maxResults));
                        locationAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<List<NominatimResult>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Nominatim API interface
    public interface NominatimAPI {
        @GET("search")
        Call<List<NominatimResult>> searchLocation(@Query("q") String query, @Query("format") String format);
    }

    // NominatimResult class
    public static class NominatimResult {
        private String display_name;
        private String lat;
        private String lon;

        public String getDisplay_name() {
            return display_name;
        }

        public String getLat() {
            return lat;
        }

        public String getLon() {
            return lon;
        }

        public double getLatAsDouble() {
            return Double.parseDouble(lat);
        }

        public double getLonAsDouble() {
            return Double.parseDouble(lon);
        }
    }

}



