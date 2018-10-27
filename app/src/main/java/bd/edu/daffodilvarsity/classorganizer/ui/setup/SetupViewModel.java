package bd.edu.daffodilvarsity.classorganizer.ui.setup;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.model.Database;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.model.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SetupViewModel extends ViewModel {
    private static final String TAG = "SetupViewModel";

    public static final String USER_STUDENT = "Student";
    public static final String USER_TEACHER = "Teacher";

    private Repository repository;

    private MutableLiveData<Resource<Boolean>> updateListener;
    private MutableLiveData<String> userTypeListener;
    private MutableLiveData<Resource<List<String>>> campusListListener;
    private MutableLiveData<Resource<List<String>>> teachersInitialListener;
    private MutableLiveData<Resource<List<String>>> departmentListListener;
    private MutableLiveData<Resource<List<String>>> programListListener;
    private MutableLiveData<Resource<List<String>>> sectionListListener;
    private MutableLiveData<Resource<Boolean>> validationListener;

    private MutableLiveData<String> selectedCampusListener;
    private MutableLiveData<Integer> selectedLevelListener;
    private MutableLiveData<Integer> selectedTermListener;
    private MutableLiveData<String> selectedInitialListener;
    private MutableLiveData<String> selectedSectionListener;
    private MutableLiveData<String> selectedDepartmentListener;
    private MutableLiveData<String> selectedProgramListener;


    private CompositeDisposable mDisposable;

    public SetupViewModel() {
        this.repository = Repository.getInstance();
    }

    void updateUserType(String userType) {
        getUserTypeListener().setValue(userType);
        PreferenceGetter.setUserType(userType);
    }

    void updateCampus(String campus) {
        getSelectedCampusListener().postValue(campus);
        PreferenceGetter.setCampus(campus);
    }

    void updateDepartment(String department) {
        getSelectedDepartmentListener().postValue(department);
        PreferenceGetter.setDepartment(department);
    }

    void updateProgram(String program) {
        getSelectedProgramListener().postValue(program);
        PreferenceGetter.setProgram(program);
    }



    void updateLevelTerm(Integer level, Integer term) {
        getSelectedLevelListener().postValue(level);
        getSelectedTermListener().postValue(term);
        PreferenceGetter.setLevel(level);
        PreferenceGetter.setTerm(term);
    }

    void updateSection(String section) {
        getSelectedSectionListener().postValue(section);
        PreferenceGetter.setSection(section);
    }

    void updateInitial(String initial) {
        getSelectedInitialListener().postValue(initial);
        PreferenceGetter.setInitial(initial);
    }

    public void validate() {
        Log.e(TAG, "validate: OKAY");
        int level = getLevel() == null ? 0: getLevel(), term = getTerm() == null ? 0: getTerm();
        repository
                .getRoutine(getUserType(), getCampus(), getDepartment(), getProgram(), level, term, getSection(), getInitial())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Routine>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getValidationListener().postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<Routine> routines) {
                        if (routines.size() > 0) {
                            getValidationListener().postValue(new Resource<>(Status.SUCCESSFUL, null, null));
                        } else {
                            getValidationListener().postValue(new Resource<>(Status.ERROR, null, null));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getValidationListener().postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    private void loadSections(String campus, String department, String program, int level, int term) {
        repository
                .getSections(campus, department, program, level, term)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        sectionListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> list) {
                        sectionListListener.postValue(new Resource<>(Status.SUCCESSFUL, list, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        sectionListListener.postValue(new Resource<>(Status.SUCCESSFUL, null, e));
                    }
                });
    }

    private void loadPrograms(String campus, String department) {
        repository
                .getPrograms(campus, department)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        programListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        programListListener.postValue(new Resource<>(Status.SUCCESSFUL, strings, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        programListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    private void loadDepartments(String campus) {
        repository
                .getDepartments(campus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        departmentListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        departmentListListener.postValue(new Resource<>(Status.SUCCESSFUL, strings, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        departmentListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    private void loadInitials(String campus, String department) {
        repository
                .getTeachersInitials(campus, department)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        teachersInitialListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        teachersInitialListener.postValue(new Resource<>(Status.SUCCESSFUL, strings, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        teachersInitialListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }


    private void loadCampuses() {
        repository
                .getCampuses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        campusListListener.postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(List<String> strings) {
                        campusListListener.postValue(new Resource<>(Status.SUCCESSFUL, strings, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        campusListListener.postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    private void checkForUpdates() {
        repository
                .getUpdateResponse()
                .doOnSuccess(updateResponse -> {
                    if (updateResponse.getVersion() > PreferenceGetter.getDatabaseVersion()) {
                        updateListener.postValue(new Resource<>(Status.UPDATING, false, null));
                    }
                })
                .flatMap(this::getDatabase)
                .flatMap(database -> repository.upgradeDatabaseFromResponse(database))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        updateListener.postValue(new Resource<>(Status.LOADING, false, null));
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        updateListener.postValue(new Resource<>(Status.SUCCESSFUL, aBoolean, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        updateListener.postValue(new Resource<>(Status.ERROR, false, null));
                    }
                });
    }

    private Single<Database> getDatabase(UpdateResponse updateResponse) {
        if (updateResponse.getVersion() > PreferenceGetter.getDatabaseVersion()) {
            return repository.getRoutineFromServer();
        } else {
            return repository.getDummyDb();
        }
    }

    private void addDisposable(Disposable disposable) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }


    MutableLiveData<Resource<Boolean>> getUpdateListener() {
        if (updateListener == null) {
            updateListener = new MutableLiveData<>();
            checkForUpdates();
        }
        return updateListener;
    }

    MutableLiveData<String> getUserTypeListener() {
        if (userTypeListener == null) {
            userTypeListener = new MutableLiveData<>();
        }
        return userTypeListener;
    }

    MutableLiveData<Resource<List<String>>> getCampusListListener() {
        if (campusListListener == null) {
            campusListListener = new MutableLiveData<>();
            loadCampuses();
        }
        return campusListListener;
    }

    MutableLiveData<String> getSelectedCampusListener() {
        if (selectedCampusListener == null) {
            selectedCampusListener = new MutableLiveData<>();
        }
        return selectedCampusListener;
    }


    MutableLiveData<Integer> getSelectedLevelListener() {
        if (selectedLevelListener == null) {
            selectedLevelListener = new MutableLiveData<>();
        }
        return selectedLevelListener;
    }

    MutableLiveData<Integer> getSelectedTermListener() {
        if (selectedTermListener == null) {
            selectedTermListener = new MutableLiveData<>();
        }
        return selectedTermListener;
    }

    MutableLiveData<String> getSelectedInitialListener() {
        if (selectedInitialListener == null) {
            selectedInitialListener = new MutableLiveData<>();
        }
        return selectedInitialListener;
    }

    MutableLiveData<Resource<List<String>>> getTeachersInitialListener(String campus, String department) {
        if (teachersInitialListener == null) {
            teachersInitialListener = new MutableLiveData<>();
            loadInitials(campus, department);
        }
        return teachersInitialListener;
    }

    MutableLiveData<String> getSelectedSectionListener() {
        if (selectedSectionListener == null) {
            selectedSectionListener = new MutableLiveData<>();
        }
        return selectedSectionListener;
    }

    MutableLiveData<String> getSelectedDepartmentListener() {
        if (selectedDepartmentListener == null) {
            selectedDepartmentListener = new MutableLiveData<>();
        }
        return selectedDepartmentListener;
    }

    MutableLiveData<String> getSelectedProgramListener() {
        if (selectedProgramListener == null) {
            selectedProgramListener = new MutableLiveData<>();

        }
        return selectedProgramListener;
    }

    MutableLiveData<Resource<List<String>>> getDepartmentListListener(String campus) {
        if (departmentListListener == null) {
            departmentListListener = new MutableLiveData<>();
        }
        loadDepartments(campus);
        return departmentListListener;
    }

    MutableLiveData<Resource<List<String>>> getProgramListListener(String campus, String department) {
        if (programListListener == null) {
            programListListener = new MutableLiveData<>();
        }
        loadPrograms(campus, department);
        return programListListener;
    }

    MutableLiveData<Resource<List<String>>> getSectionListListener(String campus, String department, String program, int level, int term) {
        if (sectionListListener == null) {
            sectionListListener = new MutableLiveData<>();
        }
        loadSections(campus, department, program, level, term);
        return sectionListListener;
    }

    MutableLiveData<Resource<Boolean>> getValidationListener() {
        if (validationListener == null) {
            validationListener = new MutableLiveData<>();
        }
        return validationListener;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    public String getCampus() {
        Log.e(TAG, "getCampus: "+getSelectedCampusListener().getValue());
        return getSelectedCampusListener().getValue();
    }

    public String getUserType() {
        Log.e(TAG, "getUserType: "+getUserTypeListener().getValue());
        return getUserTypeListener().getValue();
    }

    public String getDepartment() {
        Log.e(TAG, "getDepartment: "+getSelectedDepartmentListener().getValue());
        return getSelectedDepartmentListener().getValue();
    }

    public String getInitial() {
        Log.e(TAG, "getInitial: "+getSelectedInitialListener().getValue());
        return getSelectedInitialListener().getValue();
    }

    public String getProgram() {
        Log.e(TAG, "getProgram: "+getSelectedProgramListener().getValue());
        return getSelectedProgramListener().getValue();
    }

    public Integer getLevel() {
        Log.e(TAG, "getLevel: "+getSelectedLevelListener().getValue());
        return getSelectedLevelListener().getValue();
    }

    public Integer getTerm() {
        Log.e(TAG, "getTerm: "+getSelectedTermListener().getValue());
        return getSelectedTermListener().getValue();
    }

    public String getSection() {
        Log.e(TAG, "getSection: "+getSelectedSectionListener().getValue());
        return getSelectedSectionListener().getValue();
    }

}
