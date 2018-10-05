package bd.edu.daffodilvarsity.classorganizer.data;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.ui.setup.SetupViewModel;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;

@Database(entities = {Routine.class, Semester.class, Teacher.class}, version = ClassOrganizerDatabase.DATABASE_VERSION, exportSchema = false)
public abstract class ClassOrganizerDatabase extends RoomDatabase {
    private static final String TAG = "ClassOrganizerDatabase";

    public static final int DATABASE_VERSION = 1;

    public abstract RoutineDao routineAccess();

    public abstract SemesterDao semesterAccess();

    public abstract TeacherDao teacherAccess();

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

    private void populateData(bd.edu.daffodilvarsity.classorganizer.model.Database database) {
        //insert routines
        if (database.getRoutine() != null) {
            routineAccess().insertRoutines(database.getRoutine().toArray(new Routine[0]));
        }
        //insert semesters
        if (database.getSemester() != null) {
            semesterAccess().insertSemesters(database.getSemester().toArray(new Semester[0]));
        }
        //insert teachers
        if (database.getTeacher() != null) {
            teacherAccess().insertTeachers(database.getTeacher().toArray(new Teacher[0]));
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
        List<Routine> modifiedRoutine = PreferenceGetter.getModifiedRoutineList();
        List<Routine> cleanRoutine = removeModified(routines);
        cleanRoutine.addAll(savedRoutine);
        cleanRoutine.addAll(modifiedRoutine);
        return cleanRoutine;
    }

    private List<Routine> removeModified(List<Routine> routines) {
        List<Routine> deleted = PreferenceGetter.getDeletedRoutine();
        Map<Long, Routine> modified = PreferenceGetter.getModifiedRoutine();
        for (int i = 0; i < routines.size(); i++) {
            if (deleted.contains(routines.get(i))) {
                routines.remove(i);
                i--;
                continue;
            }
            if (modified.get(routines.get(i).getId()) != null) {
                routines.remove(i);
                i--;
            }
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

    boolean upgrade(bd.edu.daffodilvarsity.classorganizer.model.Database database) {
        try {
            //check version
            if (PreferenceGetter.getDatabaseVersion() >= database.getDatabaseVersion()) {
                return false;
            }
            //clean previous records
            routineAccess().nukeRoutines();
            semesterAccess().nukeSemesters();
            teacherAccess().nukeTeachers();
            //insert routine
            populateData(database);
            //upgrade modifications
            upgradeModifications();
            return true;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return false;
        }
    }

    private long getId(Routine routine) {
        List<Routine> routineList = routineAccess().searchRoutine(routine.getCampus(), routine.getDepartment(), routine.getProgram(), routine.getTeachersInitial(), routine.getLevel(), routine.getTerm(), routine.getSection());
        int index = routineList.indexOf(routine);
        if (index > -1) {
            return routineList.get(index).getId();
        }
        return -1;
    }

    @SuppressLint("UseSparseArrays")
    private void upgradeModifications() {
        Map<Long, Routine> modifiedOriginals = PreferenceGetter.getModifiedRoutineOriginal();
        Map<Long, Routine> modifiedRoutines = PreferenceGetter.getModifiedRoutine();
        List<Routine> saved = PreferenceGetter.getSavedRoutine();

        Map<Long, Routine> newModifiedOriginals = new HashMap<>();
        Map<Long, Routine> newModifiedRoutines = new HashMap<>();
        List<Long> keys = new ArrayList<>(modifiedOriginals.keySet());

        for (int i = 0; i < keys.size(); i++) {
            Routine original = modifiedOriginals.get(keys.get(i));
            Routine modified = modifiedRoutines.get(keys.get(i));
            long newId = getId(original);
            if (newId != -1) {
                try {
                    original.setId(newId);
                    modified.setId(newId);
                    newModifiedOriginals.put(newId, original);
                    newModifiedRoutines.put(newId, modified);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                saved.add(modified);
            }
        }

        List<Routine> deleted = PreferenceGetter.getDeletedRoutine();
        for (int i = 0; i < deleted.size(); i++) {
            long id = getId(deleted.get(i));
            if (id != -1) {
                deleted.get(i).setId(id);
            } else {
                deleted.remove(i);
                i--;
            }
        }
        PreferenceGetter.setDeletedRoutine(deleted);
        PreferenceGetter.setModifiedRoutine(newModifiedRoutines);
        PreferenceGetter.setModifiedRoutineOriginal(newModifiedOriginals);
        PreferenceGetter.setSavedRoutine(saved);
    }

    void mutifyRoutine(Routine originalRoutine) {
        Log.e(TAG, "mutifyRoutine: OriginaL "+originalRoutine.isMuted() );
        modifyRoutine(originalRoutine, mutify(originalRoutine));
    }

    private Routine mutify(Routine original) {
        Routine routine = new Routine();
        routine.setId(original.getId());
        routine.setCampus(original.getCampus());
        routine.setDepartment(original.getDepartment());
        routine.setProgram(original.getProgram());
        routine.setCourseCode(original.getCourseCode());
        routine.setCourseTitle(original.getCourseTitle());
        routine.setTeachersInitial(original.getTeachersInitial());
        routine.setSection(original.getSection());
        routine.setLevel(original.getLevel());
        routine.setTerm(original.getTerm());
        routine.setRoomNo(original.getRoomNo());
        routine.setTime(original.getTime());
        routine.setDay(original.getDay());
        routine.setTimeWeight(original.getTimeWeight());
        routine.setAltTime(original.getAltTime());
        routine.setAltTimeWeight(original.getAltTimeWeight());
        routine.setMuted(!original.isMuted());
        return routine;
    }

    void modifyRoutine(@NonNull Routine originalRoutine, @NonNull Routine modifiedRoutine) {
        if (originalRoutine.equals(modifiedRoutine)) {
            if (originalRoutine.isMuted() == modifiedRoutine.isMuted()) {
                return;
            }
        }
        List<Routine> savedRoutines = PreferenceGetter.getSavedRoutine();
        if (savedRoutines.contains(originalRoutine)) {
            int index = savedRoutines.indexOf(originalRoutine);
            savedRoutines.set(index, modifiedRoutine);
            PreferenceGetter.setSavedRoutine(savedRoutines);
            return;
        }
        Map<Long, Routine> modifiedRoutines = PreferenceGetter.getModifiedRoutine();
        Map<Long, Routine> originalMods = PreferenceGetter.getModifiedRoutineOriginal();
        Routine storedOriginal = originalMods.get(originalRoutine.getId());
        if (storedOriginal == null) {
            modifiedRoutines.put(originalRoutine.getId(), modifiedRoutine);
            originalMods.put(originalRoutine.getId(), originalRoutine);
        } else {
            modifiedRoutines.put(originalRoutine.getId(), modifiedRoutine);
        }
        PreferenceGetter.setModifiedRoutine(modifiedRoutines);
        PreferenceGetter.setModifiedRoutineOriginal(originalMods);
    }

    void deleteRoutine(Routine routine) {
        List<Routine> saved = PreferenceGetter.getSavedRoutine();
        if (saved.contains(routine)) {
            saved.remove(routine);
            PreferenceGetter.setSavedRoutine(saved);
            return;
        }
        deleteModifiedRoutine(routine);
        List<Routine> deleted = PreferenceGetter.getDeletedRoutine();
        deleted.add(routine);
        PreferenceGetter.setDeletedRoutine(deleted);
    }

    private void deleteModifiedRoutine(Routine routine) {
        Map<Long, Routine> original = PreferenceGetter.getModifiedRoutineOriginal();
        Map<Long, Routine> modified = PreferenceGetter.getModifiedRoutine();
        for (Map.Entry<Long, Routine> entry : modified.entrySet()) {
            if (routine.equals(entry.getValue())) {
                long id = entry.getKey();
                original.remove(id);
                modified.remove(id);
                break;
            }
        }
        PreferenceGetter.setModifiedRoutine(modified);
        PreferenceGetter.setModifiedRoutineOriginal(original);
    }
}
