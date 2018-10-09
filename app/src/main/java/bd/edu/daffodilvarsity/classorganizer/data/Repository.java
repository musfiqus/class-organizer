package bd.edu.daffodilvarsity.classorganizer.data;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.model.Teacher;
import bd.edu.daffodilvarsity.classorganizer.model.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.ui.setup.SetupViewModel;
import bd.edu.daffodilvarsity.classorganizer.utils.FileUtils;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private static String BASE_URL = "https://raw.githubusercontent.com/musfiqus/musfiqus.github.io/master/routinedb/";

    private static final String TAG = "Repository";

    private ClassOrganizerApi api;

    private static Repository sInstance;

    public static synchronized Repository getInstance() {
        if (sInstance == null) {
            sInstance = new Repository();
        }
        return sInstance;
    }

    private Repository() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        api = retrofit.create(ClassOrganizerApi.class);
    }

    private ClassOrganizerDatabase getDatabase() {
        return ClassOrganizerDatabase.getInstance();
    }

    public Single<UpdateResponse> getUpdateResponse() {
        return api.getUpdate();
    }

    public Single<Database> getRoutineFromServer() {
        return api.getRoutine();
    }

    public Single<List<String>> getCampuses() {
        return Single.fromCallable(() -> getDatabase().routineAccess().getCampuses());
    }

    public Single<Boolean> upgradeDatabaseFromResponse(Database database) {
        return Single.fromCallable(() -> getDatabase().upgrade(database));
    }

    public Single<List<String>> getTeachersInitials(String campus, String department) {
        return Single.fromCallable(() -> getDatabase().getTeachersInitialsFromDb(campus, department));
    }

    public Single<List<String>> getDepartments(String campus) {
        return Single.fromCallable(() -> getDatabase().routineAccess().getDepartments(campus));
    }

    public Single<List<String>> getPrograms(String campus, String department) {
        return Single.fromCallable(() -> getDatabase().routineAccess().getPrograms(campus, department));
    }

    public Single<List<String>> getSections(String campus, String department, String program, int level, int term) {
        return Single.fromCallable(() -> getDatabase().getSectionsFromDb(campus, department, program, level, term));
    }

    public Single<List<String>> getSections(int level, int term) {
        return Single.fromCallable(() -> getDatabase().getSections(level, term));
    }

    public Single<List<Routine>> getRoutine(@NonNull String userType, String campus, String department, String program, int level, int term, String section, String teachersInitial) {
        if (userType.equals(SetupViewModel.USER_STUDENT)) {
            return Single.fromCallable(() -> getDatabase().routineAccess().getRoutineStudent(campus, department, program, level, term, section));
        } else {
            return Single.fromCallable(() -> getDatabase().routineAccess().getRoutineTeacher(campus, department, teachersInitial));
        }
    }

    public Single<List<Routine>> searchRoutine(int level, int term, String section) {
        return Single.fromCallable(() -> getDatabase().getSearchRoutineResults(level, term, section));
    }

    public Single<List<Routine>> getSavedRoutine() {
        return Single.fromCallable(() -> getDatabase().loadRoutineFromDb());
    }

    public Single<List<Routine>> searchFreeRooms(String time, String day) {
        return Single.fromCallable(() -> getDatabase().getFreeRooms(time, day));
    }

    public Single<List<String>> getTimeList() {
        return Single.fromCallable(() -> getDatabase().getTimeListFromDb());
    }

    public Single<List<Routine>> searchRoutineByInitial(String teacherInitial) {
        return Single.fromCallable(() -> getDatabase().getRoutineByInitial(teacherInitial));
    }

    public Single<Semester> getSemesterFromDb() {
        return Single.fromCallable(() -> getDatabase().getSemester());
    }

    public Completable modifyRoutine(Routine originalRoutine, Routine modifiedRoutine) {
        return Completable.fromAction(() -> getDatabase().modifyRoutine(originalRoutine, modifiedRoutine));
    }

    public Completable addRoutine(Routine routine) {
        return PreferenceGetter.addRoutine(routine);
    }

    public Single<List<String>> getSections() {
        return Single.fromCallable(() -> getDatabase().routineAccess().getSections(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment()));
    }

    public Single<List<String>> getCourseCodes() {
        return Single.fromCallable(() -> getDatabase().routineAccess().getCourseCodes(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment()));
    }

    public Single<List<String>> getCourseTitles() {
        return Single.fromCallable(() -> getDatabase().routineAccess().getCourseTitles(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment()));
    }

    public Single<List<String>> getRooms() {
        return Single.fromCallable(() -> getDatabase().routineAccess().getRooms(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment()));
    }

    public Single<RoutineDao.TimePOJO> getTImePOJO(String time, boolean isRamadanEnabled) {
        if (isRamadanEnabled) {
            return Single.fromCallable(() -> getDatabase().routineAccess().getTimeByAltTime(time));
        } else {
            return Single.fromCallable(() -> getDatabase().routineAccess().getTimeByTime(time));
        }
    }

    public Completable mutifyRoutine(Routine routine) {
        return Completable.fromAction(() -> getDatabase().mutifyRoutine(routine));
    }

    public Completable deleteRoutine(Routine routine) {
        return Completable.fromAction(() -> getDatabase().deleteRoutine(routine));
    }

    public Single<Teacher> getTeacherDetails(String teachersInitial) {
        return Single.fromCallable(() -> getDatabase().teacherAccess().getTeacher(teachersInitial));
    }

    public Single<Database> getDummyDb() {
        Database database = new Database();
        database.setDatabaseVersion(0);
        return Single.just(database);
    }

    private Flowable<ClassOrganizerDatabase> getDatabaseAsync() {
        return Flowable.fromCallable(this::getDatabase);
    }

    public Flowable<List<Routine>> getRoutineChangeNotifier() {
        return getDatabaseAsync()
                .flatMap(classOrganizerDatabase -> PreferenceGetter.isStudent() ?
                        classOrganizerDatabase.routineAccess().flowableRoutineStudent(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), PreferenceGetter.getProgram(), PreferenceGetter.getLevel(), PreferenceGetter.getTerm(), PreferenceGetter.getSection()) :
                        classOrganizerDatabase.routineAccess().flowableRoutineTeacher(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment(), PreferenceGetter.getInitial()) );

    }



}
