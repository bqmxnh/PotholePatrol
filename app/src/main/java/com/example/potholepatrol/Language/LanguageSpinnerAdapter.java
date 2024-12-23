package com.example.potholepatrol.Language;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.potholepatrol.R;

import java.util.List;

public class LanguageSpinnerAdapter extends ArrayAdapter<LanguageItem> {
    private LayoutInflater inflater;

    public LanguageSpinnerAdapter(Context context, List<LanguageItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = createItemView(position, convertView, parent);
        view.setBackgroundColor(Color.TRANSPARENT);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);
        }

        ImageView ivFlag = convertView.findViewById(R.id.ivFlag);
        TextView tvLanguage = convertView.findViewById(R.id.tvLanguage);

        LanguageItem item = getItem(position);
        if (item != null) {
            ivFlag.setImageResource(item.getFlagResource());
            tvLanguage.setText(item.getLanguageName());
        }

        return convertView;
    }
}