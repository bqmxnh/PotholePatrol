package com.example.potholepatrol.Dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.potholepatrol.model.StatisticsData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.List;

public class ActivityDashboardChart extends AppCompatActivity {
    private static final String TAG = "ActivityDashboardChart";

    private ImageView backButton;
    private TextView lowNumberText, mediumNumberText, highNumberText;
    private PieChart pieChart;
    private BarChart barChart;
    ArrayList<Integer> colors = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_chart);
        ArrayList<StatisticsData> data = getIntent().getParcelableArrayListExtra("data");

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
        barChart = findViewById(R.id.barChart);

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
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
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



        // Gán giá trị vào các TextView tương ứng
        lowNumberText.setText(String.valueOf(lowSeverity));
        mediumNumberText.setText(String.valueOf(mediumSeverity));
        highNumberText.setText(String.valueOf(highSeverity));

        // Vẽ biểu đồ hình tròn
        drawPieChart(lowSeverity, mediumSeverity, highSeverity);
        List<StatisticsData> chartData = getChartData();
        renderBarChart(chartData);


    }


    private List<StatisticsData> getChartData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        List<StatisticsData> chartData = new ArrayList<>();

        // Giả sử bạn lưu tối đa 5 giá trị DailyChart
        for (int i = 1; i <= 5; i++) {
            String chartKey = "DailyChart" + i;
            String chartValue = sharedPreferences.getString(chartKey, null);  // Lấy giá trị từ SharedPreferences

            if (chartValue != null) {
                // Phân tách chuỗi và lấy thông tin ngày và tổng
                String[] parts = chartValue.split(", ");  // Phân tách theo dấu ", "
                String date = parts[0].replace("Date: ", "");  // Lấy phần date
                int total = Integer.parseInt(parts[1].replace("Total: ", ""));  // Lấy phần total và chuyển sang int

                chartData.add(new StatisticsData(date, total));  // Thêm đối tượng StatisticsData vào danh sách
            }
        }
        return chartData;  // Trả về danh sách dữ liệu
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


    private void renderBarChart(List<StatisticsData> data) {
        if (data == null || data.isEmpty()) {
            Log.d("BarChart", "No data available to render.");
            return;
        }

        BarChart barChart = findViewById(R.id.barChart);
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        // Loop through the data to create BarEntry and labels for the X-axis
        for (int i = 0; i < data.size(); i++) {
            StatisticsData stat = data.get(i);
            Log.d("BarChart", "Date: " + stat.getDate() + ", Total: " + stat.getTotal());  // Debug log
            entries.add(new BarEntry(i, stat.getTotal())); // i is the X-axis position
            labels.add(stat.getDate()); // Add the date to labels for X-axis
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Total");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.6f);  // độ rộng của mỗi cột
        barChart.setData(barData);


        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        // trục x(total)
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextSize(10f);

        //trục y (ngày)
        barChart.getAxisLeft().setGranularity(1f);
        barChart.getAxisLeft().setTextSize(14f);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisRight().setTextSize(14f);
        barDataSet.setValueTextSize(10f);


        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format("%d", (int) value);
            }
        });


        barChart.setFitBars(true);

        barChart.invalidate();
    }



}