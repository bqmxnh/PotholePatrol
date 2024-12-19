package com.example.potholepatrol.Language;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Set initial locale from saved preferences
        setLocale(getApplicationContext());
    }

    public static void setLocale(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = settings.getString("language", "en");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        context.createConfigurationContext(config);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static Context wrap(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = settings.getString("language", "en");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}