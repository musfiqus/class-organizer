
package bd.edu.daffodilvarsity.classorganizer.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = Semester.TABLE_NAME)
public class Semester implements Parcelable {
    /** The name of the Semester table. */
    public static final String TABLE_NAME = "semester";
    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    @Expose
    private int id;
    @SerializedName("semesterID")
    @ColumnInfo(name = "semester_id")
    @Expose
    private int semesterID;
    @SerializedName("semesterName")
    @ColumnInfo(name = "semester_name")
    @Expose
    private String semesterName;
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
    @SerializedName("classStart")
    @ColumnInfo(name = "class_start")
    @Expose
    private long classStart;
    @SerializedName("classEnd")
    @ColumnInfo(name = "class_end")
    @Expose
    private long classEnd;
    @SerializedName("midStart")
    @ColumnInfo(name = "mid_start")
    @Expose
    private long midStart;
    @SerializedName("midEnd")
    @ColumnInfo(name = "mid_end")
    @Expose
    private long midEnd;
    @SerializedName("vacationOneStart")
    @ColumnInfo(name = "vacation_one_start")
    @Expose
    private long vacationOneStart;
    @SerializedName("vacationOneEnd")
    @ColumnInfo(name = "vacation_one_end")
    @Expose
    private long vacationOneEnd;
    @SerializedName("vacationTwoStart")
    @ColumnInfo(name = "vacation_two_start")
    @Expose
    private long vacationTwoStart;
    @SerializedName("vacationTwoEnd")
    @ColumnInfo(name = "vacation_two_end")
    @Expose
    private long vacationTwoEnd;

    public Semester() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSemesterID() {
        return semesterID;
    }

    public void setSemesterID(int semesterID) {
        this.semesterID = semesterID;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
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

    public long getClassStart() {
        return classStart;
    }

    public void setClassStart(long classStart) {
        this.classStart = classStart;
    }

    public long getClassEnd() {
        return classEnd;
    }

    public void setClassEnd(long classEnd) {
        this.classEnd = classEnd;
    }

    public long getMidStart() {
        return midStart;
    }

    public void setMidStart(long midStart) {
        this.midStart = midStart;
    }

    public long getMidEnd() {
        return midEnd;
    }

    public void setMidEnd(long midEnd) {
        this.midEnd = midEnd;
    }

    public long getVacationOneStart() {
        return vacationOneStart;
    }

    public void setVacationOneStart(long vacationOneStart) {
        this.vacationOneStart = vacationOneStart;
    }

    public long getVacationOneEnd() {
        return vacationOneEnd;
    }

    public void setVacationOneEnd(long vacationOneEnd) {
        this.vacationOneEnd = vacationOneEnd;
    }

    public long getVacationTwoStart() {
        return vacationTwoStart;
    }

    public void setVacationTwoStart(long vacationTwoStart) {
        this.vacationTwoStart = vacationTwoStart;
    }

    public long getVacationTwoEnd() {
        return vacationTwoEnd;
    }

    public void setVacationTwoEnd(long vacationTwoEnd) {
        this.vacationTwoEnd = vacationTwoEnd;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.semesterID);
        dest.writeString(this.semesterName);
        dest.writeString(this.campus);
        dest.writeString(this.department);
        dest.writeString(this.program);
        dest.writeLong(this.classStart);
        dest.writeLong(this.classEnd);
        dest.writeLong(this.midStart);
        dest.writeLong(this.midEnd);
        dest.writeLong(this.vacationOneStart);
        dest.writeLong(this.vacationOneEnd);
        dest.writeLong(this.vacationTwoStart);
        dest.writeLong(this.vacationTwoEnd);
    }

    protected Semester(Parcel in) {
        this.id = in.readInt();
        this.semesterID = in.readInt();
        this.semesterName = in.readString();
        this.campus = in.readString();
        this.department = in.readString();
        this.program = in.readString();
        this.classStart = in.readLong();
        this.classEnd = in.readLong();
        this.midStart = in.readLong();
        this.midEnd = in.readLong();
        this.vacationOneStart = in.readLong();
        this.vacationOneEnd = in.readLong();
        this.vacationTwoStart = in.readLong();
        this.vacationTwoEnd = in.readLong();
    }

    public static final Parcelable.Creator<Semester> CREATOR = new Parcelable.Creator<Semester>() {
        @Override
        public Semester createFromParcel(Parcel source) {
            return new Semester(source);
        }

        @Override
        public Semester[] newArray(int size) {
            return new Semester[size];
        }
    };
}
