package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import bd.edu.daffodilvarsity.classorganizer.R;
import bd.edu.daffodilvarsity.classorganizer.activity.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.activity.SettingsActivity;
import bd.edu.daffodilvarsity.classorganizer.activity.WelcomeActivity;
import bd.edu.daffodilvarsity.classorganizer.data.UpdateResponse;
import bd.edu.daffodilvarsity.classorganizer.service.UpdateService;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateGetter {

    private static final int CONTEXT_MAIN = 0;
    private static final int CONTEXT_SETTINGS = 1;
    private static final int CONTEXT_WELCOME = 2;
    private static final int CONTEXT_OTHER = 3;

    private static final String TAG = "UpdateGetter";
    private static String BASE_URL = "https://raw.githubusercontent.com/musfiqus/musfiqus.github.io/master/routinedb/";

    private static UpdateGetter instance;
    private Context mContext;

    private ClassOrganizerApi updateService;
    private PrefManager prefManager;
    private int whichContext = -1;

    private String mNotificationTitle;
    private String mNotificationMessage;

    private WeakReference<MainActivity> mainActivityWeakReference;
    private WeakReference<SettingsActivity> settingsActivityWeakReference;
    private WeakReference<WelcomeActivity> welcomeActivityWeakReference;


    private UpdateGetter(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        updateService = retrofit.create(ClassOrganizerApi.class);
        this.mContext = context.getApplicationContext();
        prefManager = new PrefManager(mContext);
        if (context instanceof MainActivity) {
            Log.d(TAG, "UpdateGetter: Main");
            mainActivityWeakReference = new WeakReference<>((MainActivity) context);
            whichContext = CONTEXT_MAIN;
        } else if (context instanceof SettingsActivity) {
            settingsActivityWeakReference = new WeakReference<>((SettingsActivity) context);
            Log.d(TAG, "UpdateGetter: Settings");
            whichContext = CONTEXT_SETTINGS;
        } else if (context instanceof WelcomeActivity) {
            welcomeActivityWeakReference = new WeakReference<>((WelcomeActivity) context);
            whichContext = CONTEXT_WELCOME;
            Log.d(TAG, "UpdateGetter: Welcome");
        } else {
            Log.e(TAG, "UpdateGetter: Nothing");
            mainActivityWeakReference = null;
            settingsActivityWeakReference = null;
            welcomeActivityWeakReference = null;
            whichContext = CONTEXT_OTHER;
        }

    }

    public static UpdateGetter getInstance(Context context) {
        if (instance == null) {
            instance = new UpdateGetter(context);
        }
        Log.d(TAG, "getInstance: ");
        return instance;
    }

    public DisposableSingleObserver<UpdateResponse> getUpdate() {
        Log.d(TAG, "getUpdate: Called");
        return updateService
                .getUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateResponse>() {
                    @Override
                    public void onSuccess(UpdateResponse updateResponse) {
                        Log.d(TAG, "onSuccess: Called");
                        Log.d(TAG, "onSuccess: Current DB Version: "+prefManager.getDatabaseVersion()+" JSON DB version: "+updateResponse.getVersion());
                        initUpdate(updateResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: \n" + e.toString(), e);
                        updateCheckFailed();
                    }
                });
    }

    public void initUpdate(UpdateResponse updateResponse) {
        Log.d(TAG, "initUpdate: Update initiated");
        if (updateResponse != null) {
            if (updateResponse.getVersion() > prefManager.getDatabaseVersion()
                    && updateResponse.getVersion() != prefManager.getSuppressedMasterDbVersion()) {
                showUpdatePrompt(updateResponse);
            } else {
                noUpdatePrompt();
            }
        } else {
            Toasty.error(mContext, mContext.getString(R.string.update_error_toast), Toast.LENGTH_SHORT, true).show();
        }
    }

    public void updateCheckFailed() {
        Toasty.error(mContext, mContext.getString(R.string.update_error_toast), Toast.LENGTH_SHORT, true).show();
        switch (whichContext) {
            case CONTEXT_WELCOME:
                WelcomeActivity welcomeActivity = welcomeActivityWeakReference.get();
                if (welcomeActivity != null && welcomeActivity.isActivityRunning) {
                    welcomeActivity.setStatusText(welcomeActivity.getResources().getString(R.string.update_check_error));
                    welcomeActivity.setProgressBar(UpdateService.UPDATE_FAILED);
                }
                break;
            case CONTEXT_MAIN: break;
            case CONTEXT_SETTINGS: break;
            case CONTEXT_OTHER: break;
            default: break;
        }

    }

    private void noUpdatePrompt() {
        Log.d(TAG, "noUpdatePrompt: No update available");
        switch (whichContext) {
            case CONTEXT_MAIN:
                 //MainActivity
                break;
            case CONTEXT_SETTINGS:
                Toasty.info(mContext, "Already on the latest version.", Toast.LENGTH_SHORT, true).show(); //SettingsActivity
                break;
            case CONTEXT_WELCOME:
                WelcomeActivity welcomeActivity = welcomeActivityWeakReference.get();
                if (welcomeActivity != null && welcomeActivity.isActivityRunning) {
                    welcomeActivity.noUpdateAvailable();
                }
                break;
            case CONTEXT_OTHER:
                break;
            default:
                Toasty.error(mContext, "How did this happen?", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void showUpdatePrompt(UpdateResponse updateResponse) {
        Log.d(TAG, "showUpdatePrompt: Showing update prompt");
        UpdateNotificationHelper notificationHelper = new UpdateNotificationHelper(mContext, updateResponse);
        switch (whichContext) {
            case CONTEXT_MAIN:
                showUpdateDialogue(updateResponse); //MainActivity
                break;
            case CONTEXT_SETTINGS:
                notificationHelper.showUpdateNotification(mNotificationTitle, mNotificationMessage); //SettingsActivity
                break;
            case CONTEXT_WELCOME:
                startUpdateForWelcomeActivity(updateResponse);
                break;
            case CONTEXT_OTHER:
                notificationHelper.showUpdateNotification(mNotificationTitle, mNotificationMessage);
                break;
            default:
                Toasty.error(mContext, "How did this happen?", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void startUpdateForSettingActivity() {
    }



    private void showUpdateDialogue(UpdateResponse updateResponse) {
        MainActivity activity = mainActivityWeakReference.get();
        if (activity != null && activity.isActivityRunning()) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                    .title("Update Available!")
                    .positiveText("YES")
                    .negativeText("NO")
                    .content("A new routine update is available. Do you want to download it now?")
                    .checkBoxPrompt("Don't remind again me for this update", false, (buttonView, isChecked) -> {
                        if (isChecked) {
                            prefManager.setSuppressedMasterDbVersion(updateResponse.getVersion());
                        } else {
                            prefManager.setSuppressedMasterDbVersion(0);
                        }
                    })
                    .onPositive((materialDialog, dialogAction) -> {
                        startUpdateForMainActivity(updateResponse);
                    });
            MaterialDialog dialog = builder.build();
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }

    }



    private void startUpdateForMainActivity(UpdateResponse updateResponse) {
        Intent intent = new Intent(mContext, UpdateService.class);
        intent.putExtra(UpdateService.TAG_UPDATE_RESPONSE, updateResponse);
        Log.e(TAG, "startUpdateForMainActivity: YOO");
        mContext.startService(intent);
    }

    private void startUpdateForWelcomeActivity(UpdateResponse updateResponse) {
        WelcomeActivity welcomeActivity = welcomeActivityWeakReference.get();
        if (welcomeActivity != null && welcomeActivity.isActivityRunning) {
            welcomeActivity.startDownload();
        }
        Intent intent = new Intent(mContext, UpdateService.class);
        intent.putExtra(UpdateService.TAG_UPDATE_RESPONSE, updateResponse);
        Log.d(TAG, "startUpdateForWelcomeActivity: YOO");
        mContext.startService(intent);
    }

    public void setNotificationTitle(String notificationTitle) {
        this.mNotificationTitle = notificationTitle;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.mNotificationMessage = notificationMessage;
    }
}
