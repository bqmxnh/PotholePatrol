package com.example.potholepatrol.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.formatter.ValueFormatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;


import java.util.ArrayList;

public class ActivityDashboardChart extends AppCompatActivity {
    private static final String TAG = "ActivityDashboardChart";

    private ImageView backButton;
    private TextView lowNumberText, mediumNumberText, highNumberText;
    private PieChart pieChart;
    ArrayList<Integer> colors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_chart);

        // Cấu hình thanh trạng thái và thanh điều hướng
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.status_bar_dashboard));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.status_bar_dashboard));

        // Ánh xạ các thành phần giao diện
        backButton = findViewById(R.id.ic_back);
        lowNumberText = findViewById(R.id.low_number);
        mediumNumberText = findViewById(R.id.medium_number);
        highNumberText = findViewById(R.id.high_number);
        pieChart = findViewById(R.id.pieChart);

        // Kiểm tra PieChart
        if (pieChart == null) {
            Log.e(TAG, "PieChart not found in layout.");
            return;
        } else {
            Log.d(TAG, "PieChart successfully initialized.");
        }

        setupClickListeners();
        updateSeverityDataFromSharedPreferences();
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
    }

    private void updateSeverityDataFromSharedPreferences() {
        // Lấy dữ liệu từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("DashboardStats", MODE_PRIVATE);
        int lowSeverity = sharedPreferences.getInt("LowSeverity", 0);
        int mediumSeverity = sharedPreferences.getInt("MediumSeverity", 0);
        int highSeverity = sharedPreferences.getInt("HighSeverity", 0);

        // Log dữ liệu nhận được
        Log.d(TAG, "LowSeverity: " + lowSeverity);
        Log.d(TAG, "MediumSeverity: " + mediumSeverity);
        Log.d(TAG, "HighSeverity: " + highSeverity);

        // Gán giá trị vào các TextView tương ứng
        lowNumberText.setText(String.valueOf(lowSeverity));
        mediumNumberText.setText(String.valueOf(mediumSeverity));
        highNumberText.setText(String.valueOf(highSeverity));

        // Vẽ biểu đồ hình tròn
        drawPieChart(lowSeverity, mediumSeverity, highSeverity);
    }

    private void drawPieChart(int lowSeverity, int mediumSeverity, int highSeverity) {
        if (lowSeverity <= 0 && mediumSeverity <= 0 && highSeverity <= 0) {
            pieChart.clear();
            pieChart.setNoDataText("No data available.");
            return;
        }

        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        if (lowSeverity > 0) {
            pieEntries.add(new PieEntry(lowSeverity, "Low"));
            colors.add(Color.parseColor("#FA0066"));
        }
        if (mediumSeverity > 0) {
            pieEntries.add(new PieEntry(mediumSeverity, "Medium"));
            colors.add(Color.parseColor("#72FF72"));
        }
        if (highSeverity > 0) {
            pieEntries.add(new PieEntry(highSeverity, "High"));
            colors.add(Color.parseColor("#35D0FC"));
        }


        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(18f);
        pieDataSet.setValueTextColor(Color.BLACK);

        //định dạng giá trị hiển thị trong PieChart
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); //chuyển đổi giá trị thành số nguyên không dấu
            }
        });


        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);


        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription(null);

        // Cấu hình legend
        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setForm(Legend.LegendForm.CIRCLE);  // hình dạng của chú thích
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);  // Đặt legend theo chiều dọc
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setTextSize(14f);  // chữ của legend
        legend.setFormSize(10f);  // hình tròn trong legend

        pieChart.invalidate();
    }


}