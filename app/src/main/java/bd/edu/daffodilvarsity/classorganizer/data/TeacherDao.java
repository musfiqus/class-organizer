package bd.edu.daffodilvarsity.classorganizer.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import bd.edu.daffodilvarsity.classorganizer.model.Teacher;

@Dao
public interface TeacherDao {

    @Query("SELECT DISTINCT * FROM teacher WHERE initial LIKE :teachersInitial")
    Teacher getTeacher(String teachersInitial);

    @Insert
    void insertTeachers(Teacher... teachers);

    @Query("DELETE FROM teacher")
    void nukeTeachers();
}
