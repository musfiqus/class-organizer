package bd.edu.daffodilvarsity.classorganizer.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND level = :level AND term = :term AND section LIKE :section")
    List<Routine> searchRoutine(String campus, String department, int level, int term, String section);

    @Query("SELECT * FROM routine WHERE campus LIKE :campus AND department LIKE :department AND time LIKE :time AND day LIKE :day")
    List<Routine> searchRoutine(String campus, String department, String time, String day);

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

    @Delete
    void deleteRoutines(Routine... routines);

    @Delete
    void deleteRoutine(Routine routine);

    @Query("DELETE FROM routine WHERE campus LIKE :campus AND department LIKE :department AND program LIKE :program AND course_code LIKE :courseCode AND course_title LIKE :courseTitle AND teachers_initial LIKE :teachersInitial AND section LIKE :section AND level =:level AND term = :term AND room_no LIKE :roomNo AND time LIKE :time AND day LIKE :day AND time_weight LIKE :timeWeight AND alt_time LIKE :altTime AND alt_time_weight LIKE :altTimeWeight")
    void deleteModified(String campus, String department, String program, String courseCode, String courseTitle, String teachersInitial, String section, int level, int term, String roomNo, String time, String day, String timeWeight, String altTime, String altTimeWeight);

    @Insert
    void insertRoutines(Routine... routines);

    @Insert
    long insertRoutine(Routine routine);

    @Query("DELETE FROM routine")
    void nukeRoutines();

    @Update
    void updateRoutine(Routine routine);

    @Query("SELECT DISTINCT id FROM routine WHERE campus LIKE :campus AND department LIKE :department AND program LIKE :program AND course_code LIKE :courseCode AND course_title LIKE :courseTitle AND teachers_initial LIKE :teachersInitial AND section LIKE :section AND level =:level AND term = :term AND room_no LIKE :roomNo AND time LIKE :time AND day LIKE :day AND time_weight LIKE :timeWeight AND alt_time LIKE :altTime AND alt_time_weight LIKE :altTimeWeight")
    long getId(String campus, String department, String program, String courseCode, String courseTitle, String teachersInitial, String section, int level, int term, String roomNo, String time, String day, String timeWeight, String altTime, String altTimeWeight);

    @Query("SELECT * FROM routine WHERE id = :id")
    Routine getRoutineById(long id);


}
