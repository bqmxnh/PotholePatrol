package com.example.potholepatrol.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private ArrayAdapter<NominatimResult> locationAdapter;
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

        // Initialize the location list and adapter
        locationList = new ArrayList<>();
        locationAdapter = new ArrayAdapter<NominatimResult>(getContext(), android.R.layout.simple_list_item_1, locationList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                NominatimResult result = getItem(position);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                TextView textView = convertView.findViewById(android.R.id.text1);
                textView.setText(result.getDisplay_name());

                return convertView;
            }
        };
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
            NominatimResult selectedResult = locationList.get(position); // Get the NominatimResult object

            // Show confirmation dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Routing")
                    .setMessage("Do you want to route to this location: " + selectedResult.getDisplay_name() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Close the current fragment
                        getActivity().getSupportFragmentManager().popBackStack();

                        // Save lat and lon to SharedPreferences
                        SharedPreferences preferences = getContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("lat", selectedResult.getLat());  // Lưu lat dưới dạng String
                        editor.putString("lon", selectedResult.getLon());  // Lưu lon dưới dạng String
                        editor.apply();


                        // Go back to the previous fragment
                        getActivity().getSupportFragmentManager().popBackStack();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return rootView;
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
                        locationList.clear();  // Clear previous list
                        int maxResults = Math.min(5, results.size());  // Limit to 5 results
                        for (int i = 0; i < maxResults; i++) {
                            NominatimResult result = results.get(i);
                            // In ra lat và lon để kiểm tra
                            Log.d("Location", "Lat: " + result.getLat() + ", Lon: " + result.getLon());
                            locationList.add(result);
                        }
                        locationAdapter.notifyDataSetChanged();  // Update the ListView
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



