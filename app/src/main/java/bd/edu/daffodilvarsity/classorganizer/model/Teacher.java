package bd.edu.daffodilvarsity.classorganizer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = Teacher.TABLE_NAME)
public class Teacher implements Parcelable {

    /** The name of the Routine table. */
    public static final String TABLE_NAME = "teacher";

    @SerializedName("id")
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    @Expose
    private long id;

    @SerializedName("initial")
    @ColumnInfo(name = "initial")
    @Expose
    private String initial;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    @Expose
    private String name;

    @SerializedName("designation")
    @ColumnInfo(name = "designation")
    @Expose
    private String designation;

    @SerializedName("campus")
    @ColumnInfo(name = "campus")
    @Expose
    private String campus;

    @SerializedName("department")
    @ColumnInfo(name = "department")
    @Expose
    private String department;

    @SerializedName("faculty")
    @ColumnInfo(name = "faculty")
    @Expose
    private String faculty;

    @SerializedName("roomNo")
    @ColumnInfo(name = "roomNo")
    @Expose
    private String roomNo;

    @SerializedName("phoneNo")
    @ColumnInfo(name = "phoneNo")
    @Expose
    private String phoneNo;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    @Expose
    private String email;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.initial);
        dest.writeString(this.name);
        dest.writeString(this.designation);
        dest.writeString(this.campus);
        dest.writeString(this.department);
        dest.writeString(this.faculty);
        dest.writeString(this.roomNo);
        dest.writeString(this.phoneNo);
        dest.writeString(this.email);
    }

    public Teacher() {
    }

    protected Teacher(Parcel in) {
        this.id = in.readLong();
        this.initial = in.readString();
        this.name = in.readString();
        this.designation = in.readString();
        this.campus = in.readString();
        this.department = in.readString();
        this.faculty = in.readString();
        this.roomNo = in.readString();
        this.phoneNo = in.readString();
        this.email = in.readString();
    }

    public static final Parcelable.Creator<Teacher> CREATOR = new Parcelable.Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel source) {
            return new Teacher(source);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };
}
