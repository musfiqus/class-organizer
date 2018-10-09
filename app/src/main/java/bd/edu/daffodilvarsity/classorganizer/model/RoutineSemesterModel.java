package bd.edu.daffodilvarsity.classorganizer.model;

import java.util.List;

public class RoutineSemesterModel {
    public List<Routine> routines;
    public Semester semester;

    public RoutineSemesterModel(List<Routine> routines, Semester semester) {
        this.routines = routines;
        this.semester = semester;
    }
}