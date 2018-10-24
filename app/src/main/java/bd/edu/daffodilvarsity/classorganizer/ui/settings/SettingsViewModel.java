package bd.edu.daffodilvarsity.classorganizer.ui.settings;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Semester;
import bd.edu.daffodilvarsity.classorganizer.utils.AlarmHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.InputHelper;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = "SettingsViewModel";
    private MutableLiveData<String> routineChangeListener;
    private String lastSettings;
    private MutableLiveData<String> semesterNameListener;
    private Repository repository = Repository.getInstance();
    private AlarmHelper alarmHelper = new AlarmHelper();
    private CompositeDisposable mDisposable;


    MutableLiveData<String> getRoutineChangeListener() {
        if (routineChangeListener == null) {
            routineChangeListener = new MutableLiveData<>();
        }
        refreshRoutineSettings();
        return routineChangeListener;
    }

    MutableLiveData<String> getSemesterNameListener() {
        if (semesterNameListener == null) {
            semesterNameListener = new MutableLiveData<>();
        }
        getSemesterName();
        return semesterNameListener;
    }

    void refreshRoutineSettings() {
        String settings = PreferenceGetter.isStudent() ?
                ClassOrganizer.getInstance().getString(R.string.routine_summary_student, InputHelper.capitalizeFirstLetter(PreferenceGetter.getCampus()), PreferenceGetter.getDepartment().toUpperCase(), InputHelper.capitalizeFirstLetter(PreferenceGetter.getProgram()), PreferenceGetter.getLevel()+1, PreferenceGetter.getTerm()+1, PreferenceGetter.getSection()) :
                ClassOrganizer.getInstance().getString(R.string.routine_summary_teacher, InputHelper.capitalizeFirstLetter(PreferenceGetter.getCampus()), InputHelper.upperCase(PreferenceGetter.getDepartment()), PreferenceGetter.getInitial());
        if (lastSettings == null) {
            lastSettings = settings;
            routineChangeListener.postValue(lastSettings);
        } else {
            if (!lastSettings.equals(settings)) {
                lastSettings = settings;
                routineChangeListener.postValue(lastSettings);
            }
        }
    }

    private void getSemesterName() {
        repository.
                getSemesterFromDb()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Semester>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        semesterNameListener.postValue("");
                    }

                    @Override
                    public void onSuccess(Semester semester) {
                        semesterNameListener.postValue(semester.getSemesterName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        semesterNameListener.postValue(ClassOrganizer.getInstance().getString(R.string.semester_error));

                    }
                });
    }

    private void addDisposable(Disposable disposable) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    void restartAlarms() {
        alarmHelper
                .restartAlarms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        Log.d(TAG, "onSubscribe: Alarm restart subscribed");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Alarms restarted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: Alarm restart failed", e);
                    }
                });
    }

    void cancelAlarms() {
        alarmHelper
                .cancelAllAlarms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        Log.d(TAG, "onSubscribe: Alarm cancel subscribed");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Alarms canceled");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: Alarm cancel failed", e);
                    }
                });
    }

    void startAlarms() {
        alarmHelper
                .startAllAlarms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        Log.d(TAG, "onSubscribe: Alarm start subscribed");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Alarms started");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: Alarm start failed", e);
                    }
                });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }



}
