package bd.edu.daffodilvarsity.classorganizer;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by musfiqus on 3/24/2017.
 */

public class DayData implements Serializable, Parcelable {
    public static final Creator<DayData> CREATOR = new Creator<DayData>() {
        @Override
        public DayData createFromParcel(Parcel source) {
            return new DayData(source);
        }

        @Override
        public DayData[] newArray(int size) {
            return new DayData[size];
        }
    };
    private String courseCode;
    private String teachersInitial;
    private String roomNo;
    private String time;
    private String day;
    private double timeWeight;

    public DayData(String courseCode, String teachersInitial, String roomNo, String time, String day, double timeWeight) {
        this.courseCode = courseCode;
        this.teachersInitial = teachersInitial;
        this.roomNo = roomNo;
        this.time = time;
        this.day = day.toLowerCase();
        this.timeWeight = timeWeight;
    }

    protected DayData(Parcel in) {
        this.courseCode = in.readString();
        this.teachersInitial = in.readString();
        this.roomNo = in.readString();
        this.time = in.readString();
        this.day = in.readString();
        this.timeWeight = in.readDouble();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTeachersInitial() {
        return teachersInitial;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public String getTime() {
        return time;
    }

    public String getDay() {
        return day;
    }

    public double getTimeWeight() {
        return timeWeight;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.courseCode);
        dest.writeString(this.teachersInitial);
        dest.writeString(this.roomNo);
        dest.writeString(this.time);
        dest.writeString(this.day);
        dest.writeDouble(this.timeWeight);
    }
}

