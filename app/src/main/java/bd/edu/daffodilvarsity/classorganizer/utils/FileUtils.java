package bd.edu.daffodilvarsity.classorganizer.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * Created by Mushfiqus Salehin on 10/16/2017.
 * musfiqus@gmail.com
 */
public final class FileUtils {
    private static final String TAG = "FileUtils";

    public static void dbDownloader(String dlURL, String filePath) throws Exception {
        File downloadFile = new File(filePath);
        if (downloadFile.exists()) {
            downloadFile.delete();
        }
        downloadFile.createNewFile();
        if (dlURL != null) {
            URL downloadURL = new URL(dlURL);
            HttpURLConnection conn = (HttpURLConnection) downloadURL
                    .openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200)
                throw new Exception("Error in connection");
            conn.connect();
            InputStream is = conn.getInputStream();
            FileOutputStream os = new FileOutputStream(downloadFile);
            byte buffer[] = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                // Write data to file
                os.write(buffer, 0, byteCount);
            }
            os.close();
            is.close();
        } else {
            throw new Exception("Error parsing null URL");
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(destFile);
            source = is.getChannel();
            destination = os.getChannel();

            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size)
                ;
        } catch (Exception ex) {
        } finally {
            if (source != null) {
                source.close();
            }
            if (is != null) {
                is.close();
            }
            if (destination != null) {
                destination.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    public static String generateMasterOnlineDbPath(Context context, int dbVersion) {
        return context.getApplicationContext().getDatabasePath("masterdb_online_"+dbVersion+".db").getAbsolutePath();
    }

    public static String generateMasterOfflineDbPath(Context context, int dbVersion) {
        return context.getApplicationContext().getDatabasePath("masterdb_offline_"+dbVersion+".db").getAbsolutePath();
    }

    public static String getOnlineDbName(int dbVersion) {
        return "masterdb_online_"+dbVersion+".db";
    }

    public static String getOfflineDbName(int dbVersion) {
        return "masterdb_offline_"+dbVersion+".db";
    }

    public static void deleteMasterDb(Context context, boolean isOnline, int dbVersion) {
        Crashlytics.log("Deleting "+(isOnline ? "online ": "offline ")+" corrupt db version "+dbVersion);
        if (isOnline) {
            context.deleteDatabase(getOnlineDbName(dbVersion));
        } else {
            context.deleteDatabase(getOfflineDbName(dbVersion));
        }
        if (isOnline) {
            File deleteFile = new File(generateMasterOnlineDbPath(context, dbVersion));
            if (deleteFile.exists()) {
                File deleteJournal = new File(generateMasterOnlineDbPath(context, dbVersion)+"-journal");
                if (deleteJournal.exists()) {
                    deleteJournal.getAbsoluteFile().delete();
                }
                deleteFile.getAbsoluteFile().delete();
            }
        } else {
            File deleteFile = new File(generateMasterOfflineDbPath(context, dbVersion));
            if (deleteFile.exists()) {
                File deleteJournal = new File(generateMasterOfflineDbPath(context, dbVersion)+"-journal");
                if (deleteJournal.exists()) {
                    deleteJournal.getAbsoluteFile().delete();
                }
                deleteFile.getAbsoluteFile().delete();
            }
        }
    }

    public static void logAnError(Context context, String tag, String message, Exception exception) {
        PrefManager prefManager = new PrefManager(context);
        String errorDetails = null;
        //Get version
        String appVersionName;
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appVersionName = "App version not found";
        }
        errorDetails = "App version: "+appVersionName+"\n";
        errorDetails += "Database version: "+prefManager.getMasterDBVersion()+"\n";
        errorDetails += "Campus: "+prefManager.getCampus()+" Department: "+prefManager.getDept()+" Program: "+prefManager.getProgram()+"\n";
        if (prefManager.isUserStudent()) {
            errorDetails += "Level: "+prefManager.getLevel()+1+" Term: "+prefManager.getTerm()+1+" Section: "+prefManager.getSection()+"\n";
        } else {
            errorDetails += "Teacher's initial: "+prefManager.getTeacherInitial()+" Multi program: "+prefManager.isMultiProgram()+"\n";
        }
        errorDetails += "Message: "+message+"\n";
        if (exception != null) {
            errorDetails += "Stacktrace: \n"+exception.toString();
        }

        Crashlytics.log(1, tag, errorDetails);
    }
}
