package bd.edu.daffodilvarsity.classorganizer.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.model.Semester;

@Dao
public interface SemesterDao {

    @Query("SELECT COUNT(*) FROM semester")
    int getCount();

    @Query("SELECT * FROM semester WHERE campus LIKE :campus AND department LIKE :department")
    List<Semester> getSemesters(String campus, String department);

    @Query("SELECT * FROM semester WHERE campus LIKE :campus AND department LIKE :department AND program LIKE :program")
    List<Semester> getSemesters(String campus, String department, String program);

    @Query("DELETE FROM semester")
    void nukeSemesters();

    @Insert
    void insertSemester(Semester semester);

    @Insert
    void insertSemesters(Semester... semesters);



}
