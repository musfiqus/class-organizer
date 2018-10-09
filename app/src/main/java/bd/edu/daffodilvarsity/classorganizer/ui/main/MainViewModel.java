package bd.edu.daffodilvarsity.classorganizer.ui.main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.service.NotificationRestartJobIntentService;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import bd.edu.daffodilvarsity.classorganizer.utils.UpdatePollWorker;
import es.dmoral.toasty.Toasty;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.ResourceSubscriber;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";
    private MutableLiveData<Resource<Map<String, ArrayList<Routine>>>> routineListListener;
    private MutableLiveData<Boolean> classProgressListener;
    private MutableLiveData<Semester> semesterUpgradeDialogListener;

    private Repository mRepository;
    private CompositeDisposable mDisposable;
    private WorkManager workManager;

    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";
    public static final String MONDAY = "Monday";
    public static final String TUESDAY = "Tuesday";
    public static final String WEDNESDAY = "Wednesday";
    public static final String THURSDAY = "Thursday";
    public static final String FRIDAY = "Friday";

    private static final String UPDATE_POLL_WORKER_TAG = "update_poll";
    private static final String UPDATE_CHECK_WORK = "check_teh_f_out";

    public MainViewModel() {
        mRepository = Repository.getInstance();
    }

    MutableLiveData<Resource<Map<String, ArrayList<Routine>>>> getRoutineListListener() {
        if (routineListListener == null) {
            routineListListener = new MutableLiveData<>();
        }
        loadRoutine();
        return routineListListener;
    }

    MutableLiveData<Boolean> getClassProgressListener() {
        if (classProgressListener == null) {
            classProgressListener = new MutableLiveData<>();
        }
        return classProgressListener;
    }

    MutableLiveData<Semester> getSemesterUpgradeDialogListener() {
        if (semesterUpgradeDialogListener == null) {
            semesterUpgradeDialogListener = new MutableLiveData<Semester>();
        }
        return semesterUpgradeDialogListener;
    }

    void loadRoutine() {
        mRepository
                .getSavedRoutine()
                .flatMap(routines -> Single.fromCallable(() -> getSortedRoutine(routines)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Map<String, ArrayList<Routine>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        routineListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(Map<String, ArrayList<Routine>> stringArrayListMap) {
                        routineListListener.postValue(new Resource<>(Status.SUCCESSFUL, stringArrayListMap, null));

                    }

                    @Override
                    public void onError(Throwable e) {
                        routineListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });

    }


    private Map<String, ArrayList<Routine>> getSortedRoutine(List<Routine> routines) {
        ArrayList<Routine> satDayData = new ArrayList<>();
        ArrayList<Routine> sunDayData = new ArrayList<>();
        ArrayList<Routine> monDayData = new ArrayList<>();
        ArrayList<Routine> tueDayData = new ArrayList<>();
        ArrayList<Routine> wedDayData = new ArrayList<>();
        ArrayList<Routine> thuDayData = new ArrayList<>();
        ArrayList<Routine> friDayData = new ArrayList<>();

        for (Routine eachDay : routines) {
            if (eachDay != null && eachDay.getDay() != null) {
                if (eachDay.getDay().equalsIgnoreCase(SATURDAY)) {
                    satDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(SUNDAY)) {
                    sunDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(MONDAY)) {
                    monDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(TUESDAY)) {
                    tueDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(WEDNESDAY)) {
                    wedDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(THURSDAY)) {
                    thuDayData.add(eachDay);
                } else if (eachDay.getDay().equalsIgnoreCase(FRIDAY)) {
                    friDayData.add(eachDay);
                }
            }
        }
        Collections.sort(satDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(sunDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(monDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(tueDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(wedDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(thuDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));
        Collections.sort(friDayData, (o1, o2) -> Double.valueOf(o1.getTimeWeight()).compareTo(Double.valueOf(o2.getTimeWeight())));

        Map<String, ArrayList<Routine>> arrayListMap = new HashMap<>();
        arrayListMap.put(SATURDAY, satDayData);
        arrayListMap.put(SUNDAY, sunDayData);
        arrayListMap.put(MONDAY, monDayData);
        arrayListMap.put(TUESDAY, tueDayData);
        arrayListMap.put(WEDNESDAY, wedDayData);
        arrayListMap.put(THURSDAY, thuDayData);
        arrayListMap.put(FRIDAY, friDayData);
        return arrayListMap;
    }

    void deleteRoutine(Routine routine) {
        mRepository
                .deleteRoutine(routine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getClassProgressListener().postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        getClassProgressListener().postValue(false);
                        Toasty.info(ClassOrganizer.getInstance(), routine.getCourseCode() + " has been deleted", Toast.LENGTH_SHORT, false).show();
                        loadRoutine();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getClassProgressListener().postValue(false);
                        Toasty.error(ClassOrganizer.getInstance(), "Failed to delete " + routine.getCourseCode(), Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    void modifyRoutine(Routine originalRoutine) {
        mRepository
                .mutifyRoutine(originalRoutine)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getClassProgressListener().postValue(true);
                    }

                    @Override
                    public void onComplete() {
                        getClassProgressListener().postValue(false);
                        if (originalRoutine.isMuted()) {
                            Toasty.info(ClassOrganizer.getInstance(), originalRoutine.getCourseCode() + " has been muted", Toast.LENGTH_SHORT, false).show();
                        } else {
                            Toasty.info(ClassOrganizer.getInstance(), originalRoutine.getCourseCode() + " has been unmuted", Toast.LENGTH_SHORT, false).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getClassProgressListener().postValue(false);
                        Toasty.error(ClassOrganizer.getInstance(), "Failed to mute " + originalRoutine.getCourseCode(), Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    void receiveDbChanges() {
        Disposable disposable = mRepository
                .getRoutineChangeNotifier()
                .onBackpressureLatest()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(new DisposableSubscriber<List<Routine>>() {
                    @Override
                    public void onNext(List<Routine> routines) {
                        checkForNewSemester();
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.e(TAG, "receiveDbChanges onError: ", t);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        addDisposable(disposable);
    }

    void enqueueWorks() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ClassOrganizer.getInstance());
        boolean hasNotification = preferences.getBoolean("notification_preference", true);
        if (hasNotification) {
            NotificationRestartJobIntentService.enqueueWork(ClassOrganizer.getInstance(), new Intent(ClassOrganizer.getInstance(), NotificationRestartJobIntentService.class));
        }
        workManager = WorkManager.getInstance();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdatePollWorker.class, 6, TimeUnit.HOURS)
                .addTag(UPDATE_POLL_WORKER_TAG)
                .setConstraints(constraints)
                .build();
        workManager.enqueueUniquePeriodicWork(UPDATE_CHECK_WORK, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    private void checkForNewSemester() {
        mRepository
                .getSemesterFromDb()
                .flatMapCompletable(semester -> Completable.fromAction(() -> semesterAction(semester)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Default semesterid set");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: Error setting semesterid", e);
                    }
                });
    }

    private void semesterAction(Semester semester) {
        int currentSemester = PreferenceGetter.getSemesterId();
        if (currentSemester <= 0 ) {
            PreferenceGetter.setSemesterId(semester.getSemesterID());
        } else if (currentSemester < semester.getSemesterID()) {
            getSemesterUpgradeDialogListener().postValue(semester);
        }
    }


    private void addDisposable(Disposable disposable) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
