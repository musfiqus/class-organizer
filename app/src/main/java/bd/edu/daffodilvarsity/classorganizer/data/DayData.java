package bd.edu.daffodilvarsity.classorganizer.data;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Mushfiqus Salehin on 3/24/2017.
 * musfiqus@gmail.com
 */

public class DayData implements Serializable, Parcelable {
    @SerializedName("courseCode")
    private String courseCode;

    @SerializedName("teachersInitial")
    private String teachersInitial;

    @SerializedName("section")
    private String section;

    @SerializedName("level")
    private int level;

    @SerializedName("term")
    private int term;

    @SerializedName("roomNo")
    private String roomNo;

    @SerializedName("time")
    private String time;

    @SerializedName("day")
    private String day;

    @SerializedName("timeWeight")
    private double timeWeight;

    @SerializedName("courseTitle")
    private String courseTitle;

    public DayData(String courseCode, String teachersInitial, String section, int level, int term, String roomNo, String time, String day, double timeWeight, String courseTitle) {
        this.courseCode = courseCode;
        this.teachersInitial = teachersInitial;
        this.section = section;
        this.level = level;
        this.term = term;
        this.roomNo = roomNo;
        this.time = time;
        this.day = day;
        this.timeWeight = timeWeight;
        this.courseTitle = courseTitle;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTeachersInitial() {
        return teachersInitial;
    }

    public String getSection() {
        return section;
    }

    public int getLevel() {
        return level;
    }

    public int getTerm() {
        return term;
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

    public String getCourseTitle() {
        return courseTitle;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DayData)) {
            return false;
        }
        final DayData otherData = (DayData) obj;
        if (!(otherData.getCourseCode().equals(this.courseCode))) {
            return false;
        }
        if (!(otherData.getTeachersInitial().equals(this.teachersInitial))) {
            return false;
        }
        if (!(otherData.getSection().equals(this.section))) {
            return false;
        }
        if (otherData.getLevel() != this.level) {
            return false;
        }
        if (otherData.getTerm() != this.term) {
            return false;
        }
        if (!(otherData.getDay().equals(this.day))) {
            return false;
        }
        if (!(otherData.getRoomNo().equals(this.roomNo))) {
            return false;
        }
        if (!(otherData.getTime().equals(this.time))) {
            return false;
        }
        return otherData.getTimeWeight() == this.timeWeight;
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(courseCode, teachersInitial, section, level, term, roomNo, time, day, timeWeight, courseTitle);
        } else {
            int result = 17;
            result = 31 * result + courseCode.hashCode();
            result = 31 * result + teachersInitial.hashCode();
            result = 31 * result + section.hashCode();
            result = 31 * result + level;
            result = 31 * result + term;
            result = 31 * result + roomNo.hashCode();
            result = 31 * result + time.hashCode();
            result = 31 * result + day.hashCode();
            result = 31 * result + Double.valueOf(timeWeight).hashCode();
            result = 31 * result + courseTitle.hashCode();
            return result;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.courseCode);
        dest.writeString(this.teachersInitial);
        dest.writeString(this.section);
        dest.writeInt(this.level);
        dest.writeInt(this.term);
        dest.writeString(this.roomNo);
        dest.writeString(this.time);
        dest.writeString(this.day);
        dest.writeDouble(this.timeWeight);
        dest.writeString(this.courseTitle);
    }

    protected DayData(Parcel in) {
        this.courseCode = in.readString();
        this.teachersInitial = in.readString();
        this.section = in.readString();
        this.level = in.readInt();
        this.term = in.readInt();
        this.roomNo = in.readString();
        this.time = in.readString();
        this.day = in.readString();
        this.timeWeight = in.readDouble();
        this.courseTitle = in.readString();
    }

    public static final Parcelable.Creator<DayData> CREATOR = new Parcelable.Creator<DayData>() {
        @Override
        public DayData createFromParcel(Parcel source) {
            return new DayData(source);
        }

        @Override
        public DayData[] newArray(int size) {
            return new DayData[size];
        }
    };
}

