
package bd.edu.daffodilvarsity.classorganizer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Database implements Parcelable
{

    @SerializedName("databaseVersion")
    @Expose
    private int databaseVersion;
    @SerializedName("routine")
    @Expose
    private List<Routine> routine = new ArrayList<Routine>();
    @SerializedName("semester")
    @Expose
    private List<Semester> semester = new ArrayList<Semester>();
    public final static Parcelable.Creator<Database> CREATOR = new Creator<Database>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Database createFromParcel(Parcel in) {
            return new Database(in);
        }

        public Database[] newArray(int size) {
            return (new Database[size]);
        }

    }
    ;

    protected Database(Parcel in) {
        this.databaseVersion = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.routine, (bd.edu.daffodilvarsity.classorganizer.model.Routine.class.getClassLoader()));
        in.readList(this.semester, (bd.edu.daffodilvarsity.classorganizer.model.Semester.class.getClassLoader()));
    }

    public Database() {
    }

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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(databaseVersion);
        dest.writeList(routine);
        dest.writeList(semester);
    }

    public int describeContents() {
        return  0;
    }

}
