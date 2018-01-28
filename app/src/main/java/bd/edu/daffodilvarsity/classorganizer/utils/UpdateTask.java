package bd.edu.daffodilvarsity.classorganizer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import bd.edu.daffodilvarsity.classorganizer.activity.MainActivity;
import bd.edu.daffodilvarsity.classorganizer.data.DayData;

/**
 * Created by musfiqus on 1/9/2018.
 */

public class UpdateTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "UpdateTask";

    private WeakReference<MainActivity> activityReference;
    private WeakReference<Context> contextReference;

    public UpdateTask(MainActivity activity, Context context) {
        activityReference = new WeakReference<>(activity);
        contextReference = new WeakReference<>(context);
    }

    //Calculates the new level and term upon a new semester routine
    private void setLevelTermOnUpgrade(PrefManager prefManager) {
        int currentLevel = prefManager.getLevel();
        int currentTerm = prefManager.getTerm();
        if (currentTerm == 2) {
            if (currentLevel < 3) {
                currentLevel++;
                prefManager.saveLevel(currentLevel);
            }
            currentTerm = 0;
            prefManager.saveTerm(currentTerm);
        } else {
            currentTerm++;
            prefManager.saveTerm(currentTerm);
        }
    }

    private boolean upgradeRoutine(Context context, boolean isUpgrade, final int dbVersion, boolean loadPersonal) {
        PrefManager prefManager = new PrefManager(context);
        RoutineLoader routineLoader;
        if (isUpgrade) {
            // upgrades it
            if (prefManager.isUserStudent()) {
                setLevelTermOnUpgrade(prefManager);
                //Deleting modified data before loading new semester routine
                prefManager.resetModification(true, true, true, true);
                boolean loadCheck = upgradeRoutine(context, false, dbVersion, false);
                if (!loadCheck) {
                    prefManager.setSemesterCount(CourseUtils.getInstance(context).getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    prefManager.saveSemester(CourseUtils.getInstance(context).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                    ArrayList<DayData> updatedRoutine = routineLoader.loadRoutine(false);
                    prefManager.saveDayData(updatedRoutine);
                    return false;
                }
                return true;
            } else {
                //Deleting modified data before loading new semester routine
                prefManager.resetModification(true, true, true, true);
                boolean loadCheck = upgradeRoutine(context, false, dbVersion, false);
                if (!loadCheck) {
                    prefManager.setSemesterCount(CourseUtils.getInstance(context).getSemesterCount(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    prefManager.saveSemester(CourseUtils.getInstance(context).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()));
                    routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context);
                    ArrayList<DayData> updatedRoutine = routineLoader.loadRoutine(false);
                    prefManager.saveDayData(updatedRoutine);
                    return false;
                }
                return true;
            }
        } else {
            //Simple update function loads new routine if db version changes
            if (prefManager.isUserStudent()) {
                routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
            } else {
                routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context);
            }
            ArrayList<DayData> updatedRoutine = routineLoader.loadRoutine(loadPersonal);
            if (updatedRoutine != null) {
                if (updatedRoutine.size() > 0) {
                    prefManager.saveDayData(updatedRoutine);
                    prefManager.setMasterDbVersion(dbVersion);
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected String doInBackground(String... params) {
        //params: online/offline, db version
        //Simple update function loads new routine if db version changes
        Context context = contextReference.get();
        if (context != null && params != null && params[0] != null && params[1] != null) {
            boolean isOnline = params[0].contains("online");
            int dbVersion = Integer.parseInt(params[1]);
            PrefManager prefManager = new PrefManager(context);
            boolean isUpgrade;
            boolean isSuccessful;
            if (isOnline) {
                boolean prevUpdateValue = prefManager.isUpdatedOnline();
                int prevDatabaseValue = prefManager.getMasterDBVersion();
                prefManager.setUpdatedOnline(true);
                prefManager.setMasterDbVersion(dbVersion);
                prefManager.incrementDatabaseVersion();
                RoutineLoader routineLoader;
                if (prefManager.isUserStudent()) {
                    routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                } else {
                    routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context);
                }
                boolean isVerified = routineLoader.verifyUpdatedDb();
                if (isVerified) {
                    prefManager.setUpdatedOnline(true);
                    if (prefManager.isUserStudent()) {
                        routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                    } else {
                        routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context);
                    }
                    if (routineLoader.isNewSemesterAvailable()) {
                        isSuccessful = !upgradeRoutine(context, true, dbVersion, false);
                        isUpgrade = true;
                        Log.e(TAG, "Online new Semester");
                    } else {
                        isSuccessful = !upgradeRoutine(context, false, dbVersion, true);
                        isUpgrade = false;
                        Log.e(TAG, "Online new Semester");
                    }
                    //Delete previous db
                    FileUtils.deleteMasterDb(context, true, prefManager.getOnlineDbVersion());
                    prefManager.saveOnlineDbVersion(dbVersion);
                } else {
                    //Delete newly downloaded db
                    FileUtils.deleteMasterDb(context, true, dbVersion);
                    prefManager.setUpdatedOnline(prevUpdateValue);
                    prefManager.setMasterDbVersion(prevDatabaseValue);
                    prefManager.incrementDatabaseVersion();
                    isSuccessful = false;
                    isUpgrade = false;
                }
            } else {
                //If there is a new routine, update
                prefManager.setUpdatedOnline(false);
                prefManager.incrementDatabaseVersion();
                RoutineLoader routineLoader;
                if (prefManager.isUserStudent()) {
                    routineLoader = new RoutineLoader(prefManager.getLevel(), prefManager.getTerm(), prefManager.getSection(), context, prefManager.getDept(), prefManager.getCampus(), prefManager.getProgram());
                } else {
                    routineLoader = new RoutineLoader(prefManager.getTeacherInitial(), prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram(), context);
                }
                if (routineLoader.isNewSemesterAvailable()) {
                    isSuccessful = !upgradeRoutine(context, true, MasterDBOffline.OFFLINE_DATABASE_VERSION, false);
                    isUpgrade = true;
                } else {
                    isSuccessful = !upgradeRoutine(context, false, MasterDBOffline.OFFLINE_DATABASE_VERSION, true);
                    isUpgrade = false;
                }
                FileUtils.deleteMasterDb(context, false, prefManager.getOfflineDbVersion());
                prefManager.saveOfflineDbVersion(MasterDBOffline.OFFLINE_DATABASE_VERSION);
            }
            StringBuilder resultStr = new StringBuilder();
            if (isSuccessful) {
                if (isOnline) {
                    resultStr.append("online ");
                } else {
                    resultStr.append("offline ");
                }
                if (isUpgrade) {
                    resultStr.append("upgrade ");
                } else {
                    resultStr.append("update");
                }
                resultStr.append("success");
                return resultStr.toString();
            } else {
                if (isOnline) {
                    resultStr.append("online ");
                } else {
                    resultStr.append("offline ");
                }
                if (isUpgrade) {
                    resultStr.append("upgrade ");
                } else {
                    resultStr.append("update");
                }
                resultStr.append("failed");
                return resultStr.toString();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e(TAG, "pOST");
        if (result != null) {
            Log.e(TAG, "Result: "+result);
            boolean isUpgrade = result.contains("upgrade");
            boolean isSuccessful = result.contains("success");
            boolean isOnline = result.contains("online");
            final MainActivity activity = activityReference.get();
            if (activity != null && activity.isActivityRunning()) {
                if (isSuccessful) {
                    if (isUpgrade) {
                        Log.e(TAG, "Upgraded");
                        PrefManager prefManager = new PrefManager(activity.getApplicationContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("New Semester!");
                        builder.setMessage("The routine was updated as per " + CourseUtils.getInstance(activity).getCurrentSemester(prefManager.getCampus(), prefManager.getDept(), prefManager.getProgram()) + " semester.\n" +
                                "Note: Your level and term will automatically get updated based on current selection and your modifications will be reset.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.updateData();
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        activity.updateData();
                        activity.showSnackBar(activity, "Routine updated");
                    }

                } else {
                    if (activity.isActivityRunning()) {
                        activity.showSnackBar(activity, "Error loading updated routine!");
                    }
                }
            } else if (activity != null && !activity.isActivityRunning()) {
                PrefManager prefManager = new PrefManager(activity.getApplicationContext());
                if (isSuccessful) {
                    prefManager.saveShowSnack(true);
                    prefManager.saveSnackData("Routine updated");
                } else {
                    prefManager.saveShowSnack(true);
                    prefManager.saveSnackData( "Error loading updated routine!");
                }
            }
        }
    }
}
