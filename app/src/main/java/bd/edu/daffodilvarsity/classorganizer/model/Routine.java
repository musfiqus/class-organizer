
package bd.edu.daffodilvarsity.classorganizer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = Routine.TABLE_NAME)
public class Routine implements Parcelable {

    /** The name of the Routine table. */
    public static final String TABLE_NAME = "routine";

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    @Expose
    private long id;
    @SerializedName("campus")
    @ColumnInfo(name = "campus")
    @Expose
    private String campus;
    @SerializedName("department")
    @ColumnInfo(name = "department")
    @Expose
    private String department;
    @SerializedName("program")
    @ColumnInfo(name = "program")
    @Expose
    private String program;
    @SerializedName("courseCode")
    @ColumnInfo(name = "course_code")
    @Expose
    private String courseCode;
    @SerializedName("courseTitle")
    @ColumnInfo(name = "course_title")
    @Expose
    private String courseTitle;
    @SerializedName("teachersInitial")
    @ColumnInfo(name = "teachers_initial")
    @Expose
    private String teachersInitial;
    @SerializedName("section")
    @ColumnInfo(name = "section")
    @Expose
    private String section;
    @SerializedName("level")
    @ColumnInfo(name = "level")
    @Expose
    private int level;
    @SerializedName("term")
    @ColumnInfo(name = "term")
    @Expose
    private int term;
    @SerializedName("roomNo")
    @ColumnInfo(name = "room_no")
    @Expose
    private String roomNo;
    @SerializedName("time")
    @ColumnInfo(name = "time")
    @Expose
    private String time;
    @SerializedName("day")
    @ColumnInfo(name = "day")
    @Expose
    private String day;
    @SerializedName("timeWeight")
    @ColumnInfo(name = "time_weight")
    @Expose
    private String timeWeight;
    @SerializedName("altTime")
    @ColumnInfo(name = "alt_time")
    private String altTime;
    @SerializedName("altTimeWeight")
    @ColumnInfo(name = "alt_time_weight")
    @Expose
    private String altTimeWeight;
    @SerializedName("isMuted")
    @ColumnInfo(name = "is_muted")
    @Expose
    private boolean isMuted;

    public Routine() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
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

    public String getTimeWeight() {
        return timeWeight;
    }

    public void setTimeWeight(String timeWeight) {
        this.timeWeight = timeWeight;
    }

    public String getAltTime() {
        return altTime;
    }

    public void setAltTime(String altTime) {
        this.altTime = altTime;
    }

    public String getAltTimeWeight() {
        return altTimeWeight;
    }

    public void setAltTimeWeight(String altTimeWeight) {
        this.altTimeWeight = altTimeWeight;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Routine)) return false;

        Routine routine = (Routine) o;

        if (getLevel() != routine.getLevel()) return false;
        if (getTerm() != routine.getTerm()) return false;
        if (getCampus() != null ? !getCampus().equals(routine.getCampus()) : routine.getCampus() != null)
            return false;
        if (getDepartment() != null ? !getDepartment().equals(routine.getDepartment()) : routine.getDepartment() != null)
            return false;
        if (getProgram() != null ? !getProgram().equals(routine.getProgram()) : routine.getProgram() != null)
            return false;
        if (getCourseCode() != null ? !getCourseCode().equals(routine.getCourseCode()) : routine.getCourseCode() != null)
            return false;
        if (getCourseTitle() != null ? !getCourseTitle().equals(routine.getCourseTitle()) : routine.getCourseTitle() != null)
            return false;
        if (getTeachersInitial() != null ? !getTeachersInitial().equals(routine.getTeachersInitial()) : routine.getTeachersInitial() != null)
            return false;
        if (getSection() != null ? !getSection().equals(routine.getSection()) : routine.getSection() != null)
            return false;
        if (getRoomNo() != null ? !getRoomNo().equals(routine.getRoomNo()) : routine.getRoomNo() != null)
            return false;
        if (getTime() != null ? !getTime().equals(routine.getTime()) : routine.getTime() != null)
            return false;
        if (getDay() != null ? !getDay().equals(routine.getDay()) : routine.getDay() != null)
            return false;
        if (getTimeWeight() != null ? !getTimeWeight().equals(routine.getTimeWeight()) : routine.getTimeWeight() != null)
            return false;
        if (getAltTime() != null ? !getAltTime().equals(routine.getAltTime()) : routine.getAltTime() != null)
            return false;
        return getAltTimeWeight() != null ? getAltTimeWeight().equals(routine.getAltTimeWeight()) : routine.getAltTimeWeight() == null;
    }

    @Override
    public int hashCode() {
        int result = getCampus() != null ? getCampus().hashCode() : 0;
        result = 31 * result + (getDepartment() != null ? getDepartment().hashCode() : 0);
        result = 31 * result + (getProgram() != null ? getProgram().hashCode() : 0);
        result = 31 * result + (getCourseCode() != null ? getCourseCode().hashCode() : 0);
        result = 31 * result + (getCourseTitle() != null ? getCourseTitle().hashCode() : 0);
        result = 31 * result + (getTeachersInitial() != null ? getTeachersInitial().hashCode() : 0);
        result = 31 * result + (getSection() != null ? getSection().hashCode() : 0);
        result = 31 * result + getLevel();
        result = 31 * result + getTerm();
        result = 31 * result + (getRoomNo() != null ? getRoomNo().hashCode() : 0);
        result = 31 * result + (getTime() != null ? getTime().hashCode() : 0);
        result = 31 * result + (getDay() != null ? getDay().hashCode() : 0);
        result = 31 * result + (getTimeWeight() != null ? getTimeWeight().hashCode() : 0);
        result = 31 * result + (getAltTime() != null ? getAltTime().hashCode() : 0);
        result = 31 * result + (getAltTimeWeight() != null ? getAltTimeWeight().hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.campus);
        dest.writeString(this.department);
        dest.writeString(this.program);
        dest.writeString(this.courseCode);
        dest.writeString(this.courseTitle);
        dest.writeString(this.teachersInitial);
        dest.writeString(this.section);
        dest.writeInt(this.level);
        dest.writeInt(this.term);
        dest.writeString(this.roomNo);
        dest.writeString(this.time);
        dest.writeString(this.day);
        dest.writeString(this.timeWeight);
        dest.writeString(this.altTime);
        dest.writeString(this.altTimeWeight);
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
    }

    protected Routine(Parcel in) {
        this.id = in.readLong();
        this.campus = in.readString();
        this.department = in.readString();
        this.program = in.readString();
        this.courseCode = in.readString();
        this.courseTitle = in.readString();
        this.teachersInitial = in.readString();
        this.section = in.readString();
        this.level = in.readInt();
        this.term = in.readInt();
        this.roomNo = in.readString();
        this.time = in.readString();
        this.day = in.readString();
        this.timeWeight = in.readString();
        this.altTime = in.readString();
        this.altTimeWeight = in.readString();
        this.isMuted = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Routine> CREATOR = new Parcelable.Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel source) {
            return new Routine(source);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };
}
