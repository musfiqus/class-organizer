package bd.edu.daffodilvarsity.classorganizer.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.ui.setup.SetupViewModel;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PrefManager;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;

@Database(entities = {Routine.class, Semester.class}, version = ClassOrganizerDatabase.DATABASE_VERSION, exportSchema = false)
public abstract class ClassOrganizerDatabase extends RoomDatabase {

    public static final int DATABASE_VERSION  = 1;

    public abstract RoutineDao routineAccess();
    public abstract SemesterDao semesterAccess();

    private static ClassOrganizerDatabase sInstance;

    /**
     * Gets the singleton instance of ClassOrganizerDatabase.
     *
     * @return The singleton instance of ClassOrganizerDatabase.
     */
    public static synchronized ClassOrganizerDatabase getInstance() {
        if (sInstance == null) {
            sInstance = Room
                    .databaseBuilder(ClassOrganizer.getInstance(), ClassOrganizerDatabase.class, "routine_room_database.db")
                    .fallbackToDestructiveMigration()
                    .build();
            sInstance.generateInitialData();
        }
        return sInstance;
    }

    boolean upgrade(bd.edu.daffodilvarsity.classorganizer.model.Database database) {
        //check version
        if (PreferenceGetter.getDatabaseVersion() >= database.getDatabaseVersion()) {
            return false;
        }
        List<Routine> modifiedRoutineOriginal = PreferenceGetter.getModifiedRoutineOriginal();
        //fetch modified routines
        List<Routine> modifiedRoutines = fetchModifiedRoutines(modifiedRoutineOriginal);
        //clean previous records
        routineAccess().nukeRoutines();
        semesterAccess().nukeSemesters();
        //insert routine
        populateData(database);
        //upgrade modifications
        upgradeModifications(modifiedRoutineOriginal, modifiedRoutines);
        //delete deleted ones
        List<Routine> deleted = PreferenceGetter.getDeletedRoutine();
        for (Routine routine: deleted) {
            deleteRoutineModified(routine);
        }
        return true;

    }

    private void populateData(bd.edu.daffodilvarsity.classorganizer.model.Database database) {
        //insert routines
        if (database.getRoutine() != null) {
            routineAccess().insertRoutines(database.getRoutine().toArray(new Routine[0]));
        }
        //insert semesters
        if (database.getSemester() != null) {
            semesterAccess().insertSemesters(database.getSemester().toArray(new Semester[0]));
        }
        //set database version
        PreferenceGetter.setDatabaseVersion(database.getDatabaseVersion());
    }

    private void generateInitialData() {
        if (routineAccess().getCount() == 0) {
            bd.edu.daffodilvarsity.classorganizer.model.Database database = FileUtils.readOfflineDatabase();
            populateData(database);
        }
    }

    List<String> getSections(int level, int term) {
        List<String> sections = routineAccess().getSections(level, term);
        sections.removeAll(Collections.singleton(null));
        Collections.sort(sections);
        return sections;
    }

    List<Routine> getFreeRooms(String time, String day) {
        List<Routine> allRoutine = routineAccess().searchRoutine(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), time, day);
        if (allRoutine != null) {
            for (int i = 0; i < allRoutine.size(); i++) {
                if (!InputHelper.isEmpty(allRoutine.get(i).getCourseCode()) && !allRoutine.get(i).getCourseCode().equalsIgnoreCase("N/A")) {
                    allRoutine.remove(i);
                    i--;
                    continue;
                }
                if (!InputHelper.isEmpty(allRoutine.get(i).getTeachersInitial()) && !allRoutine.get(i).getTeachersInitial().equalsIgnoreCase("N/A")) {
                    allRoutine.remove(i);
                    i--;
                    continue;
                }
                if (!InputHelper.isEmpty(allRoutine.get(i).getCourseTitle()) && !allRoutine.get(i).getCourseTitle().equalsIgnoreCase("N/A")) {
                    allRoutine.remove(i);
                    i--;
                }
            }
        }
        return allRoutine;
    }

    List<String> getSectionsFromDb(String campus, String department, String program, int level, int term) {
        List<String> sections = routineAccess().getSections(campus, department, program, level, term);
        sections.removeAll(Collections.singleton(null));
        Collections.sort(sections);
        return sections;
    }

    List<String> getTimeListFromDb() {
        if (PreferenceGetter.isRamadanEnabled()) {
            return routineAccess().getAltTimes(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment());
        }
        return routineAccess().getTimes(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment());
    }

    List<Routine> getSearchRoutineResults(int level, int term, String section) {
        return routineAccess().searchRoutine(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), level, term, section);
    }

    List<String> getTeachersInitialsFromDb(String campus, String department) {
        List<String> initials = routineAccess().getTeachersInitials(campus, department);
        if (initials != null) {
            Collections.sort(initials);
        }
        return initials;
    }


    List<Routine> loadRoutineFromDb() {
        List<Routine> routines;
        if (PreferenceGetter.getUserType().equals(SetupViewModel.USER_STUDENT)) {
            routines = routineAccess().getRoutineStudent(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), PreferenceGetter.getProgram(), PreferenceGetter.getLevel(), PreferenceGetter.getTerm(), PreferenceGetter.getSection());
        } else {
            routines = routineAccess().getRoutineTeacher(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), PreferenceGetter.getInitial());
        }
        List<Routine> savedRoutine = PreferenceGetter.getSavedRoutine();
        if (savedRoutine != null) {
            routines.addAll(savedRoutine);
        }
        return routines;
    }

    List<Routine> getRoutineByInitial(String initial) {
        if (InputHelper.isEmpty(initial)) {
            return new ArrayList<>();
        }
        List<Routine> routines = routineAccess().getRoutineTeacher(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), initial);
        if (routines != null) {
            for (int i = 0; i < routines.size(); i++) {
                if (InputHelper.isEmpty(routines.get(i).getTeachersInitial())) {
                    routines.remove(i);
                    i--;
                }
            }
        }
        return routines;

    }

    Semester getSemester() {
        List<Semester> semesters = new ArrayList<>();
        if (PreferenceGetter.isStudent()) {
            semesters.addAll(semesterAccess().getSemesters(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), PreferenceGetter.getProgram()));
        } else {
            semesters.addAll(semesterAccess().getSemesters(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment()));
        }
        Collections.sort(semesters, ((o1, o2) -> Integer.compare(o1.getId(), o2.getId())));
        if (semesters.size() > 0) {
            return semesters.get(semesters.size() - 1);
        } else {
            return null;
        }
    }

    long getId(Routine routine) {
        return routineAccess().getId(routine.getCampus(), routine.getDepartment(), routine.getProgram(), routine.getCourseCode(), routine.getCourseTitle(), routine.getTeachersInitial(), routine.getSection(), routine.getLevel(), routine.getTerm(), routine.getRoomNo(), routine.getTime(), routine.getDay(), routine.getTimeWeight(), routine.getAltTime(), routine.getAltTimeWeight());
    }

    void modifyRoutine(Routine routine) {
        List<Routine> originalMods = PreferenceGetter.getModifiedRoutineOriginal();
        int index = -1;
        for (int i = 0; i < originalMods.size(); i++) {
            if (originalMods.get(i).getId() == routine.getId()) {
                index = i;
            }
        }
        if (index < 0) {
            Routine originalRoutine = routineAccess().getRoutineById(routine.getId());
            originalMods.add(originalRoutine);
            PreferenceGetter.setModifiedRoutineOriginal(originalMods);
        }
        routineAccess().updateRoutine(routine);
    }

    private void upgradeModifications(List<Routine> modifiedOriginals, List<Routine> modified) {
        for (int i = 0; i < modifiedOriginals.size(); i++) {
            long newId = getId(modifiedOriginals.get(i));
            if (newId != 0) {
                try {
                    modified.get(i).setId(newId);
                    routineAccess().updateRoutine(modified.get(i));
                } catch (IndexOutOfBoundsException e) {
                    modifiedOriginals.remove(i);
                    i--;
                    e.printStackTrace();
                }
                modifiedOriginals.get(i).setId(newId);
            } else {
                try {
                    modified.get(i).setId(0);
                    long insertId = routineAccess().insertRoutine(modified.get(i));
                    Routine insertedRoutine = routineAccess().getRoutineById(insertId);
                    modifiedOriginals.set(i, insertedRoutine);
                } catch (IndexOutOfBoundsException e) {
                    modifiedOriginals.remove(i);
                    i--;
                    e.printStackTrace();
                }
            }
        }
        PreferenceGetter.setModifiedRoutineOriginal(modifiedOriginals);
    }

    private List<Routine> fetchModifiedRoutines(List<Routine> modifiedRoutineOriginal) {
        List<Routine> modified = new ArrayList<>();
        for (Routine routine : modifiedRoutineOriginal) {
            modified.add(routineAccess().getRoutineById(routine.getId()));
        }
        return modified;
    }

    void deleteRoutineModified(Routine routine) {
        routineAccess().deleteModified(routine.getCampus(), routine.getDepartment(), routine.getProgram(), routine.getCourseCode(), routine.getCourseTitle(), routine.getTeachersInitial(), routine.getSection(), routine.getLevel(), routine.getTerm(), routine.getRoomNo(), routine.getTime(), routine.getDay(), routine.getTimeWeight(), routine.getAltTime(), routine.getAltTimeWeight());
    }
}
