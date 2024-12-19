package com.example.potholepatrol.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.example.potholepatrol.Fragment.FragmentDashboard;
import com.example.potholepatrol.Fragment.FragmentMap;
import com.example.potholepatrol.Fragment.FragmentSetting;
import com.example.potholepatrol.Language.App;
import com.example.potholepatrol.R;


public class MainActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentDashboard fragmentDashboard;
    private FragmentMap fragmentMap;
    private FragmentSetting fragmentSetting;
    private ImageView icDashboard, icMap, icSetting;
    private TextView navDashboardTitle, navMapTitle, navSettingTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các Fragment
        fragmentDashboard = new FragmentDashboard();
        fragmentMap = new FragmentMap();
        fragmentSetting = new FragmentSetting();

        // Quản lý Fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_container, fragmentDashboard).commit();

        // Tham chiếu đến các icon và tiêu đề
        icDashboard = findViewById(R.id.ic_dashboard);
        icMap = findViewById(R.id.ic_map);
        icSetting = findViewById(R.id.ic_setting);

        navDashboardTitle = findViewById(R.id.nav_dashboard_title);
        navMapTitle = findViewById(R.id.nav_map_title);
        navSettingTitle = findViewById(R.id.nav_setting_title);

        // Đặt kích thước mặc định cho các icon và làm nổi bật icon Dashboard mặc định
        setDefaultIconSizes();
        resizeSelectedIcon(icDashboard);
        setBoldTitle(navDashboardTitle);

        // Xử lý sự kiện click cho các icon
        icDashboard.setOnClickListener(v -> {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragmentDashboard).commit();
            resizeSelectedIcon(icDashboard);
            setBoldTitle(navDashboardTitle);
        });

        icMap.setOnClickListener(v -> {


            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragmentMap).commit();
            resizeSelectedIcon(icMap);
            setBoldTitle(navMapTitle);
        });

        icSetting.setOnClickListener(v -> {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragmentSetting).commit();
            resizeSelectedIcon(icSetting);
            setBoldTitle(navSettingTitle);
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(App.wrap(newBase));
    }

    // Hàm để đặt kích thước mặc định cho các icon
    private void setDefaultIconSizes() {
        float defaultScale = 1.0f;
        icDashboard.setScaleX(defaultScale);
        icDashboard.setScaleY(defaultScale);
        icMap.setScaleX(defaultScale);
        icMap.setScaleY(defaultScale);
        icSetting.setScaleX(defaultScale);
        icSetting.setScaleY(defaultScale);
    }

    // Hàm để thay đổi kích thước icon được chọn
    private void resizeSelectedIcon(ImageView selectedIcon) {
        // Đặt lại kích thước mặc định cho tất cả các icon
        setDefaultIconSizes();
        // Phóng to biểu tượng được chọn
        float selectedScale = 1.2f; // Tăng 20% kích thước
        selectedIcon.setScaleX(selectedScale);
        selectedIcon.setScaleY(selectedScale);
    }

    // Hàm để làm cho tiêu đề được chọn in đậm và tăng kích thước
    private void setBoldTitle(TextView selectedTitle) {
        // Kích thước chữ mặc định
        float defaultTextSize = 16f;

        // Đặt tất cả các tiêu đề về chữ thường và kích thước mặc định
        navDashboardTitle.setTypeface(null, Typeface.NORMAL);
        navDashboardTitle.setTextSize(defaultTextSize);

        navMapTitle.setTypeface(null, Typeface.NORMAL);
        navMapTitle.setTextSize(defaultTextSize);

        navSettingTitle.setTypeface(null, Typeface.NORMAL);
        navSettingTitle.setTextSize(defaultTextSize);

        // Làm cho tiêu đề được chọn in đậm và tăng kích thước
        selectedTitle.setTypeface(null, Typeface.BOLD);
        selectedTitle.setTextSize(defaultTextSize + 2); // Tăng kích thước thêm 2 đơn vị
    }
}
