package bd.edu.daffodilvarsity.classorganizer.ui.modify;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;

import bd.edu.daffodilvarsity.classorganizer.ClassOrganizer;
import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.data.Repository;
import bd.edu.daffodilvarsity.classorganizer.data.RoutineDao;
import bd.edu.daffodilvarsity.classorganizer.model.Resource;
import bd.edu.daffodilvarsity.classorganizer.model.Routine;
import bd.edu.daffodilvarsity.classorganizer.model.Status;
import bd.edu.daffodilvarsity.classorganizer.utils.CustomFilterArrayAdapter;
import bd.edu.daffodilvarsity.classorganizer.utils.PreferenceGetter;
import es.dmoral.toasty.Toasty;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ModifyViewModel extends ViewModel {
    private static final String TAG = "ModifyViewModel";
    private CompositeDisposable mDisposable;
    private MutableLiveData<Routine> mEditDataListener;
    private MutableLiveData<Resource<AdapterModel>> mAdapterListener;
    private Repository mRepository = Repository.getInstance();
    private MutableLiveData<Boolean> classProgressListener;
    private MutableLiveData<Boolean> mModificationListener;

    public ModifyViewModel() {
    }

    MutableLiveData<Routine> getEditDataListener() {
        if (mEditDataListener == null) {
            mEditDataListener = new MutableLiveData<>();
        }
        return mEditDataListener;
    }

    MutableLiveData<Resource<AdapterModel>> getAdapterListener() {
        if (mAdapterListener == null) {
            mAdapterListener = new MutableLiveData<>();
        }
        return mAdapterListener;
    }

    MutableLiveData<Boolean> getClassProgressListener() {
        if (classProgressListener == null) {
            classProgressListener = new MutableLiveData<>();
        }
        return classProgressListener;
    }

    public MutableLiveData<Boolean> getModificationListener() {
        if (mModificationListener == null) {
            mModificationListener = new MutableLiveData<>();
        }
        return mModificationListener;
    }

    void loadAdapters(String currentTime) {
        getAdapterModel(currentTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AdapterModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                        getClassProgressListener().postValue(true);
                        getAdapterListener().postValue(new Resource<>(Status.LOADING, null, null));
                    }

                    @Override
                    public void onSuccess(AdapterModel adapterModel) {
                        getClassProgressListener().postValue(false);
                        getAdapterListener().postValue(new Resource<>(Status.SUCCESSFUL, adapterModel, null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        getClassProgressListener().postValue(false);
                        getAdapterListener().postValue(new Resource<>(Status.ERROR, null, e));
                    }
                });
    }

    void modifyRoutine(Routine originalRoutine, Routine modifiedRoutine) {
        boolean isRamadanEnabled = PreferenceGetter.isRamadanEnabled();
        mRepository
                .getTImePOJO(isRamadanEnabled ? modifiedRoutine.getAltTime() : modifiedRoutine.getTime(), isRamadanEnabled)
                .flatMap(timePOJO -> Single.fromCallable(() -> mergeTimePOJO(timePOJO, modifiedRoutine)))
                .flatMapCompletable(routine1 -> mRepository.modifyRoutine(originalRoutine, routine1))
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
                        getModificationListener().postValue(true);
                        Toasty.info(ClassOrganizer.getInstance(), "Saved", Toast.LENGTH_SHORT, false).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "modifyRoutine onError: e", e);
                        getClassProgressListener().postValue(false);
                        getModificationListener().postValue(true);
                        Toasty.error(ClassOrganizer.getInstance(), "Failed to save", Toast.LENGTH_SHORT, false).show();
                    }
                });
    }

    void addRoutine(Routine routine) {
        boolean isRamadanEnabled = PreferenceGetter.isRamadanEnabled();
        mRepository
                .getTImePOJO(isRamadanEnabled ? routine.getAltTime() : routine.getTime(), isRamadanEnabled)
                .flatMap(timePOJO -> Single.fromCallable(() -> mergeTimePOJO(timePOJO, routine)))
                .flatMapCompletable(routine1 -> mRepository.addRoutine(routine))
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
                        getModificationListener().postValue(true);
                        Toasty.info(ClassOrganizer.getInstance(), "Added", Toast.LENGTH_SHORT, false).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        getClassProgressListener().postValue(false);
                        getModificationListener().postValue(false);
                        Toasty.error(ClassOrganizer.getInstance(), "Failed to add", Toast.LENGTH_SHORT, false).show();
                    }
                });
    }

    private Single<AdapterModel> getAdapterModel(String currentTime) {
         return Single.zip(
                mRepository.getCourseTitles(),
                mRepository.getCourseCodes(),
                mRepository.getTeachersInitials(PreferenceGetter.getCampus(), PreferenceGetter.getDepartment()),
                mRepository.getRooms(),
                mRepository.getSections(),
                mRepository.getTimeList(),
                 (titles, codes, initials, rooms, section, times) -> new AdapterModel(titles, codes, initials, rooms, section, times, currentTime)
         );
    }

    private Routine mergeTimePOJO(RoutineDao.TimePOJO timePOJO, Routine routine) {
        routine.setTime(timePOJO.getTime());
        routine.setTimeWeight(timePOJO.getTimeWeight());
        routine.setAltTime(timePOJO.getAltTime());
        routine.setAltTimeWeight(timePOJO.getAltTimeWeight());
        return routine;
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
        if (mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }

    public class AdapterModel {
        CustomFilterArrayAdapter titles;
        CustomFilterArrayAdapter codes;
        CustomFilterArrayAdapter teachers;
        CustomFilterArrayAdapter rooms;
        CustomFilterArrayAdapter sections;
        ArrayAdapter<String> times;
        int timePosition;

        public AdapterModel(List<String> titles, List<String> codes, List<String> teachers, List<String> rooms, List<String> sections, List<String> times, String currentTime) {
            this.titles = new CustomFilterArrayAdapter(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, titles);
            this.codes = new CustomFilterArrayAdapter(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, codes);
            this.teachers = new CustomFilterArrayAdapter(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, teachers);
            this.rooms = new CustomFilterArrayAdapter(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, rooms);
            this.sections = new CustomFilterArrayAdapter(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, sections);
            this.times = new ArrayAdapter<>(ClassOrganizer.getInstance(), R.layout.spinner_row_zero, times);
            this.times.setDropDownViewResource(R.layout.spinner_row);
            this.timePosition = calculateTimePosition(times, currentTime);
        }

        private int calculateTimePosition(List<String> times, String currentTime) {
            if (currentTime == null) {
                return 0;
            }
            for (int i =0; i < times.size(); i++) {
                if (currentTime.equalsIgnoreCase(times.get(i))) {
                    return i;
                }
            }
            return 0;
        }
    }


}
