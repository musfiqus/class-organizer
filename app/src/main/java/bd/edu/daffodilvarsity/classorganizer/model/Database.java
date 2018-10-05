
package bd.edu.daffodilvarsity.classorganizer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Database implements Parcelable {

    @SerializedName("databaseVersion")
    @Expose
    private int databaseVersion;
    @SerializedName("routine")
    @Expose
    private List<Routine> routine = new ArrayList<>();
    @SerializedName("semester")
    @Expose
    private List<Semester> semester = new ArrayList<>();
    @SerializedName("teacher")
    @Expose
    private List<Teacher> teacher = new ArrayList<>();

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public List<Routine> getRoutine() {
        return routine;
    }

    public void setRoutine(List<Routine> routine) {
        this.routine = routine;
    }

    public List<Semester> getSemester() {
        return semester;
    }

    public void setSemester(List<Semester> semester) {
        this.semester = semester;
    }

    public List<Teacher> getTeacher() {
        return teacher;
    }

    public void setTeacher(List<Teacher> teacher) {
        this.teacher = teacher;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.databaseVersion);
        dest.writeTypedList(this.routine);
        dest.writeTypedList(this.semester);
        dest.writeTypedList(this.teacher);
    }

    public Database() {
    }

    protected Database(Parcel in) {
        this.databaseVersion = in.readInt();
        this.routine = in.createTypedArrayList(Routine.CREATOR);
        this.semester = in.createTypedArrayList(Semester.CREATOR);
        this.teacher = in.createTypedArrayList(Teacher.CREATOR);
    }

    public static final Creator<Database> CREATOR = new Creator<Database>() {
        @Override
        public Database createFromParcel(Parcel source) {
            return new Database(source);
        }

        @Override
        public Database[] newArray(int size) {
            return new Database[size];
        }
    };
}
