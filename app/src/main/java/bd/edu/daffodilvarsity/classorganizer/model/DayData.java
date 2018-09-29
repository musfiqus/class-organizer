package bd.edu.daffodilvarsity.classorganizer.model;

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
    private static final String TAG = "DayData";

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

    @SerializedName("isMuted")
    private boolean isMuted;

    public DayData(String courseCode, String teachersInitial, String section, int level, int term, String roomNo, String time, String day, double timeWeight, String courseTitle, boolean isMuted) {
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
        this.isMuted = isMuted;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTeachersInitial() {
        return teachersInitial;
    }

    public void setTeachersInitial(String teachersInitial) {
        this.teachersInitial = teachersInitial;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public double getTimeWeight() {
        return timeWeight;
    }

    public void setTimeWeight(double timeWeight) {
        this.timeWeight = timeWeight;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
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
        if (otherData.getTeachersInitial() == null || !(otherData.getTeachersInitial().equals(this.teachersInitial))) {
            if (!(otherData.getTeachersInitial() == null && this.teachersInitial == null)) {
                return false;
            }
            return false;
        }
        if (otherData.getSection() == null || !(otherData.getSection().equals(this.section))) {
            return false;
        }
        if (otherData.getLevel() != this.level) {
            return false;
        }
        if (otherData.getTerm() != this.term) {
            return false;
        }
        if (otherData.getDay() == null || !(otherData.getDay().equals(this.day))) {
            return false;
        }
        if (otherData.getRoomNo() == null || !(otherData.getRoomNo().equals(this.roomNo))) {
            return false;
        }
        if (otherData.getTime() == null || !(otherData.getTime().equals(this.time))) {
            return false;
        }
        if (otherData.getTimeWeight() != otherData.getTimeWeight()) {
            return false;
        }
        if (otherData.isMuted() != otherData.isMuted()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return Objects.hash(courseCode, teachersInitial, section, level, term, roomNo, time, day, timeWeight, courseTitle, isMuted);
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
            result = 31 * result + Boolean.valueOf(isMuted).hashCode();
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
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
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
        this.isMuted = in.readByte() != 0;
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

