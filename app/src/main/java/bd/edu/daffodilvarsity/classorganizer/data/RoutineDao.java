package bd.edu.daffodilvarsity.classorganizer.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.model.Routine;

@Dao
public interface RoutineDao {


    @Query("SELECT COUNT(*) FROM routine")
    int getCount();

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND program LIKE :program AND level = :level AND term = :term AND section LIKE :section")
    List<Routine> getRoutineStudent(String campus, String department, String program, int level, int term, String section);

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND teachers_initial LIKE :teachersInitial")
    List<Routine> getRoutineTeacher(String campus, String department, String teachersInitial);

    @Query("SELECT DISTINCT time FROM routine WHERE alt_time LIKE :altTime")
    String getTime(String altTime);

    @Query("SELECT DISTINCT campus FROM routine")
    List<String> getCampuses();

    @Query("SELECT DISTINCT teachers_initial FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getTeachersInitials(String campus, String department);

    @Query("SELECT DISTINCT section FROM routine WHERE campus LIKE :campus AND department LIKE :department AND program LIKE :program AND level = :level AND term = :term")
    List<String> getSections(String campus, String department, String program, int level, int term);

    @Query("SELECT DISTINCT section FROM routine WHERE level = :level AND term = :term")
    List<String> getSections(int level, int term);

    @Query("SELECT DISTINCT time FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getTimes(String campus, String department);

    @Query("SELECT DISTINCT alt_time FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getAltTimes(String campus, String department);

    @Query("SELECT DISTINCT department FROM routine WHERE campus LIKE :campus")
    List<String> getDepartments(String campus);

    @Query("SELECT DISTINCT program FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getPrograms(String campus, String department);

    @Query("SELECT DISTINCT time_weight FROM routine WHERE time = :time")
    String getTimeWeight(String time);

    @Query("SELECT DISTINCT alt_time_weight FROM routine WHERE time = :altTime")
    String getAltTimeWeight(String altTime);

    @Query("SELECT * FROM routine WHERE id = :id")
    Routine getRoutine(long id);

    @Insert
    void insertRoutines(Routine... routines);

    @Insert
    long insertRoutine(Routine routine);

    @Query("DELETE FROM routine")
    void nukeRoutines();

    @Update
    void updateRoutine(Routine routine);


    @Query("SELECT * FROM routine WHERE id = :id")
    Routine getRoutineById(long id);

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND level = :level AND term = :term AND section LIKE :section")
    List<Routine> searchRoutine(String campus, String department, int level, int term, String section);

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND time LIKE :time AND day LIKE :day")
    List<Routine> searchRoutine(String campus, String department, String time, String day);

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND program LIKE :program AND teachers_initial LIKE :teachersInitial AND level = :level AND term = :term AND section LIKE :section")
    List<Routine> searchRoutine(String campus, String department, String program, String teachersInitial,  int level, int term, String section);

    @Query("SELECT DISTINCT course_title FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getCourseTitles(String campus, String department);

    @Query("SELECT DISTINCT course_code FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getCourseCodes(String campus, String department);

    @Query("SELECT DISTINCT section FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getSections(String campus, String department);

    @Query("SELECT DISTINCT room_no FROM routine WHERE campus LIKE :campus AND department LIKE :department")
    List<String> getRooms(String campus, String department);

    @Query("SELECT DISTINCT time, time_weight, alt_time, alt_time_weight FROM routine WHERE time LIKE :time")
    TimePOJO getTimeByTime(String time);

    @Query("SELECT DISTINCT time, time_weight, alt_time, alt_time_weight FROM routine WHERE alt_time LIKE :altTime")
    TimePOJO getTimeByAltTime(String altTime);

    class TimePOJO {
        @SerializedName("time")
        @ColumnInfo(name = "time")
        @Expose
        private String time;
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

        public TimePOJO() {
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
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
    }
}
