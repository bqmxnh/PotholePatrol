package com.example.potholepatrol.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StatisticsData implements Parcelable {
    private String date;
    private int total;
    private int low;
    private int medium;
    private int high;


    public StatisticsData(String date, int total, int low, int medium, int high) {
        this.date = date;
        this.total = total;
        this.low = low;
        this.medium = medium;
        this.high = high;
    }

    public StatisticsData(String date, int total) {
        this.date = date;
        this.total = total;
    }


    protected StatisticsData(Parcel in) {
        date = in.readString();
        total = in.readInt();
        low = in.readInt();
        medium = in.readInt();
        high = in.readInt();
    }

    public static final Creator<StatisticsData> CREATOR = new Creator<StatisticsData>() {
        @Override
        public StatisticsData createFromParcel(Parcel in) {
            return new StatisticsData(in);
        }

        @Override
        public StatisticsData[] newArray(int size) {
            return new StatisticsData[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeInt(total);
        dest.writeInt(low);
        dest.writeInt(medium);
        dest.writeInt(high);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }
}
